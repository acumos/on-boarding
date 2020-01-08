package org.acumos.onboarding;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.services.impl.DownloadModelArtifacts;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import org.springframework.http.MediaType;
import org.apache.http.HttpStatus;



//@RunWith(PowerMockRunner.class)
@RunWith(MockitoJUnitRunner.class)
//@PrepareForTest({DownloadModelArtifacts.class,CommonDataServiceRestClientImpl.class})
public class DownloadModelArtifactsTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	@Mock
	Environment env;

	@InjectMocks
	DownloadModelArtifacts downloadModelArtfct;

	@Mock
	CommonDataServiceRestClientImpl cmnDataService;

	@Mock
	OnboardingNotification onboardingNotification;

	@Mock
	NexusArtifactClient artifactClient;

	private final String url = "http://localhost:8000/ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";

	@Test
	public void getModelArtifactsTest() {
         System.out.println("Executing get model artifacts");
		try {
			//CommonDataServiceRestClientImpl cmdDataSvc = mock(CommonDataServiceRestClientImpl.class);

			//OnboardingNotification onboardingStatus = mock(OnboardingNotification.class);

			//NexusArtifactClient artifactClient = mock(NexusArtifactClient.class);

//			PowerMockito.whenNew(OnboardingNotification.class)
//			.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
//			.thenReturn(onboardingStatus);
//
//			PowerMockito.whenNew(CommonDataServiceRestClientImpl.class)
//			.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
//			.thenReturn(cmdDataSvc);

			setCdsProperty();

			MLPArtifact mLPArtifact  = new MLPArtifact();
			mLPArtifact.setArtifactTypeCode("MI");
			mLPArtifact.setUri("org/acumos/hello-world/2/hello-world-2.json");
			List<MLPArtifact> mlpArtifactList = new ArrayList();
			mlpArtifactList.add(mLPArtifact);

			ObjectMapper Obj = new ObjectMapper();
			String jsonStr = null;
			try {
				jsonStr = Obj.writeValueAsString(mlpArtifactList);
			} catch (IOException e) {
				//logger.error("Exception occurred while parsing rest page response to string ", e.getMessage());
			}
			stubFor(get(urlEqualTo("/ccds/revision/revisionId/artifact")).willReturn(
	                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
	                .withBody("[{\"artifactId\": \"111111\"," + "\"userId\": \"1234-1234-1234-1234-1234\","
	                		+ "\"artifactTypeCode\": \"MD\"," + "\"description\": null,"
	                		+ "\"version\": \"1.0.0\"," + "\"uri\": \"http://localhost\"," +  "\"created\": \"2019-04-05T20:47:03Z\"," + "\"modified\": \"2019-04-05T20:47:03Z\"}]")));
//			List<MLPArtifact> mlpArtifactList = new ArrayList();
//			mlpArtifactList.add(mLPArtifact);

			// Remember-  to mock method on mock object it should on mock() method not on object created by @Mock  . e.g.  below won't work
			// on cmnDataService created @Mock where it should work on cmdDataSvc created by mock(....)
			//Mockito.when(cmnDataService.getSolutionRevisionArtifacts(Mockito.anyString(), Mockito.anyString())).thenReturn(mlpArtifactList);

			//PowerMockito.whenNew(NexusArtifactClient.class).withArguments(Mockito.anyObject()).thenReturn(artifactClient);

			byte[] buffer = new byte[4000];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
			byteArrayOutputStream.write(buffer);

			String list = downloadModelArtfct.getModelArtifacts("solutionId", "revisionId", "userName", "password", env.getProperty("nexus.nexusEndPointURL"), env.getProperty("nexus.nexusUserName"), env.getProperty("nexus.nexusPassword"), "org/acumos/hello-world/2/hello-world-2.json");
			//assertTrue(list.size()>0);
			assertNotNull(list);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//assert(false);
			e.printStackTrace();
		}

	}

	@Test
	public void getModelProtoArtifactsTest() {
         System.out.println("Executing get model artifacts");
		try {

			setCdsProperty();

			MLPArtifact mLPArtifact  = new MLPArtifact();
			mLPArtifact.setArtifactTypeCode("MI");
			mLPArtifact.setUri("org/acumos/hello-world/2/hello-world-2.json");
			List<MLPArtifact> mlpArtifactList = new ArrayList();
			mlpArtifactList.add(mLPArtifact);

			ObjectMapper Obj = new ObjectMapper();
			String jsonStr = null;
			try {
				jsonStr = Obj.writeValueAsString(mlpArtifactList);
			} catch (IOException e) {
				//logger.error("Exception occurred while parsing rest page response to string ", e.getMessage());
			}
			stubFor(get(urlEqualTo("/ccds/revision/revisionId/artifact")).willReturn(
	                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
	                .withBody("[{\"artifactId\": \"111111\"," + "\"userId\": \"1234-1234-1234-1234-1234\","
	                		+ "\"artifactTypeCode\": \"MD\"," + "\"description\": null,"
	                		+ "\"version\": \"1.0.0\"," + "\"uri\": \"http://localhost\"," +  "\"created\": \"2019-04-05T20:47:03Z\"," + "\"modified\": \"2019-04-05T20:47:03Z\"}]")));

			byte[] buffer = new byte[4000];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
			byteArrayOutputStream.write(buffer);

			List<String> list = downloadModelArtfct.getModelProtoArtifacts("solutionId", "revisionId", "userName", "password", env.getProperty("nexus.nexusEndPointURL"), env.getProperty("nexus.nexusUserName"), env.getProperty("nexus.nexusPassword"), "org/acumos/hello-world/2/hello-world-2.json");
			//assertTrue(list.size()>0);
			assertNotNull(list);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//assert(false);
			e.printStackTrace();
		}

	}
	private void setCdsProperty() {
		when(env.getProperty("cmndatasvc.cmnDataSvcEndPoinURL")).thenReturn(url);
		when(env.getProperty("cmndatasvc.cmnDataSvcUser")).thenReturn(user);
		when(env.getProperty("cmndatasvc.cmnDataSvcPwd")).thenReturn(pass);
	}
}