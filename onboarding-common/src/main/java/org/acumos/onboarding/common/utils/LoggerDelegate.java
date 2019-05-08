package org.acumos.onboarding.common.utils;

import org.acumos.onboarding.logging.OnboardingLogConstants;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class LoggerDelegate {

	Logger logger;

	public LoggerDelegate() {

	}

	public LoggerDelegate(Logger logger) {
		this.logger = logger;
	}

	public void info(String msg) {
		logger.info(msg);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "INFO");
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_INFO);
	}
	
	public void debug(String msg) {

		logger.debug(msg);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "DEBUG");
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_DEBUG);

	}

	public void debug(String message, String path, String fileName) {

		logger.debug(message, path, fileName);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "DEBUG");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_DEBUG);

	}

	public void debug(String message, String fileName) {

		logger.debug(message, fileName);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "DEBUG");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_DEBUG);

	}

	public void warn(String msg) {
		logger.warn(msg);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "WARN");
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_WARN);
	}

	
	public void error(String message) {

		logger.error(message);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "ERROR");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR);
	}

	public void error(String message, Exception e) {

		logger.error(message, e);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "ERROR");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR);

	}

	public void error(String message, String message2) {

		logger.error(message, message2);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "ERROR");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR);

	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	
}
