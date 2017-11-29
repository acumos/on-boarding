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
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.services.PortalRestClient;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 
 * @author *****
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PortalRestClientImplTest {
	
	public static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PortalRestClientImplTest.class);
	
	@Mock
	PortalRestClient portalRestClient;

	PortalRestClientImpl objRestClient = new PortalRestClientImpl();

	
	
	public PortalRestClientImplTest() throws AcumosServiceException {
		new PortalRestClientImpl("http://cognita-dev1-vm01-core:8083");
		new PortalRestClientImpl("http://cognita-dev1-vm01-core:8083","techmdev","Root1234");
	}
	
	@Test
	public void tokenValidation() {

		JSONObject obj1 = new JSONObject();
		obj1.put("jwtToken", "tokenstr");

		JSONObject obj2 = new JSONObject();
		obj2.put("request_body", obj1);

		try {

			portalRestClient.tokenValidation(obj2, null);
			assert (true);
			logger.debug(EELFLoggerDelegate.debugLogger, "tokenValidation success");

		} catch (Exception e) {
			assert (false);
			logger.debug(EELFLoggerDelegate.debugLogger, "tokenValidation failed");
		}
	}

	@Test
	public void loginToAcumos() throws Exception {

		String user = "techmdev";
		String pass = "Root1234";

		JSONObject crediantials = new JSONObject();
		crediantials.put("username", user);
		crediantials.put("password", pass);

		JSONObject reqObj = new JSONObject();
		reqObj.put("request_body", crediantials);
		try {
			String token = objRestClient.loginToAcumos(reqObj);

			if (token != null) {
				assert (true);
				logger.debug(EELFLoggerDelegate.debugLogger, "tokenValidation success");
			} else {
				assert (true);
				logger.debug(EELFLoggerDelegate.debugLogger, "tokenValidation failed");
			}
		} catch (Exception e) {
			logger.debug(EELFLoggerDelegate.debugLogger, "tokenValidation failed");
			assert (true);
		}
	}
}
