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
import java.io.FileReader;
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
import org.acumos.onboarding.services.impl.CommonOnboarding;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

public class UtilityFunction {
	private static final Logger log = LoggerFactory.getLogger(UtilityFunction.class);
	static LoggerDelegate logger = new LoggerDelegate(log);

	private static String version = null;
    private static String projectVersion = null;

	public static String getProjectVersion() {
		return projectVersion;
	}

	public static void setProjectVersion(String projectVersion) {
		UtilityFunction.projectVersion = projectVersion;
	}

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
			logger.info("File Deleted Status = " + deleteFlag);
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
			logger.error(e.getMessage(), e);
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
				logger.error("Fail to download " + destFile.getName());
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
			logger.debug("Count is = " + count);
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
			logger.debug("Count is = " + count);
			return bytes;
		} finally {
			in.close();
		}
	}

	public static void createLogFile() {
		LogBean logBean = LogThreadLocal.get();
 		String fileName = logBean.getFileName();

		File file = new java.io.File(logBean.getLogPath());
		file.mkdirs();
		try {
			File f1 = new File(file.getPath() + File.separator + fileName);
			if (!f1.exists()) {
				f1.createNewFile();
			}
			logger.debug("Log file created successfully " + f1.getAbsolutePath());
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

	public static void addLogs(String msg, String logType, LogBean logBean) {
		try {
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
		logger.debug("Inside createDockerClientConfig");
		return DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host).withDockerTlsVerify(false).build();
	}

	public static DockerClient createDockerClient (String host) {
		logger.debug("Inside createDockerClient");
		DockerClientConfig dockerClientConfig = createDockerClientConfig(host);
		DockerClient dockerClient = DockerClientBuilder.getInstance(dockerClientConfig).build();
		logger.debug("createDockerClient ended");
		return dockerClient;

	}

	/**
	 * This method retrieves the current project version from pom.xml.
	 * @return
	 */
	public static String getCurrentVersion() {

		if (version == null) {
			logger.info("Retrieving project version :::");
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model;
			try {
				model = reader.read(new FileReader("pom.xml"));
				version = model.getVersion();
			} catch (Exception e) {
				logger.error("getCurrentVersion Failed Exception " + e.getMessage(), e);
			}
		}
		logger.debug("Onboarding version:::" + version);
	  return version;
	}

	public static void moveFile(File srcFile, File outputFolder) throws AcumosServiceException {

		try {
			String fileName = srcFile.getName();
			String fileExt = CommonOnboarding.getExtensionOfFile(srcFile.getName());

			if (fileExt.equalsIgnoreCase("json") && !(fileName.toLowerCase().contains("license"))) {
				logger.debug("moving file "+ srcFile.getName() +" from path :"+ srcFile.getAbsolutePath() + " to " + outputFolder.getAbsolutePath());
				srcFile.renameTo(new File(outputFolder.getAbsolutePath() + File.separator+"metadata.json"));
			} else if (fileExt.equalsIgnoreCase("proto") || fileExt.equalsIgnoreCase("zip")) {
				logger.debug("moving file "+ srcFile.getName() +" from path :"+ srcFile.getAbsolutePath() + " to " + outputFolder.getAbsolutePath());
				srcFile.renameTo(new File(outputFolder.getAbsolutePath() + File.separator+"model." + fileExt));
			}
		} catch (Exception e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to move file " + srcFile.getName() + " form folder cause: ", e);
		}
	}
	
	public static boolean isEmptyOrNullString(String input) {
		boolean isEmpty = false;
		if (null == input || 0 == input.trim().length()) {
			isEmpty = true;
		}
		return isEmpty;
	}
}
