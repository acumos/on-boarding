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

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.ToolkitTypeCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.designstudio.toscagenerator.ToscaGeneratorClient;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.nexus.client.data.UploadArtifactInfo;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.common.utils.OnboardingConstants;
import org.acumos.onboarding.common.utils.ResourceUtils;
import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.acumos.onboarding.logging.OnboardingLogConstants;
import org.json.simple.JSONObject;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.HttpStatusCodeException;

public class CommonOnboarding {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(CommonOnboarding.class);

	@Value("${nexus.nexusEndPointURL}")
	protected String nexusEndPointURL;

	@Value("${nexus.nexusUserName}")
	protected String nexusUserName;

	@Value("${nexus.nexusPassword}")
	protected String nexusPassword;

	@Value("${nexus.nexusGroupId}")
	protected String nexusGroupId;

	@Value("${cmndatasvc.cmnDataSvcEndPoinURL}")
	protected String cmnDataSvcEndPoinURL;

	@Value("${cmndatasvc.cmnDataSvcUser}")
	protected String cmnDataSvcUser;

	@Value("${cmndatasvc.cmnDataSvcPwd}")
	protected String cmnDataSvcPwd;

	@Value("${tosca.OutputFolder}")
	protected String toscaOutputFolder;

	@Value("${tosca.GeneratorEndPointURL}")
	protected String toscaGeneratorEndPointURL;

	@Value("${http_proxy}")
	protected String http_proxy;

	@Value("${requirements.extraIndexURL}")
	protected String extraIndexURL;

	@Value("${requirements.trustedHost}")
	protected String trustedHost;

	@Value("${mktPlace.mktPlaceEndPointURL}")
	protected String portalURL;

	@Value("${microService.microServiceEndPointURL}")
	protected String microServiceURL;

	@Value("${app.version}")
	protected String appVersion;

	protected String modelOriginalName = null;

	protected boolean dcaeflag = false;

	@Autowired
	protected ResourceLoader resourceLoader;

	protected MetadataParser metadataParser = null;

	protected CommonDataServiceRestClientImpl cdmsClient;

	protected PortalRestClientImpl portalClient;

	protected MicroserviceRestClientImpl microserviceClient;

	protected ResourceUtils resourceUtils;

	@PostConstruct
	public void init() {
		logger.debug(EELFLoggerDelegate.debugLogger,"Creating docker service instance");
		this.cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd,null);
		this.portalClient = new PortalRestClientImpl(portalURL);
		this.resourceUtils = new ResourceUtils(resourceLoader);
		this.microserviceClient = new MicroserviceRestClientImpl(microServiceURL);
	}

	/*
	 * @Method Name : validate Accepts JWT token in the form of String object.
	 * Validates it and returns validity status and ownerId.
	 */
	@SuppressWarnings("unchecked")
	public String validate(String authorization, String provider) throws AcumosServiceException {

		Boolean tokenVal = false;
		JsonResponse<Object> valid = null;
		String ownerID = null, loginName = null, token = null;

		String[] values = splitAuthorization(authorization); 

		if(values[0].toString().equalsIgnoreCase(authorization)){
			token = values[0];
		} else {
			loginName = values[0];
			token = values[1];
		}

		if (loginName != null && !loginName.isEmpty()) {

			MDC.put(OnboardingLogConstants.MDCs.USER,loginName);
			logger.debug(EELFLoggerDelegate.debugLogger,"Api Token validation started");
			MLPUser mUser = cdmsClient.loginApiUser(loginName, token);
			tokenVal = mUser.isActive();
			ownerID = mUser.getUserId();
			logger.debug(EELFLoggerDelegate.debugLogger,"OwnerID: " + ownerID);
		}

		if (tokenVal == false) {

			logger.debug(EELFLoggerDelegate.debugLogger,"JWT Token validation started");
			JSONObject obj1 = new JSONObject();
			obj1.put("jwtToken", token);

			JSONObject obj2 = new JSONObject();
			obj2.put("request_body", obj1);

			valid = portalClient.tokenValidation(obj2, provider);
			ownerID = valid.getResponseBody().toString();
			logger.debug(EELFLoggerDelegate.debugLogger,"OwnerID: " + ownerID);
		}

		return ownerID;
	}

	private String[] splitAuthorization(String authorization) {

		String[] values = authorization.split(":");
		return values;
	}

	/*
	 * @Method Name : getExistingSolution Gives existing solution against
	 * ownerId and Model name if any. *
	 */
	public List<MLPSolution> getExistingSolution(Metadata metadata) {

		String ownerId = metadata.getOwnerId();
		String modelName = metadata.getModelName();

		Map<String, Object> queryParameters = new HashMap<String, Object>();

		queryParameters.put("userId", ownerId);
		queryParameters.put("name", modelName);
		queryParameters.put("active", true);
		logger.debug(EELFLoggerDelegate.debugLogger,"Search Solution with criteria ownerId = " +ownerId + ", ModelName = " +modelName+ ", Active = true");

		/* TRUE - OR , FALSE - AND */
		RestPageResponse<MLPSolution> pageResponse = cdmsClient.searchSolutions(queryParameters, false,
				new RestPageRequest(0, 9));
		return pageResponse.getContent();

	}

	public MLPSolution createSolution(Metadata metadata, OnboardingNotification onboardingStatus) throws AcumosServiceException {
		logger.debug(EELFLoggerDelegate.debugLogger,"Create solution call started");
		MLPSolution solution = new MLPSolution();
		solution.setName(metadata.getSolutionName());
		solution.setDescription(metadata.getSolutionName());
		solution.setUserId(metadata.getOwnerId());

		logger.debug(EELFLoggerDelegate.debugLogger,"Model name[CreateSolutionMethod] :"+metadata.getSolutionName());

		String toolKit = metadata.getToolkit();

		if (toolKit != null) {
			solution.setToolkitTypeCode(getToolTypeCode(toolKit));
		} else if (dcaeflag) {
			solution.setToolkitTypeCode("ON");
		}

		solution.setActive(true);
		try {
			solution = cdmsClient.createSolution(solution);
			metadata.setSolutionId(solution.getSolutionId());
			logger.debug(EELFLoggerDelegate.debugLogger,"Solution created: " + solution.getSolutionId());
			// Creat solution id - success

			return solution;

		} catch (HttpStatusCodeException e) {
                        // Creat solution id - fail
			// Create Solution failed. Notify
			if (onboardingStatus != null) {
				// notify
				onboardingStatus.notifyOnboardingStatus("CreateMicroservice", "FA", e.getMessage());
			}
			logger.error(EELFLoggerDelegate.errorLogger, "Creation of solution failed - {}", e.getResponseBodyAsString(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Creation of solution failed - " + e.getResponseBodyAsString(), e);
		}
	}

	public String getToolTypeCode(String toolkit) {
		ToolkitTypeCode code = null;

		if (toolkit.equals("Scikit-Learn".toLowerCase())) {
			code = ToolkitTypeCode.SK;
		} else if (toolkit.equals("TensorFlow".toLowerCase())) {
			code = ToolkitTypeCode.TF;
		} else if (toolkit.equals("R".toLowerCase())) {
			code = ToolkitTypeCode.RC;
		} else if (toolkit.equals("H2O".toLowerCase())) {
			code = ToolkitTypeCode.H2;
		} else if (toolkit.equals("Design Studio".toLowerCase())) {
			code = ToolkitTypeCode.DS;
		} else if (toolkit.equals("Composite Solution".toLowerCase())) {
			code = ToolkitTypeCode.CP;
		}

		if (code != null)
			return code.name();
		else
			return null;
	}

	public MLPSolutionRevision createSolutionRevision(Metadata metadata) throws AcumosServiceException {
		logger.debug(EELFLoggerDelegate.debugLogger,"Create solution revision call started");
		MLPSolutionRevision revision = new MLPSolutionRevision();
		revision.setUserId(metadata.getOwnerId());

		/******************* Version Management *********************/
		String version = metadata.getVersion();

		if (version == null) {
			version = getModelVersion(metadata.getSolutionId());
			metadata.setVersion(version);
		}

		revision.setVersion(metadata.getVersion());
		revision.setSolutionId(metadata.getSolutionId());
		 List<MLPCodeNamePair> typeCodeList = cdmsClient.getCodeNamePairs(CodeNameType.ACCESS_TYPE);
		if (!typeCodeList.isEmpty()) {
			for (MLPCodeNamePair mlpCodeNamePair : typeCodeList) {
				if (mlpCodeNamePair.getName().equals(OnboardingConstants.ACCESS_TYPE_PRIVATE))
					revision.setAccessTypeCode(mlpCodeNamePair.getCode());
			}
		}

			List<MLPCodeNamePair> validationStatusList = cdmsClient.getCodeNamePairs(CodeNameType.VALIDATION_STATUS);
		if (!validationStatusList.isEmpty()) {
			for (MLPCodeNamePair mlpCodeNamePair : validationStatusList) {
				if (mlpCodeNamePair.getName().equals(OnboardingConstants.VALIDATION_STATUS_IP))
					revision.setValidationStatusCode(mlpCodeNamePair.getCode());
			}
		}

		try {
			revision = cdmsClient.createSolutionRevision(revision);
			metadata.setRevisionId(revision.getRevisionId());
			logger.debug(EELFLoggerDelegate.debugLogger,"Solution revision created: " + revision.getRevisionId());
			return revision;
		} catch (HttpStatusCodeException e) {
			logger.error(EELFLoggerDelegate.errorLogger,"Creation of solution revision failed: {}", e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Creation of solution revision failed - " + e.getResponseBodyAsString(), e);
		}
	}

	public String getModelVersion(String solutionId) {
		int count = 0;
		List<MLPSolutionRevision> revList = cdmsClient.getSolutionRevisions(solutionId);

		if (revList != null)
			count = revList.size();

		count++;

		return "" + count;
	}

	/**
	 * Uploads the specified artifact to Nexus using group ID read from
	 * configuration and specified artifact ID.
	 *
	 * @param metadata
	 *            Metadata about the artifact
	 * @param file
	 *            Content for artifact
	 * @param typeCode
	 *            Two-letter artifact type code
	 * @param nexusArtifactId
	 *            ID to use in Nexus
	 * @return MLPArtifact object
	 * @throws AcumosServiceException
	 *             On failure
	 */
	public MLPArtifact addArtifact(Metadata metadata, File file, String typeCode, String nexusArtifactId, OnboardingNotification onboardingStatus)
			throws AcumosServiceException {
		String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(nexusEndPointURL);
		repositoryLocation.setUsername(nexusUserName);
		repositoryLocation.setPassword(nexusPassword);
		NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
		logger.debug(EELFLoggerDelegate.debugLogger,"Upload Artifact for {}", file.getName() + " started");
		// Notify add artifacts started
		if (onboardingStatus != null) {
			onboardingStatus.notifyOnboardingStatus("AddArtifact", "ST",
					"Add Artifact for" + file.getName() + " started");
		}
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			int size = (int) file.length();
			String nexusGrpId=nexusGroupId+"."+metadata.getSolutionId();
			UploadArtifactInfo artifactInfo = artifactClient.uploadArtifact(nexusGrpId, nexusArtifactId, metadata.getVersion(), ext, size, fileInputStream);

			logger.debug(EELFLoggerDelegate.debugLogger,
					"Upload Artifact for: {}", file.getName() + " successful response: {}", artifactInfo.getArtifactId());
			try {
				logger.debug(EELFLoggerDelegate.debugLogger,"Add Artifact called for " + file.getName());
				MLPArtifact modelArtifact = new MLPArtifact();
				modelArtifact.setName(file.getName());
				modelArtifact.setDescription(file.getName());
				modelArtifact.setVersion(metadata.getVersion());
				modelArtifact.setArtifactTypeCode(typeCode);
				modelArtifact.setUserId(metadata.getOwnerId());
				modelArtifact.setUri(artifactInfo.getArtifactMvnPath());
				modelArtifact.setSize(size);
				modelArtifact = cdmsClient.createArtifact(modelArtifact);
				logger.debug(EELFLoggerDelegate.debugLogger,"Artifact created for " + file.getName());
				logger.debug(EELFLoggerDelegate.debugLogger,"addSolutionRevisionArtifact for " + file.getName() + " called");
				try {
					cdmsClient.addSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
							modelArtifact.getArtifactId());
					logger.debug(EELFLoggerDelegate.debugLogger, "addSolutionRevisionArtifact for " + file.getName() + " successful");
					// Notify add artifacts successful
					if (onboardingStatus != null) {
						//onboardingStatus.setArtifactId(modelArtifact.getArtifactId());
						onboardingStatus.notifyOnboardingStatus("AddArtifact", "SU",
								"Add Artifact for" + file.getName() + " Successful");
					}
					return modelArtifact;
				} catch (HttpStatusCodeException e) {
					logger.error(EELFLoggerDelegate.errorLogger,"Fail to call addSolutionRevisionArtifact: {}", e);
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
							"Fail to call addSolutionRevisionArtifact for " + file.getName() + " - "
									+ e.getResponseBodyAsString(),
							e);
				}
			} catch (HttpStatusCodeException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Fail to create artificate for {}", file.getName() + " - {}", e.getResponseBodyAsString(), e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
						"Fail to create artificat for " + file.getName() + " - " + e.getResponseBodyAsString(), e);
			}
		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger,"Error: {}", e);
			throw e;
		} catch (Exception e) {
			// Notify add artifacts failed
			if (onboardingStatus != null) {
				onboardingStatus.notifyOnboardingStatus("AddArtifact", "FA", e.getMessage());
			}
			logger.error(EELFLoggerDelegate.errorLogger, "Fail to upload artificat for {}", file.getName() + " - {}", e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificat for " + file.getName() + " - " + e.getMessage(), e);
		}

	}

	public MLPArtifact addArtifact(Metadata metadata, String uri, String typeCode, OnboardingNotification onboardingStatus)
			throws AcumosServiceException {

		try {
			try {
				logger.debug(EELFLoggerDelegate.debugLogger,"Add Artifact - "+uri + " for solution - "+metadata.getSolutionId()+ " started");
				// Notify add artifacts started
				if (onboardingStatus != null) {
					onboardingStatus.notifyOnboardingStatus("AddDockerImage", "ST",
							"Add Artifact for" + uri + " started");
				}
				MLPArtifact modelArtifact = new MLPArtifact();
				modelArtifact.setName(metadata.getModelName());
				modelArtifact.setDescription(uri);
				modelArtifact.setVersion(metadata.getVersion());
				modelArtifact.setArtifactTypeCode(typeCode);
				modelArtifact.setUserId(metadata.getOwnerId());
				modelArtifact.setUri(uri);
				modelArtifact.setSize(-1);
				modelArtifact = cdmsClient.createArtifact(modelArtifact);
				logger.debug(EELFLoggerDelegate.debugLogger,"create Artifact - "+uri + " for solution - "+metadata.getSolutionId()+ " Successful");
				try {
					cdmsClient.addSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
							modelArtifact.getArtifactId());
					logger.debug(EELFLoggerDelegate.debugLogger,"addSolutionRevisionArtifact - "+uri+" for solution - " +metadata.getSolutionId( )+" Successful");
					if (onboardingStatus != null) {
						//onboardingStatus.setArtifactId(modelArtifact.getArtifactId());
						onboardingStatus.notifyOnboardingStatus("AddDockerImage","SU", "Add Artifact - "+uri + " for solution - "+metadata.getSolutionId()+ " Successful");
					}
					return modelArtifact;

				} catch (HttpStatusCodeException e) {

					logger.error(EELFLoggerDelegate.errorLogger,"Fail to call addSolutionRevisionArtifact for solutoin "+metadata.getSolutionId()+ "{}", e.getResponseBodyAsString(), e);
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
							"Fail to call addSolutionRevisionArtifact for " + e.getResponseBodyAsString(), e);
				}
			} catch (HttpStatusCodeException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Fail to create artificate for solutoin " +metadata.getSolutionId()+"{}", e.getResponseBodyAsString(), e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
						"Fail to create artificate for " + e.getResponseBodyAsString(), e);
			}
		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Error: {}", e);
			throw e;
		} catch (Exception e) {
			// Notify model artifact upload failed
			if (onboardingStatus != null) {
				onboardingStatus.notifyOnboardingStatus("AddDockerImage", "FA", e.getMessage());
			}
			logger.error(EELFLoggerDelegate.errorLogger, "Fail to upload artificate for {}", e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificate for " + e.getMessage(), e);
		}

	}

	public void generateTOSCA(File localProtobufFile, File localMetadataFile, Metadata metadata, OnboardingNotification onboardingStatus) {
		logger.debug(EELFLoggerDelegate.debugLogger,"Generate TOSCA started");
		try {

			logger.debug(EELFLoggerDelegate.debugLogger,"nexusGroupId:" + nexusGroupId);
			ToscaGeneratorClient client = new ToscaGeneratorClient(toscaOutputFolder, toscaGeneratorEndPointURL,
					nexusEndPointURL, nexusUserName, nexusPassword, nexusGroupId, cmnDataSvcEndPoinURL, cmnDataSvcUser,
					cmnDataSvcPwd);

			String result = client.generateTOSCA(metadata.getOwnerId(), metadata.getSolutionId(), metadata.getVersion(),
					metadata.getRevisionId(), localProtobufFile, localMetadataFile);
			logger.debug(EELFLoggerDelegate.debugLogger,"Generate TOSCA completed and result:" + result);

		} catch (Exception e) {
			// Notify TOSCA generation failed
			if (onboardingStatus != null) {
				onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "FA", e.getMessage());
			}
			logger.error(EELFLoggerDelegate.errorLogger,"Fail to generate TOSCA for solution - {}", e);
			// Storage of artifact location references in Common Data
			// Store-failure
		}
	}

	public void revertbackOnboarding(Metadata metadata,String solutionID) throws AcumosServiceException {

		try {

			logger.debug(EELFLoggerDelegate.debugLogger,"In RevertbackOnboarding method");
			RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("1");
			repositoryLocation.setUrl(nexusEndPointURL);
			repositoryLocation.setUsername(nexusUserName);
			repositoryLocation.setPassword(nexusPassword);
			NexusArtifactClient nexusClient = new NexusArtifactClient(repositoryLocation);

			if (metadata.getSolutionId() != null) {
				logger.debug(EELFLoggerDelegate.debugLogger,"Solution id: " + metadata.getSolutionId() + "  Revision id: " + metadata.getRevisionId());

				// get the Artifact IDs for given solution
				List<MLPArtifact> artifactids = cdmsClient.getSolutionRevisionArtifacts(metadata.getSolutionId(),
						metadata.getRevisionId());

				// check if artifactids is empty
				// Delete all the artifacts for given solution

				for (MLPArtifact mlpArtifact : artifactids) {
					String artifactId = mlpArtifact.getArtifactId();

					if (!(mlpArtifact.getArtifactTypeCode().equals("LG"))) {

						// Delete SolutionRevisionArtifact
						logger.debug(EELFLoggerDelegate.debugLogger, "Deleting Artifact: " + artifactId);
						cdmsClient.dropSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
								artifactId);
						logger.debug(EELFLoggerDelegate.debugLogger,
								"Successfully Deleted the SolutionRevisionArtifact");

						// Delete Artifact
						cdmsClient.deleteArtifact(artifactId);
						logger.debug(EELFLoggerDelegate.debugLogger, "Successfully Deleted the Artifact");

						// Delete the file from the Nexus
						if (!(mlpArtifact.getArtifactTypeCode().equals("DI"))) {
							nexusClient.deleteArtifact(mlpArtifact.getUri());
							logger.debug(EELFLoggerDelegate.debugLogger,
									"Successfully Deleted the Artifact from Nexus");
						}
					}
				}

				// Delete current revision
				/*cdmsClient.deleteSolutionRevision(metadata.getSolutionId(), metadata.getRevisionId());
				logger.debug(EELFLoggerDelegate.debugLogger,"Successfully Deleted the Solution Revision");*/

			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger,"Onboarding failed");
			logger.error(EELFLoggerDelegate.errorLogger,e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to revert back onboarding changes : " + e.getMessage());
		}
	}

	public void listFilesAndFilesSubDirectories(File directory) {

		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile()) {
				logger.debug(EELFLoggerDelegate.debugLogger,file.getName());
			} else if (file.isDirectory()) {
				listFilesAndFilesSubDirectories(file);
			}
		}
	}

	//Method for getting model name used for Image
	protected String getActualModelName(Metadata metadata, String solutionID) {

		return metadata.getModelName() + "_" + solutionID;
	}
	public String getCmnDataSvcEndPoinURL() {
		return cmnDataSvcEndPoinURL;
	}

	public String getCmnDataSvcUser() {
		return cmnDataSvcUser;
	}

	public String getCmnDataSvcPwd() {
		return cmnDataSvcPwd;
	}

}
