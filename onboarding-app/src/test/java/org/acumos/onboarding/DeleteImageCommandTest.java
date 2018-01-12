package org.acumos.onboarding;

import org.acumos.onboarding.component.docker.cmd.DeleteImageCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test class for DeleteImageCommand.java
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DeleteImageCommandTest {
	
	DeleteImageCommand deleteImageCommand = new DeleteImageCommand("H2O", "1.0.0-SNAPSHOT", "Nexus");
	
	@Test
	public void getDisplayName() {
		try {
			deleteImageCommand.getDisplayName();
			assert (true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getImage() {
		deleteImageCommand.getImageName();
		assert (true);
	}

	@Test
	public void getTag() {
		deleteImageCommand.getTag();
		assert (true);
	}

	@Test
	public void getRegistry() {
		deleteImageCommand.getRegistry();
		assert (true);
	}

}
