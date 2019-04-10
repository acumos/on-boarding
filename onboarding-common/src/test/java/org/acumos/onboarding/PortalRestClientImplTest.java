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
import org.acumos.onboarding.services.PortalRestClient;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class PortalRestClientImplTest {
	
	public static Logger log = LoggerFactory.getLogger(PortalRestClientImplTest.class);
	LoggerDelegate logger = new LoggerDelegate(log);
	
	@Mock
	PortalRestClient portalRestClient;

	@InjectMocks
	PortalRestClientImpl objRestClient = new PortalRestClientImpl();

	
	
	public PortalRestClientImplTest() throws AcumosServiceException {
		new PortalRestClientImpl("http://acumos-dev1-vm01-core:8083");
		new PortalRestClientImpl("http://acumos-dev1-vm01-core:8083","techmdev","Root1234");
	}
	
	@Test
	public void tokenValidation() {

		JSONObject obj1 = new JSONObject();
		obj1.put("jwtToken", "tokenstr");

		JSONObject obj2 = new JSONObject();
		obj2.put("request_body", obj1);

		try {

			portalRestClient.tokenValidation(obj2, null);
			logger.debug("tokenValidation success");

		} catch (Exception e) {
			Assert.fail("tokenValidation failed : " + e.getMessage());
		}
	}
}
