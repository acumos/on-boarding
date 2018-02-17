package org.acumos.onboarding.open.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

//@RestController
//@ApiIgnore
public class CustomErrorController //implements ErrorController
{

	/*private static final String PATH = "/error";

	@Override
	public String getErrorPath() {
		return PATH;
	}

	@RequestMapping(value = PATH)
	@ResponseBody
	@ExceptionHandler(value = { AcumosServiceException.class })
	public ResponseEntity<ServiceResponse> AcumosServiceExceptionHandler(HttpServletRequest request,
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
	}*/

}