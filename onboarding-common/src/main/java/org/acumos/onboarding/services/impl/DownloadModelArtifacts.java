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
package org.acumos.onboarding.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadModelArtifacts {

	private static final Logger logger = LoggerFactory.getLogger(DownloadModelArtifacts.class);

	String artifactFileName;
	
	CommonDataServiceRestClientImpl cmnDataService;
	
	File artifactFile = null;
	
	public String getModelArtifacts(String solutionId, String revisionId, String userName, String password,
			String nexusUrl, String nexusUserName, String nexusPassword, String dataSource) throws Exception {
		logger.debug("------ Start getBluePrintNexus-----------------");
		logger.debug("-------solutionId-----------" + solutionId);
		logger.debug("-------revisionId-----------" + revisionId);

		List<MLPArtifact> mlpArtifactList;
		String nexusURI = "";

		ByteArrayOutputStream byteArrayOutputStream = null;
		this.cmnDataService = new CommonDataServiceRestClientImpl(dataSource, userName, password, null);

		File outputFolder = new File("dcae_model");
		outputFolder.mkdirs();

		if (revisionId != null) {
			/* Get the list of Artifacts for the SolutionId and revisionId. */
			mlpArtifactList = cmnDataService.getSolutionRevisionArtifacts(solutionId, revisionId);
			if (mlpArtifactList != null && !mlpArtifactList.isEmpty()) {

				for (int i = 0; i < mlpArtifactList.size(); i++) {

					if (mlpArtifactList.get(i).getArtifactTypeCode().equals("MI")
							|| mlpArtifactList.get(i).getArtifactTypeCode().equals("MD")) {

						nexusURI = mlpArtifactList.get(i).getUri();

						logger.debug("------ Nexus URI : " + nexusURI + " -------");

							if (nexusURI != null) {
								RepositoryLocation repositoryLocation = new RepositoryLocation();
								repositoryLocation.setId("1");
								repositoryLocation.setUrl(nexusUrl);
								repositoryLocation.setUsername(nexusUserName);
								repositoryLocation.setPassword(nexusPassword);
								NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);

								byteArrayOutputStream = artifactClient.getArtifact(nexusURI);
								if (!nexusURI.isEmpty()) {
									artifactFileName = nexusURI.substring(nexusURI.lastIndexOf("/") + 1,
											nexusURI.length());
								}
							}
							if (byteArrayOutputStream != null) {
								byteArrayOutputStream.close();
							}
							File file = new File(outputFolder, artifactFileName);
							FileOutputStream fout = new FileOutputStream(file);
							fout.write(byteArrayOutputStream.toByteArray());
							fout.flush();
							fout.close();
					}
				}
			}
		}
		return artifactFileName;
	}
	
	public String getModelProtoArtifacts(String solutionId, String revisionId, String userName, String password,
			String nexusUrl, String nexusUserName, String nexusPassword, String dataSource) throws Exception {
		logger.debug("------ Start getBluePrintNexus-----------------");
		logger.debug("-------solutionId-----------" + solutionId);
		logger.debug("-------revisionId-----------" + revisionId);

		List<MLPArtifact> mlpArtifactList;
		String nexusURI = "";

		ByteArrayOutputStream byteArrayOutputStream = null;
		this.cmnDataService = new CommonDataServiceRestClientImpl(dataSource, userName, password, null);

		File outputFolder = new File("dcae_model_proto");
		outputFolder.mkdirs();

		if (revisionId != null) {
			/* Get the list of Artifacts for the SolutionId and revisionId. */
			mlpArtifactList = cmnDataService.getSolutionRevisionArtifacts(solutionId, revisionId);
			if (mlpArtifactList != null && !mlpArtifactList.isEmpty()) {

				for (int i = 0; i < mlpArtifactList.size(); i++) {

					if (mlpArtifactList.get(i).getArtifactTypeCode().equals("MI")
							|| mlpArtifactList.get(i).getArtifactTypeCode().equals("MD")) {

						nexusURI = mlpArtifactList.get(i).getUri();

						logger.debug("------ Nexus URI : " + nexusURI + " -------");

						if (nexusURI != null) {
							RepositoryLocation repositoryLocation = new RepositoryLocation();
							repositoryLocation.setId("1");
							repositoryLocation.setUrl(nexusURI);
							repositoryLocation.setUsername(nexusUserName);
							repositoryLocation.setPassword(nexusPassword);
							NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
							logger.debug("Putting Artifact in ByteArrayOutputStream !!!");
							byteArrayOutputStream = artifactClient.getArtifact(nexusURI);
							logger.debug("Protobuf ByteArrayOutputStream = "+byteArrayOutputStream);
							if (!nexusURI.isEmpty()) {
								artifactFileName = nexusURI.substring(nexusURI.lastIndexOf("/") + 1, nexusURI.length());
								logger.debug("Proto Artifact File Name = " + artifactFileName);
							}
						}
						if (byteArrayOutputStream != null) {
							byteArrayOutputStream.close();
						}
						File file = null;
						if (artifactFileName.substring(artifactFileName.lastIndexOf(".") + 1).equals("proto")) {
							file = new File(outputFolder, artifactFileName);
						}else {
							logger.debug("Artifact File Name is not proto = "+artifactFileName);
							continue;
						}
						logger.debug("Artifact File Name before output stream = "+artifactFileName);
						FileOutputStream fout = new FileOutputStream(file);
						fout.write(byteArrayOutputStream.toByteArray());
						this.setArtifactFile(file);
						fout.flush();
						fout.close();
					}
				}
			}
		}
		logger.debug("Artifact Proto File Name --> " + artifactFileName);
		return artifactFileName;
	}


	public void setArtifactFile(File file) {
		this.artifactFile = file;
	}
	
	public File getArtifactFile() {
		return this.artifactFile;
	}
	
}