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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.utils.LogBean;
import org.acumos.onboarding.common.utils.LogThreadLocal;
import org.acumos.onboarding.common.utils.UtilityFunction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;

import org.junit.Assert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@RunWith(MockitoJUnitRunner.class)
public class UtilityFunctionTest {

	//private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UtilityFunctionTest.class);
	UtilityFunction utilityFunction = new UtilityFunction();

	@Mock
	LogThreadLocal localThread;

	@Mock
	Resource resource;

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
		} catch (Exception e) {
			Assert.fail("copyFileTest failed : " + e.getMessage());
		}
	}

	@Test
	public void toBytesTest() {

		try {
			UtilityFunction.toBytes(srcFile);
		} catch (IOException e) {
			Assert.fail("Exception occured while toBytesTest() : " + e.getMessage());
		}

	}

	@Test
	public void toBytes() {

		try {
			UtilityFunction.toBytes(srcFile);
		} catch (IOException e) {
			Assert.fail("Exception occured while toByTest() : " + e.getMessage());
		}

	}

	@Test
	public void getFileName() {

		Assert.assertNotNull(UtilityFunction.getFileName(srcFile, outputFolder));

	}

	@Test
	public void toMD5Test() {
		String data = "VM Predictor";
		try {
			UtilityFunction.toMD5(data);
		} catch (Exception e) {
			Assert.fail("Exception occured while toMD5Test() : " + e.getMessage());
		}
	}

	@Test
    public void unzipTest() {
        try {
            File fos = new File(filePath + "atest.zip");
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fos));

            String file1Name = filePath + "model.pkl";
            String file2Name = filePath + "model_pb2.py";
            String file3Name = filePath + "wrap.json";
            addToZipFile(file1Name, zos);
            addToZipFile(file2Name, zos);
            addToZipFile(file3Name, zos);

            UtilityFunction.unzip(fos, "\\");
            zos.close();
        } catch (Exception e) {
            Assert.fail("Exception occured while unzipTest() : " + e.getMessage());
        }
    }

	@Test
    public void zipFileTest() {
        try {
            File fos = new File(filePath + "atest.txt");
            File[] fileArray = new File[] {fos};
            UtilityFunction.zipFile(fileArray, fos);
            UtilityFunction.deleteDirectory(fos);
        } catch (Exception e) {
            Assert.fail("Exception occured while zipFileTest() : " + e.getMessage());
        }
    }
	
	@Test
	public void getGUID() {
		UtilityFunction.getGUID();
	}

	@Test
	public void deleteDirectoryTest(){
		File file=new File("onboarding-app/src/test/java/org/acumos/onboarding/testDir");
		file.mkdirs();
		File f1 = new File(file.getPath() + File.separator + "testFile");
		try {
			f1.createNewFile();
		} catch (IOException e) {
			Assert.fail("Exception occured while deleteDirectoryTest(): " + e.getMessage());
		}
		UtilityFunction.deleteDirectory(f1);
	}

	@Test
	public void copyFileTest1(){
		
		try {
			UtilityFunction.copyFile(srcFile, destFile);
		} catch (AcumosServiceException e) {
			Assert.fail("Exception occured while copyFileTest1(): " + e.getMessage());
		}
	}

	@Test
	public void copyFileTest2(){
		try {
			InputStream in;
			in = new FileInputStream(srcFile);
			Mockito.when(resource.getInputStream()).thenReturn(in);
			UtilityFunction.copyFile( resource, destFile);
		} catch (Exception e) {
			Assert.fail("Exception occured while copyFileTest2(): " + e.getMessage());
		}
	}

	@Test
	public void toBytesTest1(){
		try {
			InputStream in;
			in = new FileInputStream(srcFile);
			Mockito.when(resource.getInputStream()).thenReturn(in);
			UtilityFunction.toBytes( resource);
		} catch (Exception e) {
			Assert.fail("Exception occured while toBytesTest1(): " + e.getMessage());
		}
	}

	@Test
	public void logLocalThreadTest() {
		LogBean logbean = new LogBean();
		logbean.setFileName("TestFile");
		LogThreadLocal.set(logbean);
		assertNotNull(LogThreadLocal.get());
		LogThreadLocal.unset();
		assertNull(LogThreadLocal.get());
	}
	
	@Test
	public void setProjectVersionTest() {
		UtilityFunction.setProjectVersion("1.0");
	}
	
	@Test
	public void unTarFileTest() {
		try {
			String modelId = UtilityFunction.getGUID();
			File opFolder = new File(filePath + "tmpOutputFolder", modelId);
			opFolder.mkdirs();

			File files = new File(filePath + "tempModel");
			files.mkdir();

			File modelFile = new File(files, "mod.txt");
			modelFile.createNewFile();
			File tarFile = new File(opFolder, "tempFile.txt");
			tarFile.createNewFile();
			UtilityFunction.unTarFile(tarFile, opFolder);
			UtilityFunction.deleteDirectory(opFolder);
			UtilityFunction.deleteDirectory(files);
		} catch (Exception e) {
			Assert.fail("Exception occured while unTarFileTest() : " + e.getMessage());
		}
	}
	
	@Test
	public void getCurrentVersionTest() {
		String currentVersion = UtilityFunction.getCurrentVersion();
		assertNotNull(currentVersion);
	}
	
	@Test
	public void addLogsTest() {
		try {
			String msg = "Test add Logs";
			String logType = "DEBUG";
			LogBean logBean = new LogBean();
	
			logBean.setFileName("TestLogBeanFile");
			logBean.setLogPath(filePath);
			
			UtilityFunction.addLogs(msg, logType, logBean);
			File f = new File(filePath+"TestLogBeanFile");
			f.createNewFile();
			Files.delete(f.toPath());
		} catch (Exception e) {
			Assert.fail("Failed while testing addLogsTest - " + e.getMessage());
		}
	}
	
	@Test
	public void moveFileTest() {
		try {
			File srcFile1 = new File(filePath + "testMoveJsonFile.json");
			srcFile1.createNewFile();
			File srcFile2 = new File(filePath + "testMoveProtoFile.proto");
			srcFile2.createNewFile();
			File outputFolder = new File(filePath + "tempModel");
			outputFolder.mkdir();
			UtilityFunction.moveFile(srcFile1, outputFolder);
			UtilityFunction.moveFile(srcFile2, outputFolder);
			UtilityFunction.deleteDirectory(outputFolder);
		} catch (Exception e) {
			Assert.fail("Failed while testing moveFileTest - " + e.getMessage());
		}
	}

}
