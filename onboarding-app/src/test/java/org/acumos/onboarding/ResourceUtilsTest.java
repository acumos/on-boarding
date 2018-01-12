package org.acumos.onboarding;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.utils.ResourceUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ResourceLoader;

@RunWith(MockitoJUnitRunner.class)
public class ResourceUtilsTest {

	
	private ResourceLoader resourceLoader;
	ResourceUtils resourceUtils = new ResourceUtils(resourceLoader);

	String path = "samplePath";

	@Test
	public void getResourceTest() {

		try {
			resourceUtils.isResourceExists(path);
			assert (true);

		} catch (AcumosServiceException ae) {
			assert (false);
		}
	}

	@Test
	public void loadResourcesTest() {
		try {
			resourceUtils.loadResources(path);
			assert (true);
		} catch (AcumosServiceException ae) {
			assert (false);
		}
	}
	
	@Test
	public void isResourceExistsTest() {
		try {
			resourceUtils.isResourceExists(path);
			assert (true);
		} catch (AcumosServiceException ae) {
			assert (false);
		}
	}

}
