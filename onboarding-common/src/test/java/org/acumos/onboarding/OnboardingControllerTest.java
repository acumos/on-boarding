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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.AbstractResponseObject;
import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.JsonRequest;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.services.impl.OnboardingController;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(OnboardingController.class)
public class OnboardingControllerTest {

	@Mock
	RestTemplate restTemplate;

	@InjectMocks
	OnboardingController onboardingController;

	@Mock
	protected PortalRestClientImpl portalClient;
	
	@Mock
	protected CommonDataServiceRestClientImpl cdmsClient;
	
	@Mock
	OnboardingNotification onboardingNotification;
	

	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(OnboardingController.class);
	
	
	final HttpServletResponse response = mock(HttpServletResponse.class);
   
	 @Before
	  public void setUp() throws Exception {
	        MockitoAnnotations.initMocks(this);
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

    /**
     * Testcase to check invalid metadata json which should recieve failure or exception 
     * @throws Exception
     */
	/*@Test
	public void testdockerizePayloadWtihInavliadMetadata() throws Exception {

		try {
			MultipartFile multipart = mock(MultipartFile.class);

			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("meta.json").getFile());

			FileInputStream modelIS = new FileInputStream(file.getAbsolutePath());
			MockMultipartFile metaDatazipFile = new MockMultipartFile("file1", "metadata.json", "multipart/form-data",
					modelIS);

			FileInputStream metataprotoIS = new FileInputStream(file.getAbsolutePath());
			MockMultipartFile protoFile = new MockMultipartFile("file", "model.proto", "multipart/form-data",
					metataprotoIS);

			FileInputStream metaDataIS = new FileInputStream(file.getAbsolutePath());
			MockMultipartFile metaDataFile = new MockMultipartFile("file", "meta.json", "multipart/form-data",
					metaDataIS);

			CommonDataServiceRestClientImpl cmdDataSvc = mock(CommonDataServiceRestClientImpl.class);
			OnboardingNotification onboardingStatus = mock(OnboardingNotification.class);

			JsonResponse<Object> jsonResp = new JsonResponse<Object>();
			jsonResp.setStatus(true);
			jsonResp.setResponseBody("ownerid");

			PowerMockito.whenNew(CommonDataServiceRestClientImpl.class)
					.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())
					.thenReturn(cmdDataSvc);

			PowerMockito.whenNew(OnboardingNotification.class)
					.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())
					.thenReturn(onboardingStatus);

			PowerMockito.when(onboardingController.validate(any(String.class), any(String.class))).thenReturn(jsonResp);
			
			ResponseEntity<ServiceResponse> resp = onboardingController.onboardModel(mock(HttpServletRequest.class),
					metaDatazipFile, metaDataFile, protoFile, "authorization", null, "provider", null);
			logger.info("HttpStatus code:" + resp.getStatusCodeValue() +" \nBody:"+ resp.getBody());
            assertEquals(400,resp.getStatusCodeValue());
		} catch (AcumosServiceException e) {
			Assert.fail("testdockerizePayloadWtihInavliadMetadata failed : " + e.getMessage());
		}

	}*/


}
