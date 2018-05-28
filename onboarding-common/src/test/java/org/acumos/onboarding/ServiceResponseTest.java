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

package org.acumos.onboarding;

import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceResponseTest {

	public static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(TagImageCommandTest.class);

	@InjectMocks
	ServiceResponse serviceResponse;

	@Test
	public void errorResponse(){
		ServiceResponse error = new ServiceResponse();
		error.setStatus("ERROR");
		error.setErrorCode("errorCode");
		error.setErrorMessage("errorMessage");
		//when(serviceResponse.errorResponse("errorCode", "errorMessage")).thenReturn(error);
		Assert.assertNotNull(serviceResponse.errorResponse("errorCode", "errorMessage"));
	}
	
	@Test
	public void successResponse(){
		ServiceResponse success = new ServiceResponse();
		success.setStatus("SUCCESS");
		Assert.assertNotNull(serviceResponse.successResponse());
	}
	
	@Test
	public void successResponseObj(){
		ServiceResponse success = new ServiceResponse();
		success.setStatus("SUCCESS");
		Assert.assertNotNull(serviceResponse.successResponse(new Object()));
	}
	
	@Test
	public void successJWTResponse(){
		Assert.assertNotNull(serviceResponse.successJWTResponse("jwtToken"));
	}
	

	@Test
	public void status() {

		serviceResponse.setErrorCode("success");
		serviceResponse.setErrorMessage("Success");
		serviceResponse.setJwtToken(
				"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJEaWxpcCIsImNyZWF0ZWQiOjE1MDUyOTU0MTA2MDEsImV4cCI6MTUwNTI5NTQyMCwibWxwdXNlciI6eyJ1c2VySWQiOiI2ZDU0NjAxZS0wNWE4LTQ1MTQtODFiYS0xYjFmM2JkODhlMWUiLCJmaXJzdE5hbWUiOiJEaWxpcCIsIm1pZGRsZU5hbWUiOm51bGwsImxhc3ROYW1lIjoiS3VtYXIiLCJvcmdOYW1lIjpudWxsLCJlbWFpbCI6IkRpbGlwQGdtYWlsLmNvbSIsImxvZ2luTmFtZSI6IkRpbGlwIiwibG9naW5QYXNzIjpudWxsLCJsb2dpblBhc3NFeHBpcmUiOm51bGwsImF1dGhUb2tlbiI6bnVsbCwiYWN0aXZlIjp0cnVlLCJsYXN0TG9naW4iOm51bGwsInBpY3R1cmUiOm51bGwsImNyZWF0ZWQiOjE1MDUyOTU0MDIwMDAsIm1vZGlmaWVkIjoxNTA1Mjk1NDAyMDAwfX0.n3IXoP-GDGUEmowxiesttqFDXH1NCK4sICYFfsr9L6LmLAnb2BslWomB6EjJY-MsTh4u4gOyOrguEYvJhY6b-w");
		serviceResponse.setStatus("Success");
		serviceResponse.setResult("Success");
		serviceResponse.getErrorCode();
		serviceResponse.getErrorMessage();
		serviceResponse.getJwtToken();
		serviceResponse.getStatus();
		serviceResponse.getStatus();
		serviceResponse.getResult();
		Assert.assertNotNull(serviceResponse);
	}

	@Test
	public void toStringCheck() {
		Assert.assertNotNull(serviceResponse.toString());
	}
}
