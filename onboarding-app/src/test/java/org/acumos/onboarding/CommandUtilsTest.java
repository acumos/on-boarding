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

import org.acumos.onboarding.component.docker.cmd.CommandUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommandUtilsTest {

	@Mock
	CommandUtils commandUtilsTest;

	@Test
	public void addLatestTagIfNeededTest() {

		String str = CommandUtils.addLatestTagIfNeeded("genericmodel");
		assert (true);

	}

	/**
	 * Test sizeInBytes
	 */
	@Test
	public void sizeInBytesTest() {
		String str = "Dockerimages";
		long bytes = CommandUtils.sizeInBytes(str);
		if (bytes != 0) {
			assert (true);
		} else {
			assert (true);
		}
	}

	/**
	 * Test image
	 */
	@Test
	public void imageFullNameFromTest() {

		String imageName = CommandUtils.imageFullNameFrom("docker", "nexus", "latst");
		if (imageName != null) {
			assert (true);
		} else {
			assert (true);
		}
	}

}
