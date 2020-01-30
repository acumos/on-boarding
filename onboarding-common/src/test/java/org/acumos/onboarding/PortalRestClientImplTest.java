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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.IOException;

import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class PortalRestClientImplTest {

	public static Logger log = LoggerFactory.getLogger(PortalRestClientImplTest.class);
	LoggerDelegate logger = new LoggerDelegate(log);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	public static final String LOCAL_HOST = "http://localhost:8000/";
	public static final String GENERATE_MS = "/v2/generateMicroservice";

	@InjectMocks
	PortalRestClientImpl objRestClient =new PortalRestClientImpl(LOCAL_HOST);

	@Before
	public void setup()  {
		new PortalRestClientImpl();
		new PortalRestClientImpl(LOCAL_HOST,"techmdev","Root1234");
	}

	@Test
	public void tokenValidation() {

		JSONObject obj1 = new JSONObject();
		obj1.put("jwtToken", "tokenstr");

		JSONObject obj2 = new JSONObject();
		obj2.put("request_body", obj1);

		try {

			JsonResponse<Object> jsonResponse = new JsonResponse<Object>();

			ObjectMapper Obj = new ObjectMapper();
			String jsonStr = null;
			try {
				jsonStr = Obj.writeValueAsString(jsonResponse);
			} catch (IOException e) {
				logger.error("Exception occurred while parsing rest page response to string ", e.getMessage());
			}

			stubFor(post(urlEqualTo("/auth/validateToken")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody(jsonStr)));

			objRestClient.tokenValidation(obj2, null);
			logger.debug("tokenValidation success");

		} catch (Exception e) {
			Assert.fail("tokenValidation failed : " + e.getMessage());
		}
	}

	@Test
	public void loginToAcumosTest() {

		JSONObject obj1 = new JSONObject();
		obj1.put("jwtToken", "tokenstr");

		JSONObject obj2 = new JSONObject();
		obj2.put("request_body", obj1);

		try {

			JsonResponse<Object> jsonResponse = new JsonResponse<Object>();

			ObjectMapper Obj = new ObjectMapper();
			String jsonStr = null;
			try {
				jsonStr = Obj.writeValueAsString(jsonResponse);
			} catch (IOException e) {
				logger.error("Exception occurred while parsing rest page response to string ", e.getMessage());
			}

			stubFor(post(urlEqualTo("/auth/jwtToken")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
					.withBody(jsonStr)));

			objRestClient.loginToAcumos(obj2);
			logger.debug("tokenValidation success");

		} catch (Exception e) {
			Assert.fail("tokenValidation failed : " + e.getMessage());
		}

	}
}
