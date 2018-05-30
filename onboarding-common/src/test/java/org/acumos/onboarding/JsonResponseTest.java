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

import org.acumos.onboarding.common.utils.JsonResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 
 * @author ****
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonResponseTest {

	
	@Test
	public void jsonrepsonseTest() {
		JsonResponse<?> jsonResponse = new JsonResponse();
		jsonResponse.setStatus(true);
		jsonResponse.setResponseDetail("success");
		jsonResponse.setErrorCode("Success");
		jsonResponse.setResponseCode("success");
		jsonResponse.setStatusCode(200);
		jsonResponse.getErrorCode();
		jsonResponse.getResponseBody();
		jsonResponse.getResponseCode();
		jsonResponse.getResponseDetail();
		jsonResponse.getStatus();
		jsonResponse.getStatusCode();
		Assert.assertNotNull(jsonResponse);

	}

}
