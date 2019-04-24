package org.acumos.onboarding;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.acumos.cds.transport.RestPageRequest;
import org.acumos.onboarding.services.impl.MicroserviceRestClientImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MicroserviceRestClientImplTest {
	
	@Mock
	MicroserviceRestClientImpl microserviceRestClientImpl;
	
	@Test
	public void buildUriTest() {
	
	Map<String, Object> copy = new HashMap<>();
	copy.put("solutioId", "1111");
	copy.put("revisionId", "2222");
	
	URI uri1 = microserviceRestClientImpl.buildUri(new String[] { "v2", "generateMicroservice" }, copy, null);
	
	RestPageRequest pageRequest = new RestPageRequest();
	pageRequest.setSize(1);
	pageRequest.setPage(9);
	
	URI uri = microserviceRestClientImpl.buildUri(new String[] { "v2", "generateMicroservice" }, copy, pageRequest);
	}

}
