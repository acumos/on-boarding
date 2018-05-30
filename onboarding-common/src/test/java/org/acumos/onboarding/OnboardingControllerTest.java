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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;

import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.AbstractResponseObject;
import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.JsonRequest;
import org.acumos.onboarding.services.impl.OnboardingController;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingControllerTest {

	@Mock
    RestTemplate restTemplate;

    @InjectMocks
    OnboardingController onboardingController;

    @Mock
    protected PortalRestClientImpl portalClient;
    private HttpServletResponse response;

	@SuppressWarnings("unchecked")
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

        System.out.println("testing1....");
        
        String url = "http://cognita-dev1-vm01-core:8083/auth/jwtToken";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            
        when(portalClient.loginToAcumos(any(JSONObject.class))).thenReturn("jwttokena12bc");
        ResponseEntity<ServiceResponse> result = onboardingController.OnboardingWithAuthentication(cred, response);
        assertNotNull(result);
    }

	private JSONObject any(Class<JSONObject> class1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Test
	public void generateTOSCATest() {
		String filePath = FilePathTest.filePath();
		File localProtobufFile = new File(filePath + "model.proto");
		File localMetadataFile = new File(filePath + "metadata.json");
		Metadata m = new Metadata();
		OnboardingNotification onboardingStatus = new OnboardingNotification("", "", "");
		on.generateTOSCA(localProtobufFile, localMetadataFile, m, onboardingStatus);
		assert (true);

	}*/

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
