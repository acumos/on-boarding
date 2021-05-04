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

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.designstudio.toscagenerator.ToscaGeneratorClient;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.data.UploadArtifactInfo;
//import org.acumos.microservice.FilePathTest;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.AbstractResponseObject;
import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.JsonRequest;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.services.impl.CommonOnboarding;
import org.acumos.onboarding.services.impl.MicroserviceRestClientImpl;
import org.acumos.onboarding.services.impl.OnboardingController;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({OnboardingController.class,CommonOnboarding.class,PortalRestClientImpl.class,CommonDataServiceRestClientImpl.class})
public class OnboardingControllerTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

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

	@Mock
	CommonOnboarding commonOnboarding;

	@Mock
	NexusArtifactClient artifactClient;

	@Mock
	NexusArtifactClient artifactClient1;

	@Mock
	ToscaGeneratorClient client;

	@Mock
	MicroserviceRestClientImpl microserviceClient;

	@Mock
	Environment env;

	private final String url = "http://localhost:8000/ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";

	private static Logger log = LoggerFactory.getLogger(OnboardingController.class);
	LoggerDelegate logger = new LoggerDelegate(log);


	final HttpServletResponse response = mock(HttpServletResponse.class);

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testOnboardingWithAuthenticationFail() throws Exception {

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

		JsonResponse<Object> jsonResponse = new JsonResponse<Object>();

		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = Obj.writeValueAsString(jsonResponse);
		} catch (IOException e) {
			logger.error("Exception occurred while parsing rest page response to string ", e.getMessage());
		}

		Mockito.when(portalClient.loginToAcumos(any(JSONObject.class))).thenReturn(null);
		ResponseEntity<ServiceResponse> result = onboardingController.OnboardingWithAuthentication(cred, response);
		//assertNotNull(result);
	}

	@Test
	public void testOnboardingWithAuthenticationPass() throws Exception {

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

		JsonResponse<Object> jsonResponse = new JsonResponse<Object>();

		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = Obj.writeValueAsString(jsonResponse);
		} catch (IOException e) {
			logger.error("Exception occurred while parsing rest page response to string ", e.getMessage());
		}

		Mockito.when(portalClient.loginToAcumos(any(JSONObject.class))).thenReturn("jwttokena12bc");

		ResponseEntity<ServiceResponse> result = onboardingController.OnboardingWithAuthentication(cred, response);
		//assertNotNull(result);
	}

	/**
	 * Testcase to check invalid metadata json which should recieve failure or exception
	 * @throws Exception
	 */
	@Test
	public void testOnboardModel() throws Exception {

		try {
			MultipartFile multipart = mock(MultipartFile.class);

			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("metadata.json").getFile());
			File file1 = new File(classLoader.getResource("license.json").getFile());

			String filePath = FilePathTest.filePath();
			String fileJson = filePath + "metadata.json";

			FileInputStream modelIS = new FileInputStream(file.getAbsolutePath());
			FileInputStream licenseIS = new FileInputStream(file1.getAbsolutePath());

			MockMultipartFile metaDatazipFile = new MockMultipartFile("file1", "metadata.json", "multipart/form-data",modelIS);
			FileInputStream metataprotoIS = new FileInputStream(file.getAbsolutePath());
			MockMultipartFile protoFile = new MockMultipartFile("file", "model.proto", "multipart/form-data",metataprotoIS);
			MockMultipartFile licenseFile = new MockMultipartFile("file", "license.json", "multipart/form-data",licenseIS);

			FileInputStream metaDataIS = new FileInputStream(file.getAbsolutePath());
			MockMultipartFile metaDataFile = new MockMultipartFile("file", "meta.json", "multipart/form-data",metaDataIS);

			//			CommonDataServiceRestClientImpl cmdDataSvc = mock(CommonDataServiceRestClientImpl.class);
			//			OnboardingNotification onboardingStatus = mock(OnboardingNotification.class);
			//			NexusArtifactClient artifactClient = mock(NexusArtifactClient.class);
			setCdsProperty();

			MLPSolution mlLPSolution = new MLPSolution();
			mlLPSolution.setSolutionId("solutiontest1");
			List<MLPSolution> mlpSoltuionList = new ArrayList<MLPSolution>();
			mlpSoltuionList.add(mlLPSolution);

			JsonResponse<Object> jsonResp = new JsonResponse<Object>();
			jsonResp.setStatus(true);
			jsonResp.setResponseBody("ownerid");

			//			PowerMockito.whenNew(CommonDataServiceRestClientImpl.class)
			//					.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
			//					.thenReturn(cdmsClient);
			//
			//			PowerMockito.whenNew(OnboardingNotification.class)
			//					.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
			//					.thenReturn(onboardingNotification);

			JsonResponse<Object> valid = new JsonResponse<>();
			JsonResponse<Object> responseBody = new JsonResponse<>();
			valid.setStatus(false);
			valid.setResponseBody(responseBody);

			//PowerMockito.whenNew(JsonResponse.class).withNoArguments().thenReturn(valid);

			String authorization = "sampleToken";

			JSONObject obj1 = new JSONObject();
			obj1.put("jwtToken", authorization);
			JSONObject obj2 = new JSONObject();
			obj2.put("request_body", obj1);

			//Mockito.when(commonOnboarding.validate("loginName", "token123")).thenReturn("ownerId");

			Mockito.when(portalClient.tokenValidation(Mockito.anyObject(),Mockito.anyString())).thenReturn(valid);

			MLPTask task = new MLPTask();
			Long taskid = 1234576L;
			task.setTaskId(taskid);

			Mockito.when(cdmsClient.createTask(Mockito.anyObject())).thenReturn(task);

			RestPageResponse<MLPSolution> pageResponse = new RestPageResponse();
			//pageResponse.setNextPage(true);

			Mockito.when(cdmsClient.searchSolutions(Mockito.anyObject(),Mockito.anyBoolean(),Mockito.anyObject())).thenReturn(pageResponse);
			Mockito.when(cdmsClient.createSolution(Mockito.anyObject())).thenReturn(mlLPSolution);

			MLPSolutionRevision mLPSolutionRevision = new MLPSolutionRevision();
			mLPSolutionRevision.setSolutionId("solution1");
			List<MLPSolutionRevision> listSolRev = new ArrayList<MLPSolutionRevision>();
			listSolRev.add(mLPSolutionRevision);
			Mockito.when(cdmsClient.getSolutionRevisions(Mockito.anyString())).thenReturn(listSolRev);
			Mockito.when(cdmsClient.createSolutionRevision(Mockito.anyObject())).thenReturn(mLPSolutionRevision);

			//PowerMockito.whenNew(NexusArtifactClient.class).withArguments(Mockito.anyObject()).thenReturn(artifactClient);

			byte[] buffer = new byte[4000];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
			byteArrayOutputStream.write(buffer);
			//Mockito.when(artifactClient.getArtifact(Mockito.anyString())).thenReturn(byteArrayOutputStream);

			UploadArtifactInfo artifactInfo = new UploadArtifactInfo("org.acumos","org.artifcatid","1.0","jar","orgacumos",515);
			//Mockito.when(artifactClient.uploadArtifact(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyLong(),Mockito.anyObject())).thenReturn(artifactInfo);

			MLPArtifact modelArtifact = new MLPArtifact();
			modelArtifact.setName("fileName");
			modelArtifact.setDescription("fileName");
			modelArtifact.setVersion("1.0");
			modelArtifact.setArtifactTypeCode("typeCode");
			modelArtifact.setUserId("OwnerId");
			modelArtifact.setUri("uri");
			modelArtifact.setSize(515);

			//Mockito.when(cdmsClient.createArtifact(Mockito.anyObject())).thenReturn(modelArtifact);

			//PowerMockito.whenNew(ToscaGeneratorClient.class).withArguments(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString()).thenReturn(client);

			//Mockito.when(client.generateTOSCA(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyObject(),Mockito.anyObject())).thenReturn("done");

			onboardingController.lOG_DIR_LOC = System.getProperty("user.dir");

			//PowerMockito.whenNew(MicroserviceRestClientImpl.class).withArguments(Mockito.anyString()).thenReturn(microserviceClient);

			ResponseEntity<ServiceResponse> response = new ResponseEntity<ServiceResponse>(HttpStatus.OK);

			//Mockito.when(microserviceClient.generateMicroservice(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())).thenReturn(response);

			ResponseEntity<ServiceResponse> resp = onboardingController.onboardModel(mock(HttpServletRequest.class),
					metaDatazipFile, metaDataFile, protoFile, null,null,null, "authorization", false, false, null, "provider", null,null,null,null);

			ResponseEntity<ServiceResponse> resp1 = onboardingController.onboardModel(mock(HttpServletRequest.class),
					metaDatazipFile, metaDataFile, protoFile, null,null,null, "authorization", false, false, "trackingId", "provider", "shareUserName",null,null,"requestId");

			ResponseEntity<ServiceResponse> resp2 = onboardingController.onboardModel(mock(HttpServletRequest.class),
					metaDatazipFile, metaDataFile, protoFile, licenseFile,null,null, "authorization", true, false, null, "provider", null,null,null,null);

			ResponseEntity<ServiceResponse> resp3 = onboardingController.onboardModel(mock(HttpServletRequest.class),
					metaDatazipFile, metaDataFile, protoFile, null, null,null,null, false, false, null, "provider", null,null,null,null);

			logger.info("HttpStatus code:" + resp.getStatusCodeValue() +" \nBody:"+ resp.getBody());
			//assertEquals(201,resp.getStatusCodeValue());
		} catch (AcumosServiceException e) {

			Assert.fail("testdockerizePayloadWtihInavliadMetadata  AcumosServiceException failed : " + e.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail("testdockerizePayloadWtihInavliadMetadata  Exception failed : " + e.getMessage());
		}

	}

	@Test
	public void testAdvancedModel() throws Exception {

		try {
			MultipartFile multipart = mock(MultipartFile.class);

			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("license.json").getFile());
			File file1 = new File(classLoader.getResource("model.onnx").getFile());

			String filePath = FilePathTest.filePath();
			String fileJson = filePath + "metadata.json";
			String modelname = "sampleModel";

			FileInputStream modelIS = new FileInputStream(file1.getAbsolutePath());

			MockMultipartFile metaDatazipFile = new MockMultipartFile("file1", "model.onnx", "multipart/form-data",modelIS);

			FileInputStream licenseIS = new FileInputStream(file.getAbsolutePath());
			MockMultipartFile licenseFile = new MockMultipartFile("file", "license.json", "multipart/form-data",licenseIS);

			//			CommonDataServiceRestClientImpl cmdDataSvc = mock(CommonDataServiceRestClientImpl.class);
			//			OnboardingNotification onboardingStatus = mock(OnboardingNotification.class);
			//			NexusArtifactClient artifactClient = mock(NexusArtifactClient.class);

			setCdsProperty();

			MLPSolution mlLPSolution = new MLPSolution();
			mlLPSolution.setSolutionId("solutiontest1");
			List<MLPSolution> mlpSoltuionList = new ArrayList<MLPSolution>();
			mlpSoltuionList.add(mlLPSolution);

			JsonResponse<Object> jsonResp = new JsonResponse<Object>();
			jsonResp.setStatus(true);
			jsonResp.setResponseBody("ownerid");

			//			PowerMockito.whenNew(CommonDataServiceRestClientImpl.class)
			//					.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
			//					.thenReturn(cmdDataSvc);
			//
			//			PowerMockito.whenNew(OnboardingNotification.class)
			//					.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
			//					.thenReturn(onboardingStatus);

			JsonResponse<Object> valid = new JsonResponse<>();
			JsonResponse<Object> responseBody = new JsonResponse<>();
			valid.setStatus(false);
			valid.setResponseBody(responseBody);

			//PowerMockito.whenNew(JsonResponse.class).withNoArguments().thenReturn(valid);

			String authorization = "sampleToken";

			JSONObject obj1 = new JSONObject();
			obj1.put("jwtToken", authorization);
			JSONObject obj2 = new JSONObject();
			obj2.put("request_body", obj1);

			//Mockito.when(commonOnboarding.validate("loginName", "token123")).thenReturn("ownerId");

			Mockito.when(portalClient.tokenValidation(Mockito.anyObject(),Mockito.anyString())).thenReturn(valid);

			MLPTask task = new MLPTask();
			Long taskid = 1234576L;
			task.setTaskId(taskid);

			Mockito.when(cdmsClient.createTask(Mockito.anyObject())).thenReturn(task);

			RestPageResponse<MLPSolution> pageResponse = new RestPageResponse();
			//pageResponse.setNextPage(true);

			Mockito.when(cdmsClient.searchSolutions(Mockito.anyObject(),Mockito.anyBoolean(),Mockito.anyObject())).thenReturn(pageResponse);
			Mockito.when(cdmsClient.createSolution(Mockito.anyObject())).thenReturn(mlLPSolution);

			MLPSolutionRevision mLPSolutionRevision = new MLPSolutionRevision();
			mLPSolutionRevision.setSolutionId("solution1");
			List<MLPSolutionRevision> listSolRev = new ArrayList<MLPSolutionRevision>();
			listSolRev.add(mLPSolutionRevision);
			Mockito.when(cdmsClient.getSolutionRevisions(Mockito.anyString())).thenReturn(listSolRev);
			Mockito.when(cdmsClient.createSolutionRevision(Mockito.anyObject())).thenReturn(mLPSolutionRevision);

			//PowerMockito.whenNew(NexusArtifactClient.class).withArguments(Mockito.anyObject()).thenReturn(artifactClient);

			byte[] buffer = new byte[4000];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
			byteArrayOutputStream.write(buffer);
			//Mockito.when(artifactClient.getArtifact(Mockito.anyString())).thenReturn(byteArrayOutputStream);

			UploadArtifactInfo artifactInfo = new UploadArtifactInfo("org.acumos","org.artifcatid","1.0","jar","orgacumos",515);
			//Mockito.when(artifactClient.uploadArtifact(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyLong(),Mockito.anyObject())).thenReturn(artifactInfo);

			MLPArtifact modelArtifact = new MLPArtifact();
			modelArtifact.setName("fileName");
			modelArtifact.setDescription("fileName");
			modelArtifact.setVersion("1.0");
			modelArtifact.setArtifactTypeCode("typeCode");
			modelArtifact.setUserId("OwnerId");
			modelArtifact.setUri("uri");
			modelArtifact.setSize(515);

			//Mockito.when(cdmsClient.createArtifact(Mockito.anyObject())).thenReturn(modelArtifact);

			onboardingController.lOG_DIR_LOC = System.getProperty("user.dir");

			//PowerMockito.whenNew(MicroserviceRestClientImpl.class).withArguments(Mockito.anyString()).thenReturn(microserviceClient);

			ResponseEntity<ServiceResponse> response = new ResponseEntity<ServiceResponse>(HttpStatus.OK);

			//Mockito.when(microserviceClient.generateMicroservice(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())).thenReturn(response);

			ResponseEntity<ServiceResponse> resp = onboardingController.advancedModelOnboard(mock(HttpServletRequest.class),
					metaDatazipFile, licenseFile,null, modelname, "authorization", false, false, null, "provider", null,null,null);

			ResponseEntity<ServiceResponse> resp1 = onboardingController.advancedModelOnboard(mock(HttpServletRequest.class),
					metaDatazipFile, licenseFile,null, modelname, "authorization", false, false, null, "provider", "trackingId","requestId", "shareUserName");

			ResponseEntity<ServiceResponse> resp2 = onboardingController.advancedModelOnboard(mock(HttpServletRequest.class),
					metaDatazipFile, licenseFile,null, modelname, null, false, false, null, "provider", null,null,null);

			ResponseEntity<ServiceResponse> resp3 = onboardingController.advancedModelOnboard(mock(HttpServletRequest.class),
					metaDatazipFile, licenseFile,null,modelname, "authorization", true, false, null, "provider", null,null,null);

			logger.info("HttpStatus code:" + resp.getStatusCodeValue() +" \nBody:"+ resp.getBody());
			//assertEquals(201,resp.getStatusCodeValue());
		} catch (AcumosServiceException e) {

			Assert.fail("testdockerizePayloadWtihInavliadMetadata  AcumosServiceException failed : " + e.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail("testdockerizePayloadWtihInavliadMetadata  Exception failed : " + e.getMessage());
		}

	}


	@Test
	public void testAuthenticationException() throws Exception {

		try {
			MultipartFile multipart = mock(MultipartFile.class);

			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("metadata.json").getFile());


			String filePath = FilePathTest.filePath();
			String fileJson = filePath + "metadata.json";

			FileInputStream modelIS = new FileInputStream(file.getAbsolutePath());

			MockMultipartFile metaDatazipFile = new MockMultipartFile("file1", "metadata.json", "multipart/form-data",modelIS);
			FileInputStream metataprotoIS = new FileInputStream(file.getAbsolutePath());
			MockMultipartFile protoFile = new MockMultipartFile("file", "model.proto", "multipart/form-data",metataprotoIS);

			FileInputStream metaDataIS = new FileInputStream(file.getAbsolutePath());
			MockMultipartFile metaDataFile = new MockMultipartFile("file", "meta.json", "multipart/form-data",metaDataIS);

			CommonDataServiceRestClientImpl cmdDataSvc = mock(CommonDataServiceRestClientImpl.class);
			OnboardingNotification onboardingStatus = mock(OnboardingNotification.class);
			NexusArtifactClient artifactClient = mock(NexusArtifactClient.class);

			MLPSolution mlLPSolution = new MLPSolution();
			mlLPSolution.setSolutionId("solutiontest1");
			List<MLPSolution> mlpSoltuionList = new ArrayList<MLPSolution>();
			mlpSoltuionList.add(mlLPSolution);

			JsonResponse<Object> jsonResp = new JsonResponse<Object>();
			jsonResp.setStatus(true);
			jsonResp.setResponseBody("ownerid");

			//			PowerMockito.whenNew(CommonDataServiceRestClientImpl.class)
			//					.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
			//					.thenReturn(cmdDataSvc);

			//			PowerMockito.whenNew(OnboardingNotification.class)
			//					.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
			//					.thenReturn(onboardingStatus);

			JsonResponse<Object> valid = new JsonResponse<>();
			JsonResponse<Object> responseBody = new JsonResponse<>();
			valid.setStatus(false);
			valid.setResponseBody("");

			//PowerMockito.whenNew(JsonResponse.class).withNoArguments().thenReturn(valid);

			String authorization = "sampleToken";

			JSONObject obj1 = new JSONObject();
			obj1.put("jwtToken", authorization);
			JSONObject obj2 = new JSONObject();
			obj2.put("request_body", obj1);

			String loginName = "testUser";
			String jwtToken = "testToken";

			//PowerMockito.when(commonOnboarding.validate(loginName, jwtToken)).thenReturn("");

			//PowerMockito.when(portalClient.tokenValidation(Mockito.anyObject(),Mockito.anyString())).thenReturn(valid);

			ResponseEntity<ServiceResponse> resp = onboardingController.onboardModel(mock(HttpServletRequest.class),
					metaDatazipFile, metaDataFile, protoFile, null,null,null, "authorization", false, false, null, "provider", null,null,null,null);

			logger.info("HttpStatus code:" + resp.getStatusCodeValue() +" \nBody:"+ resp.getBody());
			assertEquals(401,resp.getStatusCodeValue());
		} catch (AcumosServiceException e) {

			Assert.fail("testdockerizePayloadWtihInavliadMetadata  AcumosServiceException failed : " + e.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			//Assert.fail("testdockerizePayloadWtihInavliadMetadata  Exception failed : " + e.getMessage());
		}

	}


	/*@Test
	public void testValidate() {

		try {

			//MLPUser mUser = new MLPUser();//mock(MLPUser.class);
			mUser.setUserId("test");
			mUser.setActive(true);

			CommonDataServiceRestClientImpl cmdDataSvc = mock(CommonDataServiceRestClientImpl.class);

			//PowerMockito.when(cmdDataSvc.loginApiUser("loginName", "token123")).thenReturn(mUser);

			//PowerMockito.whenNew(MLPUser.class).withNoArguments().thenReturn(mUser);

			JsonResponse<Object> jsonResp = new JsonResponse<Object>();
			jsonResp.setStatus(true);
			jsonResp.setResponseBody("ownerid");

			//PowerMockito.when(cmdDataSvc.loginApiUser("loginName", "token123")).thenReturn(mUser);

			//PowerMockito.when(portalClient.loginToAcumos(any(JSONObject.class))).thenReturn("jwttokena12bc");
			PowerMockito.when(portalclient.tokenValidation(obj2, null)).thenReturn(valid);

			onboardingController.validate("loginNameApitoken", "token123");


			PowerMockito.when(mUser.isActive()).thenReturn(true);
			PowerMockito.when(mUser.getUserId()).thenReturn("test");

		} catch (AcumosServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	 */
	@Test
	public void testGetCmnDataSvcEndPoinURL() {
		try {
			onboardingController.getCmnDataSvcEndPoinURL();
		} catch (Exception e) {
			Assert.fail("Failure while testing testGetCmnDataSvcEndPoinURL - " + e.getMessage());
		}
	}

	@Test
	public void testGetCmnDataSvcUser() {
		try {
			onboardingController.getCmnDataSvcUser();
		} catch (Exception e) {
			Assert.fail("Failure while testing testGetCmnDataSvcUser - " + e.getMessage());
		}
	}

	@Test
	public void testGetCmnDataSvcPwd() {
		try {
			onboardingController.getCmnDataSvcPwd();
		} catch (Exception e) {
			Assert.fail("Failure while testing testGetCmnDataSvcPwd - " + e.getMessage());
		}

	}

	private void setCdsProperty() {
		when(env.getProperty("cmndatasvc.cmnDataSvcEndPoinURL")).thenReturn(url);
		when(env.getProperty("cmndatasvc.cmnDataSvcUser")).thenReturn(user);
		when(env.getProperty("cmndatasvc.cmnDataSvcPwd")).thenReturn(pass);
	}
}
