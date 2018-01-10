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
import org.acumos.onboarding.common.utils.JSONTags;
import org.acumos.onboarding.services.impl.OnboardingController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import ch.qos.logback.classic.Logger;

@RunWith(MockitoJUnitRunner.class)
public class JSONTagsTest {
	
	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(JSONTags.class);
	
	@Test
	public void jsonTagTest() {
		JSONTags jSONTagsObj;
		
		logger.info(JSONTags.TAG_REQUEST_FNAME);
		logger.info(JSONTags.TAG_REQUEST_MNAME);
		logger.info(JSONTags.TAG_STATUS_SUCCESS);
		logger.info(JSONTags.TAG_RESPONSE_STATUS);
		logger.info(JSONTags.TAG_RESPONSE_STATUS_CODE);
		logger.info(JSONTags.TAG_REQUEST_FROM);
		logger.info(JSONTags.TAG_REQUEST_ID);
		
		logger.info(JSONTags.TAG_REQUEST_BODY);
		logger.info(JSONTags.TAG_REQUEST_UNIQUE_ID);
		logger.info(JSONTags.TAG_STATUS_FAILURE);
		logger.info(JSONTags.TAG_REQUEST_UID);
		
		logger.info(JSONTags.TAG_RESPONSE_DETAIL);
		logger.info(JSONTags.TAG_RESPONSE_BODY);
		logger.info(JSONTags.TAG_RESPONSE_MESSAGE);
		logger.info(JSONTags.TAG_ERROR_CODE);
		logger.info(JSONTags.TAG_RESPONSE_CODE);
		logger.info(JSONTags.TAG_ERROR_CODE_SUCCESS);
		logger.info(JSONTags.TAG_ERROR_CODE_FAILURE);
		logger.info(JSONTags.TAG_ERROR_CODE_EXCEPTION);
		logger.info(JSONTags.TAG_ERROR_CODE_RESET_USERNAME);
		logger.info(JSONTags.TAG_ERROR_CODE_RESET_EMAILID);
		logger.info(JSONTags.TAG_ERROR_CODE_OLDPASS_NOTMATCH);
		
		logger.info(JSONTags.ROLE_ID);
		logger.info(JSONTags.ROLE_NAME);
		logger.info(JSONTags.ROLE_ACTIVE);
		logger.info(JSONTags.ROLE_MODIFIED);
		logger.info(JSONTags.ROLE_MODIFIED);
		
		
	}

}
