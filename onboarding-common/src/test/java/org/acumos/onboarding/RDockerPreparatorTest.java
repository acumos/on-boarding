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
import org.acumos.onboarding.component.docker.preparation.RDockerPreparator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class RDockerPreparatorTest {

	String filePath = FilePathTest.filePath(); 
	File jsonFile = new File(filePath+"modelDetails.json");

	public RDockerPreparatorTest() throws AcumosServiceException {
		new MetadataParser(jsonFile);
	}

	MetadataParser metadataParser = new MetadataParser(jsonFile);
	private String httpProxy= "http://10.1.0.6:3128";

	@Mock
	RDockerPreparator rDockerPreparator = new RDockerPreparator(metadataParser, httpProxy);

	@Test
	public void compareVersionTest() {

		int[] baseVersion = { 1, 2, 3 };
		int[] currentVersion = { 4, 5, 6 };
		int result = RDockerPreparator.compareVersion(baseVersion, currentVersion);
		Assert.assertEquals(result,-1);
	}

	@Test
	public void versionAsArrayTest() {

		int[] baseVersion = RDockerPreparator.versionAsArray("1234");
		Assert.assertNotNull(baseVersion);
	}

	@Test
	public void prepareDockerAppTest(){

		try {
			File f1 = new File(FilePathTest.filePath());
			doNothing().when(rDockerPreparator).prepareDockerApp(f1);
		} catch (AcumosServiceException e) {
			Assert.fail("prepareDockerAppTest failed : " + e.getMessage());
		}

  }

}

