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

import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.component.docker.cmd.TagImageCommand;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TagImageCommandTest {

	public static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(TagImageCommandTest.class);

	TagImageCommand tagImageCommand = new TagImageCommand("H2O", "Nexus", "H2O", true, true);

	@Test
	public void getDisplayName() {
		try {

			Assert.assertNotNull(tagImageCommand.getImage());
		} catch (Exception e) {
			Assert.fail("tagImageCommand failed : " + e.getMessage());

		}
	}

	@Test
	public void getImage() {

		tagImageCommand.getImage();
		tagImageCommand.getDisplayName();
		tagImageCommand.getIgnoreIfNotFound();
		tagImageCommand.getRepository();
		tagImageCommand.getTag();
		tagImageCommand.getWithForce();
		Assert.assertNotNull(tagImageCommand);
	}
}
