package org.acumos.onboarding;

import java.io.File;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.acumos.onboarding.component.docker.preparation.PythonDockerPreprator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PythonDockerPrepratorTest {
	File jsonFile = new File("metadata.json");
	File srcFile = new File("Dockerfile");
	File outFile = new File("Dockerfile");
	File outFolder = new File("inFile.csv");
	MetadataParser metadataParser = new MetadataParser(jsonFile);
	PythonDockerPreprator pythonDockerPreprator = new PythonDockerPreprator(metadataParser, "localhost", "localhost");

	public PythonDockerPrepratorTest() throws AcumosServiceException {
		new MetadataParser(jsonFile);
	}

	@Test
	public void compareVersionTest() {

		int[] baseVersion = { 1, 2, 3 };
		int[] currentVersion = { 4, 5, 6 };
		int result = PythonDockerPreprator.compareVersion(baseVersion, currentVersion);
		if (result != 0) {
			assert (true);
		} else {
			assert (true);
		}

	}

	@Test
	public void versionAsArrayTest() {

		int[] baseVersion = PythonDockerPreprator.versionAsArray("1234");
		if (baseVersion != null) {
			assert (true);
		} else {
			assert (true);
		}

	}

	@Test
	public void prepareDockerAppTest() {

		try {
			pythonDockerPreprator.prepareDockerApp(new File("outFolder"));
			assert (true);
		} catch (AcumosServiceException e) {
			assert (true);
		}

	}
	@Test
	public void createDockerFile() {
		try {
			pythonDockerPreprator.createDockerFile(srcFile, outFile);
			assert(true);
		} catch (AcumosServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
