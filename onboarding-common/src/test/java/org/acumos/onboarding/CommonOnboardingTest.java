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

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.designstudio.toscagenerator.ToscaGeneratorClient;
import org.acumos.designstudio.toscagenerator.exceptionhandler.AcumosException;
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
	
	@Mock
	ToscaGeneratorClient toscaClient ;
	
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
		
		JSONObject obj1 = new JSONObject();
		obj1.put("jwtToken", token);
		JSONObject obj2 = new JSONObject();
		obj2.put("request_body", obj1);
		Mockito.when(client.tokenValidation(obj2, "GitHub")).thenReturn(valid);

		try {
			commonOnboarding.validate(token, "GitHub");
		} catch (AcumosServiceException e) {
			logger.info("Exception occured while validateTest()" + e.getMessage());
		}
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
		
		Mockito.when(cdsClientImpl.createSolutionRevision(Mockito.any(MLPSolutionRevision.class))).thenReturn(revision);
		try {
			commonOnboarding.createSolutionRevision(data);
		} catch (AcumosServiceException e) {
			logger.info("Exception occured while createSolutionRevisionTest()" + e.getMessage());
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
			commonOnboarding.createSolution(data,null);
		} catch (AcumosServiceException e) {
			logger.info("Exception occured while createSolutionTest()" + e.getMessage());
		}
	}
	
	@Test
	public void generateTOSCATest() {
		Metadata data = new Metadata();
		data.setSolutionId("02a87750-7ba3-4ea7-8c20-c1286930f57c");
		data.setRevisionId("0b1510a2-2f0f-4e59-9783-1606e2e78072");
		data.setModelName("Predictor");
		data.setVersion("3.6.1");
		data.setOwnerId("361de562-2e4d-49d7-b6a2-b551c35050e6");

		File localProtobufFile = new File("pathname");
		File localMetadataFile = new File("path");

		try {
			when(toscaClient.generateTOSCA(data.getOwnerId(), data.getSolutionId(), data.getVersion(),
					data.getRevisionId(), localProtobufFile, localMetadataFile)).thenReturn("result");
			commonOnboarding.generateTOSCA(localProtobufFile, localMetadataFile, data, onboardingStatus);
		} catch (AcumosException e) {
			logger.info("Exception occured while generateTOSCATest()" + e.getMessage());
		}
	}
	
	@Test
	public void listFilesAndFilesSubDirectories(){
		File file=new File("onboarding-app/src/test/java/org/acumos/onboarding");
		file.mkdirs();
		File f1 = new File(file.getPath() + File.separator + "testFile");
		Path path=FileSystems.getDefault().getPath(f1.getPath()+"/testFile");
     	try {
			f1.createNewFile();
			commonOnboarding.listFilesAndFilesSubDirectories(file);
	     	Files.deleteIfExists(path);
		} catch (IOException e) {
			logger.info("Exception occured while listFilesAndFilesSubDirectories()" + e.getMessage());
		}
     	
	}
	
}
