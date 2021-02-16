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
package org.acumos.onboarding.services.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.acumos.cds.client.HttpComponentsClientHttpRequestFactoryBasicAuth;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.services.MicroserviceRestClient;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class MicroserviceRestClientImpl implements MicroserviceRestClient{

	private static Logger log = LoggerFactory.getLogger(MicroserviceRestClientImpl.class);
	LoggerDelegate logger = new LoggerDelegate(log);

	private final String baseUrl;
	private final RestTemplate restTemplate;

	/**
	 *
	 */
	public MicroserviceRestClientImpl() {
		baseUrl = "";
		restTemplate = null;
		logger.debug("In MicroserviceRestClientImpl() ");
	}

		public MicroserviceRestClientImpl(String webapiUrl) {

			if (webapiUrl == null)
				throw new IllegalArgumentException("Null URL not permitted");

			URL url = null;
			try {
				url = new URL(webapiUrl);
				baseUrl = url.toExternalForm();
				logger.debug("In MicroserviceRestClientImpl(String webapiUrl) "+baseUrl);
			} catch (MalformedURLException ex) {
				throw new RuntimeException("Failed to parse URL", ex);
			}
			final HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());
			// Build a client with a credentials provider
			CloseableHttpClient httpClient = null;

			// Create request factory
			httpClient = HttpClientBuilder.create().build();
			HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
					httpHost);
			requestFactory.setHttpClient(httpClient);

			// Put the factory in the template
			restTemplate = new RestTemplate();
			restTemplate.setRequestFactory(requestFactory);
			logger.debug("In MicroserviceRestClientImpl(String webapiUrl) at end "+restTemplate.toString());
	}

	public URI buildUri(final String[] path, final Map<String, Object> queryParams, RestPageRequest pageRequest) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.baseUrl);
		for (int p = 0; p < path.length; ++p)
			builder.pathSegment(path[p]);
		if (queryParams != null && queryParams.size() > 0) {
			for (Map.Entry<String, ? extends Object> entry : queryParams.entrySet()) {
				Object value = null;
				// Server expect Date as Long.
				if (entry.getValue() instanceof Date)
					value = ((Date) entry.getValue()).getTime();
				else
					value = entry.getValue().toString();
				builder.queryParam(entry.getKey(), value);
			}
		}
//		if (pageRequest != null) {
//			if (pageRequest.getSize() != null)
//				builder.queryParam("page", Integer.toString(pageRequest.getPage()));
//			if (pageRequest.getPage() != null)
//				builder.queryParam("size", Integer.toString(pageRequest.getSize()));
//			if (pageRequest.getFieldToDirectionMap() != null && pageRequest.getFieldToDirectionMap().size() > 0) {
//				for (Map.Entry<String, String> entry : pageRequest.getFieldToDirectionMap().entrySet()) {
//					String value = entry.getKey() + (entry.getValue() == null ? "" : ("," + entry.getValue()));
//					builder.queryParam("sort", value);
//				}
//			}
//		}
		return builder.build().encode().toUri();
	}

	@Override
	public ResponseEntity<ServiceResponse> generateMicroservice(String solutioId, String revisionId, String provider,
			String authorization, String trackingID, String modName, Integer deployment_env, String request_id, boolean deploy) {
		logger.debug("In MicroserviceRestClientImpl: SolutionId " + solutioId + " and RevisionId " + revisionId);
		Map<String, Object> copy = new HashMap<>();
		copy.put("solutioId", solutioId);
		copy.put("revisionId", revisionId);

		if(modName != null && !modName.isEmpty()){
			copy.put("modName", modName);
		}
		if(deployment_env != null){
			copy.put("deployment_env", deployment_env);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authorization);
		headers.set("tracking_id", trackingID);
		headers.set("provider", provider);
		headers.set("Request-ID", request_id);
		headers.set("deploy", deploy+"");

		HttpEntity entity = new HttpEntity(headers);

		URI uri = buildUri(new String[] { "v2", "generateMicroservice" }, copy, null);
		logger.debug("Microservice: uri " + uri);
		logger.debug("Parameters for Microservice: SolutionId " + solutioId + " and RevisionId " + revisionId);
		ResponseEntity<ServiceResponse> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				new ParameterizedTypeReference<ServiceResponse>() {
				});
		logger.debug("Response code from microservice " + response.getStatusCodeValue());
		HttpStatus statusCode = response.getStatusCode();
		if (statusCode == HttpStatus.CREATED) {
			return new ResponseEntity<ServiceResponse>(
					ServiceResponse.successResponse(response.getBody().getModelName(), response.getBody().getTaskId(), response.getBody().getTrackingId(), response.getBody().getDockerImageUri()), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<ServiceResponse>(
					ServiceResponse.successResponse(response.getBody().getErrorMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
