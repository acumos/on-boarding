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

package org.acumos.onboarding.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.springframework.http.ResponseEntity;

/**
 * Defines a method to check application health.
 */
public interface HealthcheckService {
	
	/**
	 * Checks application health.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return ServiceResponse with statement of health
	 * @throws AcumosServiceException
	 *             On failure
	 */
	public ResponseEntity<ServiceResponse> getHealth(HttpServletRequest request, HttpServletResponse response)
			throws AcumosServiceException;
}
