package org.acumos.onboarding.common.utils;

import org.slf4j.Logger;

public class LoggerDelegate {

	Logger logger;

	public LoggerDelegate() {

	}

	public LoggerDelegate(Logger logger) {
		this.logger = logger;
	}

	public void info(String msg) {
		logger.info(msg);
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_INFO);
	}
	
	public void debug(String msg) {

		logger.debug(msg);
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_DEBUG);

	}

	public void debug(String message, String path, String fileName) {

		logger.debug(message, path, fileName);
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_DEBUG);

	}

	public void debug(String message, String fileName) {

		logger.debug(message, fileName);
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_DEBUG);

	}

	public void warn(String msg) {
		logger.warn(msg);
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_WARN);
	}

	
	public void error(String message) {

		logger.error(message);
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR);
	}

	public void error(String message, Exception e) {

		logger.error(message, e);
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR);

	}

	public void error(String message, String message2) {

		logger.error(message, message2);
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR);

	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	
}
