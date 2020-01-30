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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.designstudio.toscagenerator.ToscaGeneratorClient;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.services.impl.CommonOnboarding;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class CommonOnboardingTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	public static final String LOCAL_HOST = "http://localhost:8000/";

	@Mock
	RestTemplate restTemplate;

	@Mock
    CommonDataServiceRestClientImpl cdsClientImpl;

	@Mock
	PortalRestClientImpl client;

	@InjectMocks
	CommonOnboarding commonOnboarding;

	@Mock
	OnboardingNotification onboardingStatus;

	@Mock
	ToscaGeneratorClient toscaClient ;

	/*@Test
	public void getToolTypeCodeTest() {
		String typeCode = null;
		typeCode = commonOnboarding.getToolTypeCode("composite solution");
		Assert.assertNotNull(typeCode);
		typeCode = commonOnboarding.getToolTypeCode("scikit-learn");
		Assert.assertNotNull(typeCode);
		typeCode = commonOnboarding.getToolTypeCode("tensorflow");
		Assert.assertNotNull(typeCode);
		typeCode = commonOnboarding.getToolTypeCode("r");
		Assert.assertNotNull(typeCode);
		typeCode = commonOnboarding.getToolTypeCode("h2o");
		Assert.assertNotNull(typeCode);
		typeCode = commonOnboarding.getToolTypeCode("design studio");
		Assert.assertNotNull(typeCode);
	}*/


	@Test
	public void getModelVersionTest1() {
		List<MLPSolutionRevision> revList = new ArrayList<>();
		MLPSolutionRevision rev = new MLPSolutionRevision();
		rev.setRevisionId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		rev.setUserId("02a87750-7ba3-4ea7-8c20-c1286930f57c");
		rev.setSolutionId("03a87750-9ba3-4ea7-8c20-c1286930f85c");
		revList.add(rev);

		Mockito.when(cdsClientImpl.getSolutionRevisions("03a87750-9ba3-4ea7-8c20-c1286930f85c")).thenReturn(revList);
		Assert.assertNotEquals(commonOnboarding.getModelVersion("03a87750-9ba3-4ea7-8c20-c1286930f85c"), "0");
	}

	@Test
	public void getModelVersionTest2() {
		List<MLPSolutionRevision> revList = new ArrayList<>();
		MLPSolutionRevision rev = new MLPSolutionRevision();
		rev.setRevisionId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		rev.setUserId("02a87750-7ba3-4ea7-8c20-c1286930f57c");
		rev.setSolutionId("03a87750-9ba3-4ea7-8c20-c1286930f85c");
		revList.add(rev);

		Mockito.when(cdsClientImpl.getSolutionRevisions("03a87750-9ba3-4ea7-8c20-c1286930f85c")).thenReturn(revList);
		Assert.assertNotEquals(commonOnboarding.getModelVersion("03a87750-9ba3-4ea7-8c20-c1286930f85c", new File(FilePathTest.filePath()+"model.proto")), "0");
	}


	@Test
	public void validateTest() {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWNobSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlQ291bnQiOjAsInJvbGVJZCI6IjEyMzQ1Njc4LWFiY2QtOTBhYi1jZGVmLTEyMzQ1Njc4OTBhYiIsIm5hbWUiOiJhZG1pbiIsImFjdGl2ZSI6ZmFsc2UsImNyZWF0ZWQiOm51bGwsIm1vZGlmaWVkIjpudWxsfV0sImNyZWF0ZWQiOjE1MjM2MTc2OTU0NjIsImV4cCI6MTUyNDIyMjQ5NSwibWxwdXNlciI6eyJjcmVhdGVkIjoxNTIxODA4MDQyMDAwLCJtb2RpZmllZCI6MTUyMzUyMDE1MDAwMCwidXNlcklkIjoiMWE4ZThiNzMtMWNlNy00MWU4LWEzNjQtOTNmNWI1N2RlYjE0IiwiZmlyc3ROYW1lIjoidGVjaG0iLCJtaWRkbGVOYW1lIjpudWxsLCJsYXN0TmFtZSI6InRlY2htIiwib3JnTmFtZSI6bnVsbCwiZW1haWwiOiJ0ZWNobUB0ZWNobS5jb20iLCJsb2dpbk5hbWUiOiJ0ZWNobSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjoxNTIzNjE3Njk1MjAwLCJwaWN0dXJlIjpudWxsfX0.aXzsHPXw7SrgwudJS3rhKN-ECExknm02xtonJHwKIl0ngXZ3MvFHANklInNBwJoNqVYbpj7fSMyFVxZ_JkR0eA";

		JsonResponse<Object> valid = new JsonResponse<>();
		valid.setStatus(true);

		JSONObject obj1 = new JSONObject();
		obj1.put("jwtToken", token);
		JSONObject obj2 = new JSONObject();
		obj2.put("request_body", obj1);

		JsonResponse<Object> jsonResponse = new JsonResponse<Object>();

		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = Obj.writeValueAsString(jsonResponse);
		} catch (IOException e) {
			//logger.error("Exception occurred while parsing rest page response to string ", e.getMessage());
		}

		stubFor(post(urlEqualTo("/auth/validateToken")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody(jsonStr)));

//		try {
//			commonOnboarding.validate(token, null);
//		} catch (AcumosServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


//		try {
//			Assert.assertNotNull(commonOnboarding.validate(token, "loginName", "GitHub"));
//		} catch (AcumosServiceException e) {
//			Assert.fail("Exception occured while validateTest(): " + e.getMessage());
//		}

	}

	@Test
	public void createSolutionRevisionTest1() {

		Metadata data = new Metadata();
		data.setModelName("Predictor");
		data.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		data.setVersion("3.6.1");
		data.setSolutionId("02a87750-7ba3-4ea7-8c20-c1286930f57c");

		MLPSolutionRevision revision = new MLPSolutionRevision();
		revision.setRevisionId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		revision.setVersion("3.6.1");

		Mockito.when(cdsClientImpl.createSolutionRevision(Mockito.any(MLPSolutionRevision.class))).thenReturn(revision);
		try {
			Assert.assertNotNull(commonOnboarding.createSolutionRevision(data));
		} catch (AcumosServiceException e) {
			Assert.fail("Exception occured while createSolutionRevisionTest(): " + e.getMessage());
		}
	}

	@Test
	public void createSolutionRevisionTest2() {

		Metadata data = new Metadata();
		data.setModelName("Predictor");
		data.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		data.setVersion("3.6.1");
		data.setSolutionId("02a87750-7ba3-4ea7-8c20-c1286930f57c");

		MLPSolutionRevision revision = new MLPSolutionRevision();
		revision.setRevisionId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		revision.setVersion("3.6.1");

		Mockito.when(cdsClientImpl.createSolutionRevision(Mockito.any(MLPSolutionRevision.class))).thenReturn(revision);
		try {
			Assert.assertNotNull(commonOnboarding.createSolutionRevision(data, new File(FilePathTest.filePath()+"model.proto")));
		} catch (AcumosServiceException e) {
			Assert.fail("Exception occured while createSolutionRevisionTest(): " + e.getMessage());
		}
	}

	@Test
	public void createSolutionTest() {

		Metadata data = new Metadata();
		data.setModelName("Predictor");
		data.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		data.setToolkit("H2");

		MLPSolution solution1 = new MLPSolution();
		solution1.setName("Predictor");
		solution1.setSolutionId("02a87750-7ba3-4ea7-8c20-c1286930f57c");

		Mockito.when(cdsClientImpl.createSolution(Mockito.any(MLPSolution.class))).thenReturn(solution1);

		try {
			Assert.assertNotNull(commonOnboarding.createSolution(data,null));
		} catch (AcumosServiceException e) {
			Assert.fail("Exception occured while createSolutionTest(): " + e.getMessage());
		}
	}

	@Test
	public void listFilesAndFilesSubDirectoriesTest() {

		File f1 = new File(FilePathTest.filePath());
		try {
			commonOnboarding.listFilesAndFilesSubDirectories(f1);
		} catch (Exception e) {
			Assert.fail("Exception occured while listFilesAndFilesSubDirectoriesTest(): " + e.getMessage());
		}
	}
}
