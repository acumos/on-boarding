package org.acumos.onboarding;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.services.impl.DownloadModelArtifacts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;



@RunWith(PowerMockRunner.class)
@PrepareForTest({DownloadModelArtifacts.class,CommonDataServiceRestClientImpl.class})
public class DownloadModelArtifactsTest {
	
	@InjectMocks
	DownloadModelArtifacts downloadModelArtfct;
	

	@Mock
	CommonDataServiceRestClientImpl cmnDataService;
	
	@Mock
	OnboardingNotification onboardingNotification;
	
	@Mock
	NexusArtifactClient artifactClient;
	
	@Test
	public void getModelArtifactsTest() {
         System.out.println("Executing get model artifacts");		
		try {
			CommonDataServiceRestClientImpl cmdDataSvc = mock(CommonDataServiceRestClientImpl.class);
			
			OnboardingNotification onboardingStatus = mock(OnboardingNotification.class);
			
			NexusArtifactClient artifactClient = mock(NexusArtifactClient.class);
			
			PowerMockito.whenNew(OnboardingNotification.class)
			.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
			.thenReturn(onboardingStatus);
			
			
			PowerMockito.whenNew(CommonDataServiceRestClientImpl.class)
			.withArguments(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())
			.thenReturn(cmdDataSvc);
			
			MLPArtifact mLPArtifact  = new MLPArtifact();
			mLPArtifact.setArtifactTypeCode("MI");
			mLPArtifact.setUri("org/acumos/hello-world/2/hello-world-2.json");
			
			List<MLPArtifact> mlpArtifactList = new ArrayList();
			mlpArtifactList.add(mLPArtifact);		
			
			// Remember-  to mock method on mock object it should on mock() method not on object created by @Mock  . e.g.  below won't work 
			// on cmnDataService created @Mock where it should work on cmdDataSvc created by mock(....)
			PowerMockito.when(cmdDataSvc.getSolutionRevisionArtifacts(Mockito.anyString(), Mockito.anyString())).thenReturn(mlpArtifactList);
			
			PowerMockito.whenNew(NexusArtifactClient.class).withArguments(Mockito.anyObject()).thenReturn(artifactClient);
			
			byte[] buffer = new byte[4000];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
			byteArrayOutputStream.write(buffer);
			PowerMockito.when(artifactClient.getArtifact(Mockito.anyString())).thenReturn(byteArrayOutputStream);
			
			String list = downloadModelArtfct.getModelArtifacts("solutionId", "revisionId", "userName", "password", "nexusUrl", "nexusUserName", "nexusPassword", "org/acumos/hello-world/2/hello-world-2.json");
			//assertTrue(list.size()>0);
			assertNotNull(list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			assert(false);
			e.printStackTrace();
		}
		
	}
	

}