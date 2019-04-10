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

package org.acumos.onboarding;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class MetadataParserTest {

	@Mock
	MetadataParser metadataParser;
	private static final Logger log = LoggerFactory.getLogger(MetadataParser.class);
	LoggerDelegate logger = new LoggerDelegate(log);

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
