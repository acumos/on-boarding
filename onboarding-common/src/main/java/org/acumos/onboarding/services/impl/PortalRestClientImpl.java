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
import java.util.Map;

import org.acumos.cds.client.HttpComponentsClientHttpRequestFactoryBasicAuth;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.onboarding.common.utils.AbstractResponseObject;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.services.PortalRestClient;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class PortalRestClientImpl implements PortalRestClient {

	private static Logger log = LoggerFactory.getLogger(PortalRestClientImpl.class);
	LoggerDelegate logger = new LoggerDelegate(log);

	private final String baseUrl;
	private final RestTemplate restTemplate;

	/**
	 *
	 */
	public PortalRestClientImpl() {
		baseUrl = "";
		restTemplate = null;
	}

	public PortalRestClientImpl(String webapiUrl) {

		if (webapiUrl == null)
			throw new IllegalArgumentException("Null URL not permitted");

		URL url = null;
		try {
			url = new URL(webapiUrl);
			baseUrl = url.toExternalForm();
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
	}

	public PortalRestClientImpl(String webapiUrl, String user, String pass) {
		if (webapiUrl == null)
			throw new IllegalArgumentException("Null URL not permitted");

		URL url = null;
		try {
			url = new URL(webapiUrl);
			baseUrl = url.toExternalForm();
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Failed to parse URL", ex);
		}
		final HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());

		// Build a client with a credentials provider
		CloseableHttpClient httpClient = null;
		if (user != null && pass != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(user, pass));
			httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
		} else {
			httpClient = HttpClientBuilder.create().build();
		}
		// Create request factory
		HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
				httpHost);
		requestFactory.setHttpClient(httpClient);

		// Put the factory in the template
		restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(requestFactory);
	}

	private URI buildUri(final String[] path, final Map<String, Object> queryParams, RestPageRequest pageRequest) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.baseUrl);
		for (int p = 0; p < path.length; ++p)
			builder.pathSegment(path[p]);
//		if (queryParams != null && queryParams.size() > 0) {
//			for (Map.Entry<String, ? extends Object> entry : queryParams.entrySet()) {
//				Object value = null;
//				// Server expect Date as Long.
//				if (entry.getValue() instanceof Date)
//					value = ((Date) entry.getValue()).getTime();
//				else
//					value = entry.getValue().toString();
//				builder.queryParam(entry.getKey(), value);
//			}
//		}
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
	public String loginToAcumos(org.json.simple.JSONObject credentials) {
		URI uri = buildUri(new String[] { "auth", "jwtToken" }, null, null);
		logger.debug("jwtToken: uri : " + uri);
		logger.debug("Token URI : " + uri);
		AbstractResponseObject result = restTemplate.postForObject(uri, credentials, AbstractResponseObject.class);

		return result.getJwtToken();
	}

	@Override
	public JsonResponse<Object> tokenValidation(org.json.simple.JSONObject token, String provider) {

		URI uri = buildUri(new String[] { "auth", "validateToken" }, null, null);
		logger.debug("jwtToken: uri : " + uri);
		logger.debug("Validation URI : " + uri);

		HttpHeaders headers = new HttpHeaders();
		headers.set("provider", provider);
		HttpEntity<?> entity = new HttpEntity<Object>(token, headers);
		JsonResponse<Object> result = restTemplate.postForObject(uri, entity, JsonResponse.class);
		return result;
	}


}
