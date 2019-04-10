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

package org.acumos.onboarding.services.impl;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.services.HealthcheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

/**
 * Answers REST requests for the service health.
 */
@Controller
@RequestMapping(value = "/", produces = "application/json")
public class HealthcheckController implements HealthcheckService {

	private final Logger log = LoggerFactory.getLogger(HealthcheckController.class);
	LoggerDelegate logger = new LoggerDelegate(log);

	private ICommonDataServiceRestClient cdmsClient;

	@Value("${cmndatasvc.cmnDataSvcEndPoinURL}")
	private String cmnDataSvcEndPoinURL;

	@Value("${cmndatasvc.cmnDataSvcUser}")
	private String cmnDataSvcUser;

	@Value("${cmndatasvc.cmnDataSvcPwd}")
	private String cmnDataSvcPwd;

	/**
	 * This method initialize the common data service
	 */
	@PostConstruct
	public void init() {
		logger.debug("init: creating CDS client");
		cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd,null);
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return Success or Error
	 */
	@ApiOperation(value = "Assesses the health of the application.", response = SuccessTransport.class)
	@RequestMapping(value = "healthcheck", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ServiceResponse> getHealth(HttpServletRequest request, HttpServletResponse response) {
		ResponseEntity<ServiceResponse> result = null;
		logger.debug("getHealth");
		try {
			SuccessTransport cdmsHealth = cdmsClient.getHealth();
			result = new ResponseEntity<ServiceResponse>(
					ServiceResponse.successResponse("CDMS health: " + cdmsHealth.getData().toString()), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("getHealth failed", ex);
			result = new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse("not healthy", ex.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

}
