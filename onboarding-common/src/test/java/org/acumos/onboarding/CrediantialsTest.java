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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;

import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.AbstractResponseObject;
import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.JsonRequest;
import org.acumos.onboarding.services.impl.OnboardingController;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class CrediantialsTest {

	@InjectMocks
	OnboardingController onboardingController;

	@Mock
	protected PortalRestClientImpl portalClient;
	
	final HttpServletResponse response = mock(HttpServletResponse.class);
	
	@Test
	public void crediantialsTest() {
		Crediantials crediantials = new Crediantials();
		crediantials.setUsername("testUser");
		crediantials.setPassword("password");
		crediantials.getUsername();
		crediantials.getPassword();
		Assert.assertNotNull(crediantials);
	}
	
	@Test
	public void testOnboardingWithAuthentication() throws Exception {

		Crediantials credential = new Crediantials();
		String user = " ";
		String pass = " ";
		String token = "SampleToken";
		AbstractResponseObject absObj = new AbstractResponseObject();

		JSONObject crediantials = new JSONObject();
		crediantials.put("username", user);
		crediantials.put("password", pass);

		JSONObject reqObj = new JSONObject();
		reqObj.put("request_body", crediantials); 


		JsonRequest<Crediantials> cred = new JsonRequest<>();
		cred.setBody(credential);

		when(portalClient.loginToAcumos(any(JSONObject.class))).thenReturn("jwttokena12bc");
		ResponseEntity<ServiceResponse> result = onboardingController.OnboardingWithAuthentication(cred, response);
		assertNotNull(result);
	}

}
