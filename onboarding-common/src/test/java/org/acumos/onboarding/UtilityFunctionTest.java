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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.acumos.onboarding.common.utils.UtilityFunction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UtilityFunctionTest {

	UtilityFunction utilityFunction = new UtilityFunction();

	String filePath = FilePathTest.filePath();

	File srcFile = new File(filePath + "inFile.csv");
	File destFile = new File(filePath + "outFile.csv");

	String outputFolder = "onboarding-app/src/test/java/org/acumos/onboarding";

	public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

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

	@Test
	public void unzip() {
		try {
			File fos = new File(filePath + "atest.zip");
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fos));

			String file1Name = filePath + "model.pkl";
			String file2Name = filePath + "model_pb2.py";
			String file3Name = filePath + "wrap.json";
			addToZipFile(file1Name, zos);
			addToZipFile(file2Name, zos);
			addToZipFile(file3Name, zos);

			utilityFunction.unzip(fos, outputFolder);
			zos.close();
			assert (true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getGUID() {
		utilityFunction.getGUID();

	}
}
