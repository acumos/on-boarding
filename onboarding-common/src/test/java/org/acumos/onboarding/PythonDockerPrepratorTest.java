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

import java.io.File;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.acumos.onboarding.component.docker.preparation.PythonDockerPreprator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(MockitoJUnitRunner.class)
public class PythonDockerPrepratorTest {
	
	String filePath = FilePathTest.filePath();
	 
	File jsonFile = new File(filePath+"metadata.json");
	File srcFile = new File(filePath+"Dockerfile");
	File outFile = new File(filePath+"Dockerfile");
	File outFolder = new File(filePath);
	File reqtxt = new File(filePath+"requirements.txt");
	MetadataParser metadataParser = new MetadataParser(jsonFile);
	private JsonNode metadataJson = metadataParser.getMetadataJson();
	
	private String httpProxy= "http://10.1.0.6:3128";
	
	//@InjectMocks
	PythonDockerPreprator pythonDockerPreprator = new PythonDockerPreprator(metadataParser, "localhost", "localhost",httpProxy);
	PythonDockerPreprator pythonDockerPrepratorSpy = Mockito.spy(pythonDockerPreprator);
	
	private static final String METHOD1 = "findPredictMethod";
	private static final String METHOD2 = "prepareYaml";
	private static final String METHOD3 = "createRequirementTxt";

	public PythonDockerPrepratorTest() throws AcumosServiceException {
		new MetadataParser(jsonFile);
	} 

	@Test
	public void compareVersionTest() {

		int[] baseVersion = { 1, 2, 3 };
		int[] currentVersion = { 4, 5, 6 };
		int result = PythonDockerPreprator.compareVersion(baseVersion, currentVersion);
		Assert.notNull(result, "");
	}

	@Test
	public void versionAsArrayTest() {

		int[] baseVersion = PythonDockerPreprator.versionAsArray("1234");
		Assert.notNull(baseVersion, "");
		
	}

	@Test
	public void prepareDockerAppTest() throws AcumosServiceException {
		
		pythonDockerPreprator.prepareDockerAppV2(outFolder);
	}
		
	@Test
	public void createRequirementTxtTest() throws Exception {
		
		pythonDockerPreprator.createRequirementTxt(reqtxt, reqtxt);
		
	}
	
}
