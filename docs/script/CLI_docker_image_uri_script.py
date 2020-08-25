#!/usr/bin/env python3
# ===================================================================================
# Copyright (C) 2019 Fraunhofer Gesellschaft. All rights reserved.
# ===================================================================================
# This Acumos software file is distributed by Fraunhofer Gesellschaft
# under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# This file is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ===============LICENSE_END==========================================================
"""
Provides an example of Docker URI cli on-boarding
"""
import requests
import contextlib
import getpass
import os
from os import environ

_USERNAME_VAR = 'ACUMOS_USERNAME'
_PASSWORD_VAR = 'ACUMOS_PASSWORD'
host = ""


class Options(object):
    '''
    A collection of options that users may wish to specify along with their Acumos model

    Parameters
    ----------
    create_microservice : bool, optional
        If True, instructs the Acumos platform to eagerly build the model microservice
        but in this script its default value is false.
    license : str, optional
        A license to include with the Acumos model. This parameter may either be a path to a license
        file, or a string containing the license content.
    protobuf : str, optional
        A protobuf to include with the Acumos model.
    '''
    __slots__ = ('create_microservice', 'license', 'protobuf')

    def __init__(self, create_microservice=False, license=None, protobuf=None):
        self.create_microservice = create_microservice
        self.license = license
        self.protobuf = protobuf


@contextlib.contextmanager
def _patch_environ(**kwargs):
    '''Temporarily adds kwargs to os.environ'''
    try:
        orig_vars = {k: environ[k] for k in kwargs.keys() if k in environ}
        environ.update(kwargs)
        yield
    finally:
        environ.update(orig_vars)
        for extra_key in (kwargs.keys() - orig_vars.keys()):
            del environ[extra_key]


def _authenticate(auth_api):
    '''Authenticates and returns the jwt string'''
    username = environ.get(_USERNAME_VAR)
    password = environ.get(_PASSWORD_VAR)

    # user/pass supported for now. use if explicitly provided instead of prompting for token
    if username and password:
        if auth_api is None:
            print('An authentication API is required if using username & password credentials')

        headers = {'Content-Type': 'application/json', 'Accept': 'application/json'}
        request_body = {'request_body': {'username': username, 'password': password}}
        response = requests.post(auth_api, json=request_body, headers=headers)

        if response.status_code != 200:
            print("Authentication failure: {}".format(r.text))

        jwt = response.json()['jwtToken']
    else:
        jwt = gettoken('Enter onboarding token: ')

    return jwt


def _post_model(dockerImageURI, model_name, files, advance_api, auth_api, options):
    '''Attempts to post the model to Acumos'''
    headers = {"Accept": "application/json",
               "modelname": model_name,
               "Authorization": _authenticate(auth_api),
               "dockerImageUri": dockerImageURI,
               'isCreateMicroservice': 'true' if options.create_microservice else 'false'}
    response = requests.post(advance_api, files=files, headers=headers)
    if response.status_code == 201:
        print("Docker uri is pushed successfully on {" + host + "}, response is: ", response.status_code)
    else:
        print("Docker uri is not pushed on {" + host + "}, response is: ", response.status_code)


class Checkinput(object):
    result = None
    response = None

    def __init__(self):
        pass

    def _check_userinput(self, response):
        if response.lower() == 'y':
            return True
        elif response.lower() == 'n':
            return True
        else:
            return False

    def read_userinput(self, question):
        self.response = input(question)
        self.result = self._check_userinput(self.response)

        if not self.result:
            print('Invalid Input: ')
            self.response = self.read_userinput(question)
        return self.response

    def attach_file(self, question, option, category):
        file = ""
        response = self.read_userinput(question).lower()
        if response == 'y':
            file = input("Enter the name of file : ")
            file = "./" + file
            if os.path.isfile(file):
                if category == "license":
                    option.license = True
                elif category == "protobuf":
                    option.protobuf = True
                return file
            else:
                print('This {} file does not exist in this folder'.format(category))
                return self.attach_file(question, option, category)
        elif response.lower() == 'n':
            return file


if __name__ == "__main__":
    user_name = input("Enter your Acumos user name: ")
    __Password = getpass.getpass("Enter your Acumos password: ")
    model_name = input("Enter model name: ")
    print("Docker image URI looks like: https://example.com:port/image-tag:version")
    dockerImageURI = input("Enter docker image URI: ")
    host = input("Enter the host name: ")
    auth_api = "https://" + host + ":443/onboarding-app/v2/auth"
    advance_api = "https://" + host + ":443/onboarding-app/v2/advancedModel"
    option = Options(create_microservice=False, license=False, protobuf=False)
    check_input = Checkinput()

    license_file = check_input.attach_file("Do you want to attach license file (y/n)? : ", option, "license")
    protobuf_file = check_input.attach_file("Do you want to attach protobuf file (y/n)? : ", option, "protobuf")

    with _patch_environ(**{_USERNAME_VAR: user_name, _PASSWORD_VAR: __Password}):
        files = {}
        if option.protobuf and option.license:
            files = {'license': open(license_file, 'rb'), 'protobuf': open(protobuf_file, 'rb')}
        elif option.license:
            files = {'license': open(license_file, 'rb')}
        elif option.protobuf:
            files = {'protobuf': open(protobuf_file, 'rb')}
        else:
            files = {}

        _post_model(dockerImageURI=dockerImageURI, model_name=model_name, files=files, advance_api=advance_api,
                    auth_api=auth_api, options=option)
