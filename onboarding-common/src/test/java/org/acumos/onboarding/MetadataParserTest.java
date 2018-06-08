package org.acumos.onboarding;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetadataParserTest {

	@Mock
	MetadataParser metadataParser;
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(MetadataParser.class);

	String filePath = FilePathTest.filePath();
	File validJsonFile = new File(filePath + "metadata.json");
	File invalidJsonFile = new File(filePath + "meta.json");

	@Test
	public void metadataParserValidJsonTest() throws AcumosServiceException {

		MetadataParser mparser = new MetadataParser(validJsonFile);
		assertNotNull(mparser);

	}

	/**
	 * It will return error message "Invalid input JSON" in the case of backward slash("/") 
	 * and "invalid model name" in the case of backward slash provided after adding \ it should not become escape sequence character. 
	 * e.g. if you will put \ before t it will become \t tab (spaces) and on boarding remove spaces.
	 * @throws AcumosServiceException
	 */

	@Test
	public void metadataParserInvalidJsonTest() throws AcumosServiceException {

		try {
			MetadataParser mparser = new MetadataParser(invalidJsonFile);
			assertNotNull(mparser);
		} catch (AcumosServiceException ae) {
			if ((ae.getMessage().equalsIgnoreCase("Invalid input JSON")) || (ae.getMessage().contains("Invalid Model Name"))) {
				logger.info("Exception while metadataParserInvalidJsonTest()" + ae.getMessage());
			} else {
				throw ae;
			}
		}
	}

}
