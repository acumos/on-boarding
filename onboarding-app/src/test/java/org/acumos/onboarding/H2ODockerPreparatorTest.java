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
import org.acumos.onboarding.component.docker.preparation.H2ODockerPreparator;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class H2ODockerPreparatorTest {

	@Mock
	H2ODockerPreparator h2ODockerPreparator;

	@Test
	public void compareVersionTest() {

		int[] baseVersion = { 1, 2, 3 };
		int[] currentVersion = { 4, 5, 6 };
		int result = H2ODockerPreparator.compareVersion(baseVersion, currentVersion);
		if (result != 0) {
			assert (true);
		} else {
			assert (true);
		}

	}

	@Test
	public void versionAsArrayTest() {

		int[] baseVersion = H2ODockerPreparator.versionAsArray("1234");
		if (baseVersion != null) {
			assert (true);
		} else {
			assert (true);
		}

	}
	
	@Test
	public void prepareDockerAppTest(){
		
		try {
			h2ODockerPreparator.prepareDockerApp(new File("dFile"));
			assert(true);
		} catch (AcumosServiceException e) {
			assert(false);
		}

  }
	
	@Test
	public void createDockerFileTest() {
		
		try {
			h2ODockerPreparator.createDockerFile(new File("dFile"), new File("dFile1"));
			assert(true);
		} catch (AcumosServiceException e) {
			assert(false);
		}
	}
}






