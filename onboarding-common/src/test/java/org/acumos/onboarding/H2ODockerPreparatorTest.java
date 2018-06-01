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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.File;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.component.docker.preparation.H2ODockerPreparator;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

@RunWith(MockitoJUnitRunner.class)
public class H2ODockerPreparatorTest {

	 
	String filePath = FilePathTest.filePath(); 
	File outFolder = new File(filePath);
	File jsonFile = new File(filePath+"modelDetails.json");
	File reqtxt = new File(filePath+"requirements.txt");
	File srcFile = new File(filePath+"Dockerfile");
	
	public H2ODockerPreparatorTest() throws AcumosServiceException {
		new MetadataParser(jsonFile);
	}
	
	MetadataParser metadataParser = new MetadataParser(jsonFile);
	
	@Mock
	H2ODockerPreparator h2ODockerPreparator = new H2ODockerPreparator(metadataParser);

	@Test
	public void compareVersionTest() {

		int[] baseVersion = { 1, 2, 3 };
		int[] currentVersion = { 4, 5, 6 };
		int result = H2ODockerPreparator.compareVersion(baseVersion, currentVersion);
		assertNotNull(result);
	}

	@Test
	public void versionAsArrayTest() {

		int[] baseVersion = H2ODockerPreparator.versionAsArray("1234");
		assertNotNull(baseVersion);
	}
	
	@Test
	public void prepareDockerAppTest() throws AcumosServiceException {
		
		doNothing().when(h2ODockerPreparator).prepareDockerApp(outFolder);

	}
	
	@Test
	public void createDockerFileTest() throws AcumosServiceException {
		
		doNothing().when(h2ODockerPreparator).createDockerFile(srcFile, srcFile);

	}
	
	@Test
	public void createRequirementsTest() throws AcumosServiceException {
		doNothing().when(h2ODockerPreparator).createRequirements(reqtxt, reqtxt);
		
	}
}