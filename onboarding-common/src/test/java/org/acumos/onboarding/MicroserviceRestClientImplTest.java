package org.acumos.onboarding;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import java.io.IOException;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.services.impl.MicroserviceRestClientImpl;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class MicroserviceRestClientImplTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	public static Logger log = LoggerFactory.getLogger(PortalRestClientImplTest.class);
	LoggerDelegate logger = new LoggerDelegate(log);

	public static final String LOCAL_HOST = "http://localhost:8000/";

	@InjectMocks
	MicroserviceRestClientImpl microserviceRestClientImpl = new MicroserviceRestClientImpl(LOCAL_HOST);

	@Before
	public void setup(){
		new MicroserviceRestClientImpl();
	}

	@Test
	public void generateMicroserviceTest() {

        MLPSolution mlpSolution = new MLPSolution();
        ServiceResponse serviceResponse= ServiceResponse.successResponse(mlpSolution, 1111111, "trackingId");

 		ObjectMapper Obj = new ObjectMapper();
 		String jsonStr=null;
 		try {
 			jsonStr = Obj.writeValueAsString(serviceResponse);
 		}
 		catch (IOException e) {
 			logger.error("Exception occurred while parsing rest page response to string ",e.getMessage());
 		}

 		stubFor(post(urlEqualTo("/v2/generateMicroservice")).willReturn(
                aResponse().withStatus(HttpStatus.SC_ACCEPTED).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStr)));

// 	 	ResponseEntity<ServiceResponse> response = microserviceRestClientImpl.generateMicroservice("solutioId", "revisionId", "provider",
// 				"authorization", "trackingID", "modName", 1, "request_id");
 		//assertNotNull(response);

	}

}
