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

import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.onboarding.services.impl.HealthcheckController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HealthcheckControllerTest{
	
	@InjectMocks
	HealthcheckController healthcheckController = new HealthcheckController();
	
	@Mock
	ICommonDataServiceRestClient cdmsClient;
	
	@Test
	public void getHealthTest(){
		SuccessTransport cdmsHealth =new SuccessTransport();
		cdmsHealth.setStatus(1);
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockReponse= mock(HttpServletResponse.class);	  
		when(cdmsClient.getHealth()).thenReturn(cdmsHealth);
		healthcheckController.getHealth(mockRequest, mockReponse);
	}
}
