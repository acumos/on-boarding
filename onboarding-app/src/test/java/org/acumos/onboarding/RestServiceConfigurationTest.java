package org.acumos.onboarding;

import org.acumos.onboarding.common.config.RestServiceConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RestServiceConfigurationTest {

	RestServiceConfiguration confg= new RestServiceConfiguration();
	
	
	@Test
	public void apiTest() {
		confg.api();
		assert(true);
	}
}
