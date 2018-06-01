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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;

import java.io.File;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.acumos.onboarding.component.docker.preparation.PythonDockerPreprator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PythonDockerPrepratorTest {
	
	String filePath = FilePathTest.filePath();
	 
	File jsonFile = new File(filePath+"metadata.json");
	File srcFile = new File(filePath+"Dockerfile");
	File outFile = new File(filePath+"Dockerfile");
	File outFolder = new File(filePath);
	File reqtxt = new File(filePath+"requirements.txt");
	MetadataParser metadataParser = new MetadataParser(jsonFile);
	
	@Mock
	PythonDockerPreprator pythonDockerPreprator;
	
	public PythonDockerPrepratorTest() throws AcumosServiceException {
		new MetadataParser(jsonFile);
	} 

	@Test
	public void compareVersionTest() {

		int[] baseVersion = { 1, 2, 3 };
		int[] currentVersion = { 4, 5, 6 };
		int result = PythonDockerPreprator.compareVersion(baseVersion, currentVersion);
		assertNotNull(result);
	}

	@Test
	public void versionAsArrayTest() {

		int[] baseVersion = PythonDockerPreprator.versionAsArray("1234");
		assertNotNull(baseVersion);
		
	}

	@Test
	public void prepareDockerAppTest() throws AcumosServiceException {
		
		doNothing().when(pythonDockerPreprator).prepareDockerAppV2(outFolder);
	}
		
	@Test
	public void createRequirementTxtTest() throws Exception {
		
		doNothing().when(pythonDockerPreprator).createRequirementTxt(reqtxt, reqtxt);
		
	}
	
}
