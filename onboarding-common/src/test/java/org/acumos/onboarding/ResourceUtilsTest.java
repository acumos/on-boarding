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

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.common.utils.ResourceUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

@RunWith(MockitoJUnitRunner.class)
public class ResourceUtilsTest {

	private static final Logger log = LoggerFactory.getLogger(ResourceUtilsTest.class);	
	LoggerDelegate logger = new LoggerDelegate(log);
	private ResourceLoader resourceLoader;
	ResourceUtils resourceUtils = new ResourceUtils(resourceLoader);

	String path = "samplePath";
	String pattern = "samplePath";

	@Test
	public void getResourceTest() {

		try {
			resourceUtils.getResource(path);
			Assert.assertNotNull(resourceUtils);

		} catch (AcumosServiceException e) {
			Assert.fail("getResourceTest failed : " + e.getMessage());

		}
	}

	@Test
	public void loadResourcesTest() {
		try {
			resourceUtils.loadResources(path);
			ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern);
			Assert.assertNotNull(resourceUtils);
		}  catch (Exception e) {
			Assert.fail("loadResourcesTest failed : " + e.getMessage());

		} 
	}

	@Test
	public void isResourceExistsTest() {
		try {
			resourceUtils.isResourceExists(path);
		} catch (AcumosServiceException e) {
			Assert.fail("isResourceExistsTest failed : " + e.getMessage());
			
		}
	}

}
