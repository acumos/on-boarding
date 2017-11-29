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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.acumos.onboarding.common.utils.UtilityFunction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UtilityFunctionTest {

	UtilityFunction utilityFunction = new UtilityFunction();
	
	//OSystemTest oSystemTest;
	
	 String filePath = FilePathTest.filePath();

	File srcFile = new File(filePath+"inFile.csv");
	File destFile = new File(filePath+"outFile.csv");
	File untar = new File(filePath+"sample.tar.gz");
	File zipfile = new File(filePath+"model.zip");
	String outputFolder = "onboarding-app/src/test/java/org/acumos/onboarding";

	/*@Test
	public void copyFile() throws AcumosServiceException {

		try {
			UtilityFunction.copyFile(srcFile, destFile);
			assert (true);
		} catch (AcumosServiceException e) {

			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to copy file" + srcFile.getName() + " cause:" + e.getMessage(), e);

		}

	}*/

	@Test
	public void copyFileTest() {

		try {
		
			InputStream in;
			in = new FileInputStream(srcFile);

			UtilityFunction.copyFile(in, destFile);
			assert (true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	@Test
	public void toBytesTest() {

		try {
			utilityFunction.toBytes(srcFile);
			assert (true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Test
	public void toBytes() {

		try {
			utilityFunction.toBytes(srcFile);
			assert (true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getFileName() {

		utilityFunction.getFileName(srcFile, outputFolder);
		assert (true);

	}

	@Test
	public void unTarFile() {

		try {
			utilityFunction.unTarFile(untar, untar);
			assert(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert (true);

	}

	@Test
	public void toMD5() {
		String data = "VM Predictor";
		try {
			utilityFunction.toMD5(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert (true);

	}

	/*@Test
	public void deleteDirectory() {
		try {
			utilityFunction.deleteDirectory(srcFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert (true);

	}*/

	@Test
	public void unzip() {
		try {
			utilityFunction.unzip(zipfile, outputFolder);
			assert (true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getGUID() {
		utilityFunction.getGUID();
		assert (true);

	}
}
