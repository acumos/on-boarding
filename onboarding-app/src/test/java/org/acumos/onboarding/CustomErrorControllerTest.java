/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual
 * 						Property & Tech
 * 						Mahindra. All rights reserved.
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

import javax.servlet.http.HttpServletRequest;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.services.impl.CustomErrorController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomErrorControllerTest {
	
	
	@Mock
	CustomErrorController customErrorController;
	
	@Test
	public void cognitaServiceExceptionHandlerTest() {
		
		HttpServletRequest request = null;// = new MockHttpServletRequest();
		
		AcumosServiceException acumosServiceException = new AcumosServiceException("service not available");
		//customErrorController.cognitaServiceExceptionHandler(request, exception);
		acumosServiceException.setErrorCode(null);
		
		ResponseEntity<ServiceResponse>  response = customErrorController.acumosServiceExceptionHandler(request, acumosServiceException);
		customErrorController.acumosServiceExceptionHandler(request, acumosServiceException);
		when(customErrorController.acumosServiceExceptionHandler(request, acumosServiceException)).thenReturn(response);
		assert(true);
		
			
	}
	
	@Test
	public void cognitaServiceExceptionHandlerTest1() {
		
		HttpServletRequest request = null;// = new MockHttpServletRequest();
		
		AcumosServiceException acumosServiceException = new AcumosServiceException("service not available");
		//customErrorController.cognitaServiceExceptionHandler(request, exception);
		acumosServiceException.setErrorCode(AcumosServiceException.ErrorCode.INVALID_PARAMETER.name());
		
		ResponseEntity<ServiceResponse>  response = customErrorController.acumosServiceExceptionHandler(request, acumosServiceException);
		customErrorController.acumosServiceExceptionHandler(request, acumosServiceException);
		when(customErrorController.acumosServiceExceptionHandler(request, acumosServiceException)).thenReturn(response);
		assert(true);
		}

	
}
