# -*- coding: utf-8 -*-
'''
Provides an Acumos model runner for DCAE
'''
import logging
import sys
from argparse import ArgumentParser
from threading import Thread, Event

from dcaeapplib import DcaeEnv
from acumos.wrapped import load_model


logging.basicConfig(stream=sys.stdout, level=logging.INFO, format='%(asctime)s - %(threadName)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)


_GETDATA_TIMEOUT_MS = 60000


class ModelRunnerError(Exception):
    '''ModelRunner base exception'''


class Dcae(object):
    '''Wrapper around DcaeEnv to allow decoupling between the env and model runner'''

    def __init__(self):
        self.env = DcaeEnv(self._health_check, self._on_config)
        self.config = self.env.getconfig()
        self.health_checks = []
        self.on_configs = []

    def _on_config(self):
        '''Invokes all on_config listeners with new configuration'''
        config = self.env.getconfig()
        for on_config in self.on_configs:
            on_config(config)

    def _health_check(self):
        '''Invokes all health check callbacks'''
        return False if not self.health_checks else all(hc() for hc in self.health_checks)


class ModelMethod(object):
    '''Manages the execution of a single model method'''

    def __init__(self, name, method, get_data, send_data, config, timeout):
        self.name = name
        self._sub_key = "{}_subscriber".format(name)
        self._pub_key = "{}_publisher".format(name)
        self._from_json = method.from_json
        self._get_data = get_data
        self._send_data = send_data
        self._config = config
        self._timeout = timeout

    def on_config(self, config):
        '''Callback to invoke when new config is available'''
        self._config = config

    def process(self):
        '''Performs a single sub/pub iteration'''
        input_json = self._get_data(self._sub_key, self._timeout)
        if input_json is None:
            raise TimeoutError('Timeout received while waiting for data')
        logger.debug('Received: %s', input_json)

        output_json = self._from_json(input_json).as_json()
        logger.debug('Sending: %s', output_json)
        self._send_data(self._pub_key, 'group1', output_json)


def run_method(method, event):
    '''Runs a method forever. To be used as a Thread target'''
    logger.info('Starting process loop')
    while event.is_set():
        try:
            method.process()
        except Exception as e:
            logger.error("Process failure: {}".format(e))
    logger.info('Exitting process loop')


class ModelRunner(object):
    '''Manages individual ModelMethod objects'''

    def __init__(self, model, get_data, send_data, config, timeout=_GETDATA_TIMEOUT_MS):
        self.event = Event()
        self._model = model
        self._methods = tuple(ModelMethod(name, method, get_data, send_data, config, timeout) for name, method in model.methods.items())
        self._threads = tuple()

    def health_check(self):
        '''Returns True if all model pub/sub threads are alive'''
        return False if not self._threads else all(t.is_alive() for t in self._threads)

    def on_config(self, config):
        '''Callback to invoke when new config is available'''
        for m in self._methods:
            m.on_config(config)

    def start(self):
        '''Creates and starts methods threads'''
        if self._threads:
            raise ModelRunnerError('ModelRunner.start has already been invoked')
        self.event.set()
        self._threads = tuple(Thread(target=run_method, args=(m, self.event), name="Model.{}".format(m.name))
                              for m in self._methods)
        for t in self._threads:
            t.start()

    def stop(self):
        '''Stops model method threads. Blocks until threads are joined'''
        self.event.clear()
        for t in self._threads:
            t.join()


def _init_runner(model_dir, timeout=_GETDATA_TIMEOUT_MS):
    '''Helper function which creates, configures, and returns model runner and DCAE objects'''
    dcae = Dcae()

    model = load_model(model_dir)
    runner = ModelRunner(model, dcae.env.getdata, dcae.env.senddata, dcae.config, timeout)

    dcae.health_checks.append(runner.health_check)
    dcae.on_configs.append(runner.on_config)

    return runner, dcae


def run_model():
    '''Command line level utility for creating and running a model runner'''
    parser = ArgumentParser()
    parser.add_argument('model_dir', type=str, help='Directory that contains either the dumped model.zip or its unzipped contents.')
    parser.add_argument('--timeout', default=_GETDATA_TIMEOUT_MS, type=int, help='Timeout (ms) used when fetching.')
    parser.add_argument('--debug', action='store_true', help='Sets the log level to DEBUG')
    pargs = parser.parse_args()

    if pargs.debug:
        logger.setLevel(logging.DEBUG)
        logger.info('Logging level set to DEBUG')

    logger.info('Creating DCAE environment and model runner')
    runner, dcae = _init_runner(pargs.model_dir, pargs.timeout)

    logger.info('Starting DCAE environment and model runner')
    dcae.env.start()
    runner.start()

    try:
        runner.event.wait()
    except KeyboardInterrupt:
        logger.info('Interrupt received. Stopping model runner and DCAE environment...')
        dcae.env.stop()
        runner.stop()
        logger.info('Stopped model runner and DCAE environment. Exiting')