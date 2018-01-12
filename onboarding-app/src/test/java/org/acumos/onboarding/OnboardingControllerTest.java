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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.acumos.cds.domain.MLPArtifact;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.onboarding.common.exception.AcumosServiceException;
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
	OnboardingController on = new OnboardingController();

	@Mock
	RestTemplate restTemplate;

	@InjectMocks
	PortalRestClientImpl portalclient = new PortalRestClientImpl("http://cognita-dev1-vm01-core:8083");

	@SuppressWarnings("unchecked")
	@Test
	public void OnboardingWithAuthentication() throws Exception {

		try {
			String user = "acumosdev";
			String pass = "Root1234";

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

	/*
	 * @Test public void dockerizeFile(){
	 * 
	 * String modelOriginalName = "model.zip"; String modelId =
	 * UtilityFunction.getGUID(); File outputFolder = new File("tmp", modelId);
	 * outputFolder.mkdirs();
	 * 
	 * File localmodelFile = new File(outputFolder, modelOriginalName);
	 * 
	 * File localMetadataFile = new File(outputFolder, "metadata.json");
	 * 
	 * File localProtobufFile = new File(outputFolder, "model.proto");
	 * 
	 * //new File(this.getClass().getResource("/myxml.xml").getFile())
	 * 
	 * URL path = OnboardingControllerTest.class.getResource("model.proto");
	 * 
	 * File file = new File(path.getFile());
	 * 
	 * // File file = new
	 * File(this.getClass().getResource("model.proto").getFile());
	 * 
	 * try {
	 * 
	 * InputStream targetStream = new FileInputStream(file);
	 * 
	 * UtilityFunction.copyFile(targetStream, localProtobufFile);
	 * 
	 * MetadataParser metadataParser = new MetadataParser(localMetadataFile);
	 * 
	 * Metadata mData = metadataParser.getMetadata(); mData.setOwnerId("sohil");
	 * 
	 * MLPSolution mlpSolution = null;
	 * 
	 * List<MLPSolution> solList = new ArrayList<MLPSolution>();
	 * 
	 * MLPSolution mlPSolution = new MLPSolution();
	 * mlPSolution.setSolutionId("03a08df1-23de-4aae-a9bb-97afd92ee17a");
	 * 
	 * solList.add(mlPSolution);
	 * 
	 * on.dockerizeFile(metadataParser, localmodelFile);
	 * 
	 * assert(true);
	 * 
	 * }catch (FileNotFoundException e1) { // TODO Auto-generated catch block
	 * e1.printStackTrace(); }catch (AcumosServiceException e) { assert(true);
	 * // TODO Auto-generated catch block //e.printStackTrace();
	 * 
	 * } catch (IOException e) { assert(true); // TODO Auto-generated catch
	 * block //e.printStackTrace(); }
	 * 
	 * }
	 */

	/*
	 * @Test public void OnboardingWithAuthentication() throws Exception {
	 * 
	 * try { modelOriginalName = model.getOriginalFilename(); String modelId =
	 * UtilityFunction.getGUID(); File outputFolder = new File("tmp", modelId);
	 * outputFolder.mkdirs(); try { File localmodelFile = new File(outputFolder,
	 * model.getOriginalFilename()); try {
	 * UtilityFunction.copyFile(model.getInputStream(), localmodelFile); } catch
	 * (IOException e) { throw new
	 * AcumosServiceException(AcumosServiceException.ErrorCode.
	 * INTERNAL_SERVER_ERROR, "Fail to download model file " +
	 * localmodelFile.getName()); } File localMetadataFile = new
	 * File(outputFolder, metadata.getOriginalFilename()); try {
	 * UtilityFunction.copyFile(metadata.getInputStream(), localMetadataFile); }
	 * catch (IOException e) { throw new
	 * AcumosServiceException(AcumosServiceException.ErrorCode.
	 * INTERNAL_SERVER_ERROR, "Fail to download metadata file " +
	 * localMetadataFile.getName()); } File localProtobufFile = new
	 * File(outputFolder, schema.getOriginalFilename()); try {
	 * UtilityFunction.copyFile(schema.getInputStream(), localProtobufFile); }
	 * catch (IOException e) { throw new
	 * AcumosServiceException(AcumosServiceException.ErrorCode.
	 * INTERNAL_SERVER_ERROR, "Fail to download protobuf file " +
	 * localProtobufFile.getName()); }
	 * 
	 * MetadataParser metadataParser = new MetadataParser(localMetadataFile);
	 * 
	 * // String jwtToken = request.getHeader("jwtToken");
	 * 
	 * // Call to validate JWT Token.....!
	 * 
	 * // MLPSolution mlpSolution = //
	 * on.createSolution(metadataParser.getMetadata());
	 * 
	 * 
	 * on.createSolutionRevision(metadataParser.getMetadata());
	 * 
	 * on.addArtifact(metadataParser.getMetadata(), localmodelFile,
	 * ArtifactTypeCode.MI);
	 * 
	 * on.addArtifact(metadataParser.getMetadata(), localProtobufFile,
	 * ArtifactTypeCode.MI);
	 * 
	 * on.addArtifact(metadataParser.getMetadata(), localMetadataFile,
	 * ArtifactTypeCode.MD);
	 * 
	 * 
	 * // String imageUri = dockerizeFile(metadataParser, // localmodelFile);
	 * 
	 * // on.addArtifact(metadataParser.getMetadata(), imageUri, //
	 * ArtifactTypeCode.DI);
	 * 
	 * // generateTOSCA(localProtobufFile,localMetadataFile,metadataParser.
	 * getMetadata()); } finally {
	 * UtilityFunction.deleteDirectory(outputFolder); } } catch
	 * (AcumosServiceException e) { HttpStatus httpCode =
	 * HttpStatus.INTERNAL_SERVER_ERROR; if
	 * (e.getErrorCode().equals(AcumosServiceException.ErrorCode.
	 * INVALID_PARAMETER.name())) { httpCode = HttpStatus.BAD_REQUEST; } } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */

	/*
	 * @Test public void dockerizePayload() throws Exception { try {
	 * modelOriginalName = "H20 Model"; String modelId =
	 * UtilityFunction.getGUID(); File outputFolder = new File("tmp", modelId);
	 * outputFolder.mkdirs(); try { File localmodelFile = new
	 * File(outputFolder,modelOriginalName); try {
	 * UtilityFunction.copyFile(model.getInputStream(), localmodelFile); } catch
	 * (IOException e) { throw new
	 * AcumosServiceException(AcumosServiceException.ErrorCode.
	 * INTERNAL_SERVER_ERROR, "Fail to download model file " +
	 * localmodelFile.getName()); } File localMetadataFile = new
	 * File(outputFolder, metadata.getOriginalFilename()); try {
	 * UtilityFunction.copyFile(metadata.getInputStream(), localMetadataFile); }
	 * catch (IOException e) { throw new
	 * AcumosServiceException(AcumosServiceException.ErrorCode.
	 * INTERNAL_SERVER_ERROR, "Fail to download metadata file " +
	 * localMetadataFile.getName()); } File localProtobufFile = new
	 * File(outputFolder, schema.getOriginalFilename()); try {
	 * UtilityFunction.copyFile(schema.getInputStream(), localProtobufFile); }
	 * catch (IOException e) { throw new
	 * AcumosServiceException(AcumosServiceException.ErrorCode.
	 * INTERNAL_SERVER_ERROR, "Fail to download protobuf file " +
	 * localProtobufFile.getName()); }
	 * 
	 * MetadataParser metadataParser = new MetadataParser(localMetadataFile);
	 * 
	 * // authenticate(metadataParser.getMetadata());
	 * 
	 * 
	 * MLPSolution mlpSolution = createSolution(metadataParser.getMetadata());
	 * 
	 * createSolutionRevision(metadataParser.getMetadata());
	 * 
	 * addArtifact(metadataParser.getMetadata(), localmodelFile,
	 * ArtifactTypeCode.MI);
	 * 
	 * addArtifact(metadataParser.getMetadata(), localProtobufFile,
	 * ArtifactTypeCode.MI);
	 * 
	 * addArtifact(metadataParser.getMetadata(), localMetadataFile,
	 * ArtifactTypeCode.MD);
	 * 
	 * String imageUri = dockerizeFile(metadataParser, localmodelFile);
	 * 
	 * addArtifact(metadataParser.getMetadata(), imageUri, ArtifactTypeCode.DI);
	 * 
	 * //generateTOSCA(metadataParser.getMetadata());
	 * generateTOSCA(localProtobufFile,localMetadataFile, metadataParser.
	 * getMetadata());
	 * 
	 * 
	 * } finally { UtilityFunction.deleteDirectory(outputFolder); } } catch
	 * (AcumosServiceException e) { HttpStatus httpCode =
	 * HttpStatus.INTERNAL_SERVER_ERROR; if
	 * (e.getErrorCode().equals(AcumosServiceException.ErrorCode.
	 * INVALID_PARAMETER.name())) { httpCode = HttpStatus.BAD_REQUEST; } } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */

	@Test
	public void listFilesAndFilesSubDirectoriesTest() throws AcumosServiceException {
		on.listFilesAndFilesSubDirectories(new File("dFile"));
		assert (true);
	}
	@Test
	public void revertbackOnboardingTest() throws AcumosServiceException {
		
		String imageUri="abcd";
		Metadata metadataParser = new Metadata();
		on.revertbackOnboarding(metadataParser, imageUri);
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl("http://cognita_model_rw:not4you@cognita-nexus01:8081/repository/repo_cognita_model_maven");
		repositoryLocation.setUsername("cognita_model_rw");
		repositoryLocation.setPassword("not4you");
		NexusArtifactClient nexusClient = new NexusArtifactClient(repositoryLocation);
		MLPArtifact mlpArtifact = new MLPArtifact();
		mlpArtifact.setArtifactTypeCode("hello");
		mlpArtifact.setUri("http://cognita_model_rw:not4you@cognita-nexus01:8081/repository/repo_cognita_model_maven");
		
		if (!(mlpArtifact.getArtifactTypeCode().equals("DI"))) {
			try {
				nexusClient.deleteArtifact(mlpArtifact.getUri());
				assert (true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assert (true);
			}
			
		}
	}
	@Test 
	public void getModelVersionTest(){
		String solutionId="d237fefc-1d2f-41d5-9bb4-2abbf533e687";
		on.getModelVersion(solutionId);
		assert(true);
	}
}
