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

import javax.servlet.http.HttpServletRequest;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
public class CustomErrorController implements ErrorController {

	private static final String PATH = "/error";

	@Override
	public String getErrorPath() {
		return PATH;
	}

	@RequestMapping(value = PATH)
	@ResponseBody
	@ExceptionHandler(value = { AcumosServiceException.class })
	public ResponseEntity<ServiceResponse> acumosServiceExceptionHandler(HttpServletRequest request,
			AcumosServiceException exception) {

		AcumosServiceException e = (AcumosServiceException) exception;
		HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
		if (e.getErrorCode() == null) {
			e.setErrorCode(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR.name());
		}
		if (e.getErrorCode().equals(AcumosServiceException.ErrorCode.INVALID_PARAMETER.name())) {
			httpCode = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()),
				httpCode);
	}

}
