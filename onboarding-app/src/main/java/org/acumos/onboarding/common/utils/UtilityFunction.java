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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.acumos.onboarding.Application;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.Resource;

/**
 * 
 * @author ******
 *
 */
public class UtilityFunction {
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(Application.class);
	

	public static String getGUID() {
		return java.util.UUID.randomUUID().toString();
	}

	/**
	 * 
	 * @param inputFiles
	 * @param outZipFilePath
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @param zipFile
	 * @param outPath
	 * @throws IOException
	 */
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
			}
		} finally {
			in.close();
		}
	}

	/**
	 * 
	 * @param fileStreamPath
	 */
	public static void deleteDirectory(File fileStreamPath) {
		if (fileStreamPath.exists()) {
			if (fileStreamPath.isDirectory()) {
				File[] files = fileStreamPath.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteDirectory(files[i]);
				}
			}
			boolean deleteFlag = fileStreamPath.delete();
			//logger.info("File Deleted Status = " + deleteFlag);
		}
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static String toMD5(String data) {
		try {
			//StringBuffer result = new StringBuffer("");
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

	/**
	 * 
	 * @param srcFile
	 * @param destFile
	 * @throws AcumosServiceException
	 */
	public static void copyFile(Resource srcFile, File destFile) throws AcumosServiceException {
		try {
			InputStream in = srcFile.getInputStream();
			copyFile(in, destFile);
		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to copy file" + srcFile.getFilename() + " form classpath cause:" + e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param srcFile
	 * @param destFile
	 * @throws AcumosServiceException
	 */
	public static void copyFile(File srcFile, File destFile) throws AcumosServiceException {
		try {
			InputStream in = new FileInputStream(srcFile);
			copyFile(in, destFile);
		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to copy file" + srcFile.getName() + " cause:" + e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param in
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFile(InputStream in, File destFile) throws IOException {

		try {
			OutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[8 * 1024];
				int bytesRead = 0;
				while ((bytesRead = in.read(buffer)) > 0) {
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}

	}

	/**
	 * 
	 * @param fileUrl
	 * @return
	 * @throws IOException
	 */
	public static byte[] toBytes(File fileUrl) throws IOException {
		FileInputStream in = new FileInputStream(fileUrl);
		try {
			byte[] bytes = new byte[in.available()];
			int count = 0;
			count = in.read(bytes);
			logger.debug("Count is= " + count);
			return bytes;
		} finally {
			in.close();
		}

	}

	/**
	 * 
	 * @param tarFile
	 * @param destFile
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @param gZippedFile
	 * @param tarFile
	 * @return
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @param inputFile
	 * @param outputFolder
	 * @return
	 */
	public static String getFileName(File inputFile, String outputFolder) {
		return outputFolder + File.separator + inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
	}

	/**
	 * 
	 * @param resouce
	 * @return
	 * @throws IOException
	 */
	public static byte[] toBytes(Resource resouce) throws IOException {
		InputStream in = resouce.getInputStream();

		try {
			byte[] bytes = new byte[in.available()];
			int count = 0;
			count = in.read(bytes);
			logger.debug("Count is= " + count);
				return bytes;
		} finally {
			in.close();
		}
	}
}
