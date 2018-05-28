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

import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.services.impl.CommonOnboarding;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class CommonOnboardingTest {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(CommonOnboardingTest.class);

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
	
	@Test
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
	}
	
	@Test
	public void addArtifactURITest() {
		try {
			Metadata data = new Metadata();
			data.setSolutionId("02a87750-7ba3-4ea7-8c20-c1286930f57c");
			data.setRevisionId("0b1510a2-2f0f-4e59-9783-1606e2e78072");
			data.setModelName("Predictor");
			data.setVersion("3.6.1");
			data.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");

			MLPArtifact artifact = new MLPArtifact();
			artifact.setName("Predictor");
			artifact.setDescription("Provides the set of Alarm condition");
			artifact.setVersion("3.6.1");
			artifact.setArtifactTypeCode("MS");
			artifact.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");
			artifact.setUri("http://cognita-nexus01:8001/");
			artifact.setSize(100);
			artifact.setArtifactId("12345678-abcd-90ab-cdef-1234567890ab");

			MLPArtifact artifactObj = new MLPArtifact();
			artifactObj.setName("Predictor");
			artifactObj.setDescription("Provides the set of Alarm condition");
			artifactObj.setVersion("3.6.1");
			artifactObj.setArtifactTypeCode("MS");
			artifactObj.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");
			artifactObj.setUri("http://cognita-nexus01:8001/");
			artifactObj.setSize(100);
			artifactObj.setArtifactId("54645678-abcd-90ab-cdef-1234567890ab");

			when(cdsClientImpl.createArtifact(artifact)).thenReturn(artifactObj);

			Mockito.doNothing().when(cdsClientImpl).addSolutionRevisionArtifact(Mockito.isA(String.class),
					Mockito.isA(String.class), Mockito.isA(String.class));

			Mockito.doNothing().when(cdsClientImpl).addSolutionRevisionArtifact(data.getSolutionId(),
					data.getRevisionId(), "12345678-abcd-90ab-cdef-1234567890ab");

			Mockito.doNothing().when(onboardingStatus).notifyOnboardingStatus("AddToRepository", "ST",
					"Add Artifact for" + "http://cognita-nexus01:8001/" + " Started");
			commonOnboarding.addArtifact(data, "http://cognita-nexus01:8001/", "MS", onboardingStatus);
		} catch (AcumosServiceException e) {
			logger.info("Exception occured while addArtifactURITest()" + e.getMessage());
		}
	}
		
	@Test
	public void getModelVersion() {
		List<MLPSolutionRevision> revList = new ArrayList<>();
		MLPSolutionRevision rev = new MLPSolutionRevision();
		rev.setRevisionId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		rev.setOwnerId("02a87750-7ba3-4ea7-8c20-c1286930f57c");
		rev.setSolutionId("03a87750-9ba3-4ea7-8c20-c1286930f85c");
		revList.add(rev);

		Mockito.when(cdsClientImpl.getSolutionRevisions("03a87750-9ba3-4ea7-8c20-c1286930f85c")).thenReturn(revList);
		commonOnboarding.getModelVersion("03a87750-9ba3-4ea7-8c20-c1286930f85c");
	}
	
	
	@Test
	public void validateTest() {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWNobSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlQ291bnQiOjAsInJvbGVJZCI6IjEyMzQ1Njc4LWFiY2QtOTBhYi1jZGVmLTEyMzQ1Njc4OTBhYiIsIm5hbWUiOiJhZG1pbiIsImFjdGl2ZSI6ZmFsc2UsImNyZWF0ZWQiOm51bGwsIm1vZGlmaWVkIjpudWxsfV0sImNyZWF0ZWQiOjE1MjM2MTc2OTU0NjIsImV4cCI6MTUyNDIyMjQ5NSwibWxwdXNlciI6eyJjcmVhdGVkIjoxNTIxODA4MDQyMDAwLCJtb2RpZmllZCI6MTUyMzUyMDE1MDAwMCwidXNlcklkIjoiMWE4ZThiNzMtMWNlNy00MWU4LWEzNjQtOTNmNWI1N2RlYjE0IiwiZmlyc3ROYW1lIjoidGVjaG0iLCJtaWRkbGVOYW1lIjpudWxsLCJsYXN0TmFtZSI6InRlY2htIiwib3JnTmFtZSI6bnVsbCwiZW1haWwiOiJ0ZWNobUB0ZWNobS5jb20iLCJsb2dpbk5hbWUiOiJ0ZWNobSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjoxNTIzNjE3Njk1MjAwLCJwaWN0dXJlIjpudWxsfX0.aXzsHPXw7SrgwudJS3rhKN-ECExknm02xtonJHwKIl0ngXZ3MvFHANklInNBwJoNqVYbpj7fSMyFVxZ_JkR0eA";

		JsonResponse<Object> valid = new JsonResponse<>();
		valid.setStatus(true);
		JSONObject obj2 = new JSONObject();
		Mockito.when(client.tokenValidation(obj2, "GitHub")).thenReturn(valid);

		try {
			commonOnboarding.validate(token, "GitHub");
		} catch (AcumosServiceException e) {
			logger.info("Exception occured while validateTest()" + e.getMessage());
		}
	}
	
	
	/*@Test
	public void getExistingSolution() {
		Metadata data = new Metadata();
		data.setModelName("Predictor");
		data.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ownerId", "361de562-2e4d-49d7-b6a2-b551c35050e6");
		queryParameters.put("name", "Predictor");
		queryParameters.put("active", true);
	
		List<MLPSolution> solList = new ArrayList<>();
		MLPSolution sol=new MLPSolution();
		sol.setSolutionId("78a87750-9ba3-4ea7-8c20-c1286930f85c");
		sol.setOwnerId("56a87750-9ba3-4ea7-8c20-c1286930f85c");
		sol.setName("Predictor");
		solList.add(sol);
		RestPageResponse<MLPSolution> pageResponse =new RestPageResponse<>(solList);
		
		Mockito.when(cdsClientImpl.searchSolutions(queryParameters, false, new RestPageRequest(0, 9))).thenReturn(pageResponse);
		commonOnboarding.getExistingSolution(data);
	}
	
	@Test
	public void createSolutionRevisionTest() {
		
		Metadata data = new Metadata();
		data.setModelName("Predictor");
		data.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		data.setVersion("3.6.1");
		data.setSolutionId("02a87750-7ba3-4ea7-8c20-c1286930f57c");
		
		MLPSolutionRevision revision = new MLPSolutionRevision();
		revision.setRevisionId("361de562-2e4d-49d7-b6a2-b551c35050e6");
		revision.setVersion("3.6.1");
		
		Mockito.when(cdsClientImpl.createSolutionRevision(revision)).thenReturn(revision);
		try {
			commonOnboarding.createSolutionRevision(data);
		} catch (AcumosServiceException e) {
			e.printStackTrace();
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
		
		Mockito.when(cdsClientImpl.createSolution(solution1)).thenReturn(solution1);
		
		try {
			commonOnboarding.createSolution(data,null);
		} catch (AcumosServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void dockerizeFileTest() {
		//File localProtobufFile = new File(filePath + "model.proto");
		String filePath = FilePathTest.filePath();
		File dataFile = new File(filePath + "metadata.json");
		MetadataParser parser;
		try {
			parser = new MetadataParser(dataFile);
		
		File localmodelFile = null;
		
		commonOnboarding.dockerizeFile(parser, localmodelFile, "78a87750-9ba3-4ea7-8c20-c1286930f85c");
		} catch (AcumosServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void addArtifactTest() {
		Metadata data = new Metadata();
		data.setSolutionId("02a87750-7ba3-4ea7-8c20-c1286930f57c");
		data.setRevisionId("0b1510a2-2f0f-4e59-9783-1606e2e78072");
		data.setModelName("Predictor");
		data.setVersion("3.6.1");
		data.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");

		File outputFolder = new File("tmp", "02a87750-7ba3-4ea7-8c20-c1286930f57c");
		File localmodelFile = new File(outputFolder, "02a87750-7ba3-4ea7-8c20-c1286930f57c");
		
		Mockito.doNothing().when(onboardingStatus).notifyOnboardingStatus("AddToRepository", "ST",
				"Add Artifact for" + "http://cognita-nexus01:8001/" + " Started");
		try {
			commonOnboarding.addArtifact(data, localmodelFile, "MI", "Predictor",onboardingStatus);
		} catch (AcumosServiceException e) {
			e.printStackTrace();
		}
	}*/
}
