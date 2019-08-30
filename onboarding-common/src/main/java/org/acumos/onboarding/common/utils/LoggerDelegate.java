/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

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
	
	public void info(String msg, LogBean logBean) {
		logger.info(msg);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "INFO");
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_INFO, logBean);
	}

	public void debug(String msg) {

		logger.debug(msg);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "DEBUG");
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_DEBUG);

	}

	public void debug(String msg, LogBean logBean) {

		logger.debug(msg);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "DEBUG");
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_DEBUG, logBean);

	}

	public void debug(String message, String path, String fileName) {

		logger.debug(message, path, fileName);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "DEBUG");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_DEBUG);

	}
	
	public void debug(String message, String path, String fileName, LogBean logBean) {

		logger.debug(message, path, fileName);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "DEBUG");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_DEBUG, logBean);

	}

	public void debug(String message, String fileName) {

		logger.debug(message, fileName);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "DEBUG");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_DEBUG);

	}
	
	public void debug(String message, String fileName, LogBean logean) {

		logger.debug(message, fileName);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "DEBUG");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_DEBUG, logean);

	}

	public void warn(String msg) {
		logger.warn(msg);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "WARN");
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_WARN);
	}
	
	public void warn(String msg, LogBean logBean) {
		logger.warn(msg);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "WARN");
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_WARN, logBean);
	}

	public void error(String message) {

		logger.error(message);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "ERROR");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR);
	}
	
	public void error(String msg, LogBean logBean) {

		logger.error(msg);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "ERROR");
		UtilityFunction.addLogs(msg, OnboardingConstants.lOG_TYPE_ERROR, logBean);

	}
	
	public void error(String message, Exception e) {

		logger.error(message, e);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "ERROR");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR);

	}
	
	public void error(String message, Exception e, LogBean logBean) {

		logger.error(message, e);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "ERROR");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR, logBean);

	}

	public void error(String message, String message2) {

		logger.error(message, message2);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "ERROR");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR);

	}

	public void error(String message, String message2, LogBean logBean) {

		logger.error(message, message2);
		MDC.put(OnboardingLogConstants.MDCs.RESPONSE_SEVERITY, "ERROR");
		UtilityFunction.addLogs(message, OnboardingConstants.lOG_TYPE_ERROR, logBean);

	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}


}
