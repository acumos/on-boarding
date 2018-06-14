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

import org.acumos.onboarding.component.docker.cmd.PushImageCommand;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 
 * @author *****
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PushImageCommandTest {

	PushImageCommand pushImageCommand = new PushImageCommand("H2O", "1.0.0-SNAPSHOT", "Nexus");

	@Test
	public void getDisplayName() {
		try {
			pushImageCommand.getDisplayName();
		} catch (Exception e) {
			Assert.fail("getDisplayName failed : " + e.getMessage());
		}
	}

	@Test
	public void getImage() {
		pushImageCommand.getImage();
	}

	@Test
	public void getTag() {
		pushImageCommand.getTag();
	}

	@Test
	public void getRegistry() {
		pushImageCommand.getRegistry();
	}
	
}
