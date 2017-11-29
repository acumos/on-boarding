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

package org.acumos.onboarding.component.docker.preparation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.utils.UtilityFunction;

/**
 * 
 * @author *****
 *
 */
public class RDockerPreparator {
	private Metadata metadata;

	private String rVersion;

	private String rhttpProxy;

	/**
	 * 
	 * @param metadataParser
	 * @param httpProxy
	 * @throws AcumosServiceException
	 */
	public RDockerPreparator(MetadataParser metadataParser, String httpProxy) throws AcumosServiceException {
		this.rhttpProxy = httpProxy;
		this.metadata = metadataParser.getMetadata();
		int[] runtimeVersion = versionAsArray(metadata.getRuntimeVersion());
		if (runtimeVersion[0] == 3) {
			int[] baseVersion = new int[] { 3, 3, 2 };
			if (compareVersion(baseVersion, runtimeVersion) >= 0) {
				this.rVersion = "3.3.2";
			} else {
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
						"Unspported r version " + metadata.getRuntimeVersion());
			}
		} else {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
					"Unspported r version " + metadata.getRuntimeVersion());
		}
	}

	/**
	 * 
	 * @param outputFolder
	 * @throws AcumosServiceException
	 */
	public void prepareDockerApp(File outputFolder) throws AcumosServiceException {
		this.createDockerFile(new File(outputFolder, "Dockerfile"), new File(outputFolder, "Dockerfile"));
		this.createPackageR(new File(outputFolder, "packages.R"), new File(outputFolder, "packages.R"));
	}

	private void createPackageR(File inPackageRFile, File outPackageRFile) throws AcumosServiceException {
		try {
			List<Requirement> requirements = this.metadata.getRequirements();
			StringBuilder reqBuilder = new StringBuilder();
			for (Requirement requirement : requirements) {
				reqBuilder.append("\"" + requirement.name + "\",");
			}
			String reqAsString = reqBuilder.toString();
			reqAsString = reqAsString.substring(0, reqAsString.length() - 1);
			String packageRFileAsString = new String(UtilityFunction.toBytes(inPackageRFile));
			packageRFileAsString = MessageFormat.format(packageRFileAsString, new Object[] { reqAsString });
			FileWriter writer = new FileWriter(outPackageRFile);
			try {
				writer.write(packageRFileAsString.trim());
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to create dockerFile for input model", e);
		}
	}

	private void createDockerFile(File inDockerFile, File outDockerFile) throws AcumosServiceException {
		try {
			String dockerFileAsString = new String(UtilityFunction.toBytes(inDockerFile));
			dockerFileAsString = MessageFormat.format(dockerFileAsString,
					new Object[] { this.rhttpProxy, this.rVersion });
			FileWriter writer = new FileWriter(outDockerFile);
			try {
				writer.write(dockerFileAsString.trim());
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to create dockerFile for input model", e);
		}
	}

	/**
	 * 
	 * @param baseVersion
	 * @param currentVersion
	 * @return
	 */
	public static int compareVersion(int[] baseVersion, int[] currentVersion) {
		int result = 0;
		for (int i = 0; i < baseVersion.length; i++) {
			if (currentVersion.length < i + 1 || baseVersion[i] > currentVersion[i]) {
				result = 1;
				break;
			} else if (baseVersion[i] < currentVersion[i]) {
				result = -1;
				break;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param version
	 * @return
	 */
	public static int[] versionAsArray(String version) {
		String[] versionArr = version.split("\\.");
		int[] versionIntArr = new int[versionArr.length];
		for (int i = 0; i < versionArr.length; i++) {
			versionIntArr[i] = Integer.parseInt(versionArr[i]);
		}
		return versionIntArr;
	}

}
