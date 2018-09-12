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

package org.acumos.onboarding.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.Resource;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

public class UtilityFunction {
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UtilityFunction.class);

	public static String getGUID() {
		return java.util.UUID.randomUUID().toString();
	}

	public static void zipFile(File[] inputFiles, File outZipFilePath) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outZipFilePath));
		try {
			for (int i = 0; i < inputFiles.length; i++) {
				FileInputStream in = new FileInputStream(inputFiles[i]);
				ZipEntry zipEntry = new ZipEntry(inputFiles[i].getName());
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(zipEntry));
				// Transfer bytes from the file to the ZIP file
				try {
					int len;
					byte[] buf = new byte[8 * 1024];
					while ((len = in.read(buf)) > 0)
						out.write(buf, 0, len);
					// Complete the entry
					out.closeEntry();
				} finally {
					in.close();
				}
			}
			// Complete the ZIP file
		} finally {
			out.close();
		}
	}

	public static void unzip(File zipFile, String outPath) throws IOException {
		ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));
		try {
			ZipEntry entry = null;
			File file = new File(outPath);
			if (!file.exists())
				file.mkdirs();
			while ((entry = in.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					File out = new File(outPath + File.separatorChar + entry.getName());
					out.getParentFile().mkdirs();
					FileOutputStream stream = new FileOutputStream(outPath + File.separatorChar + entry.getName());
					try {
						int len;
						byte[] buf = new byte[1024];
						while ((len = in.read(buf)) > 0)
							stream.write(buf, 0, len);
						buf = null;
					} finally {
						stream.close();
					}
				}
			else{
				Path path = Paths.get(outPath+File.separatorChar+entry.getName());
				Files.createDirectories(path);
			}
			}
		} finally {
			in.close();
		}
	}

	public static void deleteDirectory(File fileStreamPath) {
		if (fileStreamPath.exists()) {
			if (fileStreamPath.isDirectory()) {
				File[] files = fileStreamPath.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteDirectory(files[i]);
				}
			}
			boolean deleteFlag = fileStreamPath.delete();
			//info as log file is deleted, in debug we are calling addLog()
			logger.info("File Deleted Status = {}", deleteFlag);
		}
	}

	public static String toMD5(String data) {
		try {
			StringBuilder result = new StringBuilder("");
			byte[] bytes = MessageDigest.getInstance("MD5").digest(data.getBytes());
			for (int i = 0; i < bytes.length; i++) {
				int hex = bytes[i] & 255;
				if (hex < 16)
					result.append("0");
				result.append(Integer.toHexString(hex));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.error(EELFLoggerDelegate.errorLogger,e.getMessage(), e);
			return data;
		}
	}

	public static void copyFile(Resource srcFile, File destFile) throws AcumosServiceException {
		try {
			InputStream in = srcFile.getInputStream();
			copyFile(in, destFile);
		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to copy file "+ srcFile.getFilename()+ " form classpath cause: ", e);
		}
	}

	public static void copyFile(File srcFile, File destFile) throws AcumosServiceException {
		try {
			InputStream in = new FileInputStream(srcFile);
			copyFile(in, destFile);
		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to copy file " + srcFile.getName() + " cause: ", e);
		}
	}

	public static void copyFile(InputStream in, File destFile) throws IOException, AcumosServiceException {

		try {
			OutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[8 * 1024];
				int bytesRead = 0;
				while ((bytesRead = in.read(buffer)) > 0) {
					out.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Fail to download {}", destFile.getName());
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
						"Fail to download " + destFile.getName());
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}

	}

	public static byte[] toBytes(File fileUrl) throws IOException {
		FileInputStream in = new FileInputStream(fileUrl);
		try {
			byte[] bytes = new byte[in.available()];
			int count = 0;
			count = in.read(bytes);
			logger.debug(EELFLoggerDelegate.debugLogger,"Count is= {}", count);
			return bytes;
		} finally {
			in.close();
		}

	}

	public static void unTarFile(File tarFile, File destFile) throws IOException {
		FileInputStream fis = new FileInputStream(tarFile);
		TarArchiveInputStream tis = new TarArchiveInputStream(fis);
		TarArchiveEntry tarEntry;

		while ((tarEntry = tis.getNextTarEntry()) != null) {
			File outputFile = new File(destFile + File.separator + tarEntry.getName());

			if (tarEntry.isDirectory()) {
				if (!outputFile.exists()) {
					outputFile.mkdirs();
				}
			} else {
				outputFile.getParentFile().mkdirs();
				FileOutputStream fos = new FileOutputStream(outputFile);
				IOUtils.copy(tis, fos);
				fos.close();
			}
		}
		tis.close();
	}

	public static File deCompressGZipFile(File gZippedFile, File tarFile) throws IOException {
		FileInputStream fis = new FileInputStream(gZippedFile);
		GZIPInputStream gZIPInputStream = new GZIPInputStream(fis);

		FileOutputStream fos = new FileOutputStream(tarFile);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = gZIPInputStream.read(buffer)) > 0) {
			fos.write(buffer, 0, len);
		}

		fos.close();
		gZIPInputStream.close();
		return tarFile;

	}

	public static String getFileName(File inputFile, String outputFolder) {
		return outputFolder + File.separator + inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
	}

	public static byte[] toBytes(Resource resouce) throws IOException {
		InputStream in = resouce.getInputStream();

		try {
			byte[] bytes = new byte[in.available()];
			int count = 0;
			count = in.read(bytes);
			logger.debug(EELFLoggerDelegate.debugLogger,"Count is= {}", count);
			return bytes;
		} finally {
			in.close();
		}
	}
	
	public static void createLogFile() {
		LogBean logBean = LogThreadLocal.get();
		String fileName = logBean.getFileName();

		File file = new java.io.File(OnboardingConstants.lOG_DIR_LOC);
		file.mkdirs();
		try {
			File f1 = new File(file.getPath() + File.separator + fileName);
			if (!f1.exists()) {
				f1.createNewFile();
			}
			logger.debug(EELFLoggerDelegate.debugLogger,
					"Log file created successfully " + f1.getAbsolutePath());
		} catch (Exception e) {
			//info to avoid infinite loop.logger.debug call again calls addlog method
			logger.info("Failed while creating log file " + e.getMessage());
		}

	}
	
	public static void addLogs(String msg, String logType) {
		try {
			LogBean logBean = LogThreadLocal.get();
			if (logBean != null) {
				String fileName = logBean.getFileName();
				String logPath = logBean.getLogPath();
				File file = new java.io.File(logPath);
				if (file.isDirectory()) {
					FileWriter fout = new FileWriter(file.getPath() + File.separator + fileName, true);
					fout.write(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + "  " + logType + "  "
							+ msg + "\n");
					fout.close();
				}
			} 
		} catch (IOException e) {
			//info to avoid infinite loop.logger.debug call again calls addlog method
			logger.info("Exception occured while adding logs in log file" + e.getMessage());
		}
	} 
	
	public static DockerClientConfig createDockerClientConfig(String host) {
		logger.debug(EELFLoggerDelegate.debugLogger, "Inside createDockerClientConfig");
		return DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host).withDockerTlsVerify(false).build();
	}
	
	public static DockerClient createDockerClient (String host) {
		logger.debug(EELFLoggerDelegate.debugLogger, "Inside createDockerClient");
		DockerClientConfig dockerClientConfig = createDockerClientConfig(host);
		DockerClient dockerClient = DockerClientBuilder.getInstance(dockerClientConfig).build();
		logger.debug(EELFLoggerDelegate.debugLogger, "createDockerClient ended");
		return dockerClient;
		
	}
}
