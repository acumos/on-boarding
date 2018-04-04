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

/**
 * 
 */
package org.acumos.onboarding;

import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.AbstractResponseObject;
import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.JsonRequest;
import org.acumos.onboarding.component.docker.preparation.Metadata;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingControllerTest {
	
	@Mock
	RestTemplate restTemplate;

	@Mock
	OnboardingController onboardingController;

	@InjectMocks
	PortalRestClientImpl portalclient = new PortalRestClientImpl("http://cognita-dev1-vm01-core:8083");
	
	private HttpServletResponse response;
    //private HttpServletRequest request = new MockHttpServletRequest();

/*	@SuppressWarnings("unchecked")
	@Test
	public void OnboardingWithAuthentication() throws Exception {

		try {
			String user = " ";
			String pass = " ";

			JSONObject crediantials = new JSONObject();
			crediantials.put("username", user);
			crediantials.put("password", pass);

			JSONObject reqObj = new JSONObject();
			reqObj.put("request_body", crediantials);

			System.out.println("testing....");

			String token = portalclient.loginToAcumos(reqObj);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	@Test
	public void OnboardingWithAuthentication1() throws Exception {

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

		System.out.println("testing....");
		
		String url = "http://cognita-dev1-vm01-core:8083/auth/jwtToken";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		
		URI uri = builder.build().encode().toUri();
		when(restTemplate.postForObject(uri, reqObj, AbstractResponseObject.class)).thenReturn(absObj);

		 when(portalclient.loginToAcumos(crediantials)).thenReturn(token);
		
		ResponseEntity<ServiceResponse> result = onboardingController.OnboardingWithAuthentication(cred, response);
		Assert.assertNotNull(result);
	}


	@Test
	public void generateTOSCATest() {
		String filePath = FilePathTest.filePath();
		File localProtobufFile = new File(filePath + "model.proto");
		File localMetadataFile = new File(filePath + "metadata.json");
		Metadata m = new Metadata();
		onboardingController.generateTOSCA(localProtobufFile, localMetadataFile, m);
		assert (true);

	}

	@Test
	public void getToolTypeCodeTest() {
		String toolkit = "Scikit-Learn";
		onboardingController.getToolTypeCode(toolkit);
		assert (true);
	}

	@Test
	public void getCmnDataSvcEndPoinURLTest() {
		onboardingController.getCmnDataSvcEndPoinURL();
	}

	@Test
	public void getCmnDataSvcUserTest() {
		onboardingController.getCmnDataSvcUser();
	}

	@Test
	public void getCmnDataSvcPwdTest() {
		onboardingController.getCmnDataSvcPwd();
	}
}
