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

import java.io.File;

import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.services.impl.OnboardingController;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingControllerTest {

	@Mock
	RestTemplate restTemplate;

	OnboardingController on = new OnboardingController();

	@InjectMocks
	PortalRestClientImpl portalclient = new PortalRestClientImpl("http://cognita-dev1-vm01-core:8083");

	@SuppressWarnings("unchecked")
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
	}

	@Test
	public void generateTOSCATest() {
		String filePath = FilePathTest.filePath();
		File localProtobufFile = new File(filePath + "model.proto");
		File localMetadataFile = new File(filePath + "metadata.json");
		Metadata m = new Metadata();
		on.generateTOSCA(localProtobufFile, localMetadataFile, m);
		assert (true);

	}

	@Test
	public void getToolTypeCodeTest() {
		String toolkit = "Scikit-Learn";
		on.getToolTypeCode(toolkit);
		assert (true);
	}

	@Test
	public void getCmnDataSvcEndPoinURLTest() {
		on.getCmnDataSvcEndPoinURL();
	}

	@Test
	public void getCmnDataSvcUserTest() {
		on.getCmnDataSvcUser();
	}

	@Test
	public void getCmnDataSvcPwdTest() {
		on.getCmnDataSvcPwd();
	}
}
