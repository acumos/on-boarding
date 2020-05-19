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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.designstudio.toscagenerator.ToscaGeneratorClient;
import org.acumos.licensemanager.client.LicenseProfile;
import org.acumos.licensemanager.profilevalidator.exceptions.LicenseProfileException;
import org.acumos.licensemanager.profilevalidator.model.LicenseProfileValidationResults;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.nexus.client.data.UploadArtifactInfo;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.proto.Protobuf;
import org.acumos.onboarding.common.proto.RevisionListDateComaparator;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.common.utils.LogBean;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.common.utils.ProtobufUtil;
import org.acumos.onboarding.common.utils.ResourceUtils;
import org.acumos.onboarding.common.utils.UtilityFunction;
import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.acumos.onboarding.logging.OnboardingLogConstants;
import org.acumos.securityverification.domain.Workflow;
import org.acumos.securityverification.service.SecurityVerificationClientServiceImpl;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.HttpStatusCodeException;

import com.networknt.schema.ValidationMessage;

public class CommonOnboarding {

	private static final Logger log = LoggerFactory.getLogger(CommonOnboarding.class);
	LoggerDelegate logger = new LoggerDelegate(log);

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

	@Value("${docker.imagetag.prefix}")
	protected String imagetagPrefix;

	@Value("${docker.imagetag.proxyPrefix}")
	protected String imagetagProxyPrefix;

	@Value("${security.verificationApiUrl}")
	protected String securityVerificationApiUrl;

	@Value("${security.verificationEnableFlag}")
	protected Boolean securityVerificationEnable;

	protected String modelOriginalName = null;

	protected boolean dcaeflag = false;

	@Autowired
	protected ResourceLoader resourceLoader;

	protected MetadataParser metadataParser = null;

	protected CommonDataServiceRestClientImpl cdmsClient;

	protected PortalRestClientImpl portalClient;

	protected MicroserviceRestClientImpl microserviceClient;

	protected ResourceUtils resourceUtils;

	Map<String, String> toolkitTypeDetails = new HashMap<>();

	@PostConstruct
	public void init() {
		logger.debug("Creating docker service instance");
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
			logger.debug("Api Token validation started");
			MLPUser mUser = cdmsClient.loginApiUser(loginName, token);
			tokenVal = mUser.isActive();
			ownerID = mUser.getUserId();
			logger.debug("OwnerID: " + ownerID);
		}

		if (tokenVal == false) {

			logger.debug("JWT Token validation started");
			JSONObject obj1 = new JSONObject();
			obj1.put("jwtToken", token);

			JSONObject obj2 = new JSONObject();
			obj2.put("request_body", obj1);

			valid = portalClient.tokenValidation(obj2, provider);
			ownerID = valid.getResponseBody().toString();
			logger.debug("OwnerID: " + ownerID);
		}

		return ownerID;
	}

	private String[] splitAuthorization(String authorization) {

		String[] values = authorization.split(":");
		return values;
	}

	public static String getExtensionOfFile(String fileName)
	{
		String fileExtension="";

		// If fileName do not contain "." or starts with "." then it is not a valid file
		if(fileName.contains(".") && fileName.lastIndexOf(".")!= 0)
		{
			fileExtension=fileName.substring(fileName.lastIndexOf(".")+1);
		}

		return fileExtension;
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
		logger.debug("Search Solution with criteria ownerId = " +ownerId + ", ModelName = " +modelName+ ", Active = true");

		/* TRUE - OR , FALSE - AND */
		RestPageResponse<MLPSolution> pageResponse = cdmsClient.searchSolutions(queryParameters, false,
				new RestPageRequest(0, 9));
		return pageResponse.getContent();

	}

	public MLPSolution createSolution(Metadata metadata, OnboardingNotification onboardingStatus) throws AcumosServiceException {
		logger.debug("Create solution call started");
		MLPSolution solution = new MLPSolution();
		solution.setName(metadata.getSolutionName());
		//solution.setDescription(metadata.getSolutionName());
		solution.setUserId(metadata.getOwnerId());

		logger.debug("Model name[CreateSolutionMethod] :"+metadata.getSolutionName());

		toolkitTypeDetails = getToolkitTypeDetails();
		String toolKit = metadata.getToolkit();

		if (toolKit != null) {
			solution.setToolkitTypeCode(getToolkitTypeCode(toolKit));
		} else if (dcaeflag) {
			solution.setToolkitTypeCode("ON");
		}

		solution.setActive(true);
		try {
			solution = cdmsClient.createSolution(solution);
			metadata.setSolutionId(solution.getSolutionId());
			logger.debug("Solution created: " + solution.getSolutionId());
			// Creat solution id - success

			return solution;

		} catch (HttpStatusCodeException e) {
			// Creat solution id - fail
			// Create Solution failed. Notify
			if (onboardingStatus != null) {
				// notify
				try {
					onboardingStatus.notifyOnboardingStatus("CreateMicroservice", "FA", e.getMessage());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			logger.error( "Creation of solution failed - "+ e.getResponseBodyAsString(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Creation of solution failed - " + e.getResponseBodyAsString(), e);
		}
	}

	private Map<String, String> getToolkitTypeDetails() {
		List<MLPCodeNamePair> typeCodeList = cdmsClient.getCodeNamePairs(CodeNameType.TOOLKIT_TYPE);
		Map<String, String> toolkitTypeDetails = new HashMap<>();
		if (!typeCodeList.isEmpty()) {
			for (MLPCodeNamePair codeNamePair : typeCodeList) {
				toolkitTypeDetails.put(codeNamePair.getName(), codeNamePair.getCode());
			}
		}
		return toolkitTypeDetails;
	}

	public String getToolkitTypeCode(String toolkitTypeName) {
		String typeCode = toolkitTypeDetails.get(toolkitTypeName);
		return typeCode;
	}

	/*public String getToolTypeCode(String toolkit) {
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
	 */
	public MLPSolutionRevision createSolutionRevision(Metadata metadata) throws AcumosServiceException {
		logger.debug("Create solution revision call started");
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
		/*List<MLPCodeNamePair> typeCodeList = cdmsClient.getCodeNamePairs(CodeNameType.ACCESS_TYPE);
		if (!typeCodeList.isEmpty()) {
			for (MLPCodeNamePair mlpCodeNamePair : typeCodeList) {
				if (mlpCodeNamePair.getName().equals(OnboardingConstants.ACCESS_TYPE_PRIVATE))
					revision.setAccessTypeCode(mlpCodeNamePair.getCode());
			}
		}*/

		/*List<MLPCodeNamePair> validationStatusList = cdmsClient.getCodeNamePairs(CodeNameType.VALIDATION_STATUS);
		if (!validationStatusList.isEmpty()) {
			for (MLPCodeNamePair mlpCodeNamePair : validationStatusList) {
				if (mlpCodeNamePair.getName().equals(OnboardingConstants.VALIDATION_STATUS_IP))
					revision.setValidationStatusCode(mlpCodeNamePair.getCode());
			}
		}*/

		try {
			revision = cdmsClient.createSolutionRevision(revision);
			metadata.setRevisionId(revision.getRevisionId());
			logger.debug("Solution revision created: " + revision.getRevisionId());
			return revision;
		} catch (HttpStatusCodeException e) {
			logger.error("Creation of solution revision failed: "+ e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Creation of solution revision failed - " + e.getResponseBodyAsString(), e);
		}
	}

	public MLPSolutionRevision createSolutionRevision(Metadata metadata, File localProtoFile) throws AcumosServiceException {
		logger.debug("Create solution revision call started");
		MLPSolutionRevision revision = new MLPSolutionRevision();
		revision.setUserId(metadata.getOwnerId());

		/******************* Version Management *********************/
		String version = metadata.getVersion();
		logger.debug("Version in createSolutionRevision method : "+version);
		if (version == null) {
			version = getModelVersion(metadata.getSolutionId(), localProtoFile);
			metadata.setVersion(version);
		}

		logger.debug("After Setting Version in createSolutionRevision method in Metedata: "+metadata.getVersion());

		revision.setVersion(metadata.getVersion());
		revision.setSolutionId(metadata.getSolutionId());
		/*List<MLPCodeNamePair> typeCodeList = cdmsClient.getCodeNamePairs(CodeNameType.ACCESS_TYPE);
		if (!typeCodeList.isEmpty()) {
			for (MLPCodeNamePair mlpCodeNamePair : typeCodeList) {
				if (mlpCodeNamePair.getName().equals(OnboardingConstants.ACCESS_TYPE_PRIVATE))
					revision.setAccessTypeCode(mlpCodeNamePair.getCode());
			}
		}*/

		/*List<MLPCodeNamePair> validationStatusList = cdmsClient.getCodeNamePairs(CodeNameType.VALIDATION_STATUS);
		if (!validationStatusList.isEmpty()) {
			for (MLPCodeNamePair mlpCodeNamePair : validationStatusList) {
				if (mlpCodeNamePair.getName().equals(OnboardingConstants.VALIDATION_STATUS_IP))
					revision.setValidationStatusCode(mlpCodeNamePair.getCode());
			}
		}*/

		try {
			revision = cdmsClient.createSolutionRevision(revision);
			metadata.setRevisionId(revision.getRevisionId());
			logger.debug("Solution revision created: " + revision.getRevisionId());
			return revision;
		} catch (HttpStatusCodeException e) {
			logger.error("Creation of solution revision failed: "+ e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Creation of solution revision failed - " + e.getResponseBodyAsString(), e);
		}
	}

	public String getModelVersion(String solutionId) {
		int count = 0;

		List<MLPSolutionRevision> revList = cdmsClient.getSolutionRevisions(solutionId);

		if (revList != null) {
			count = revList.size();
		}
		count++;
		return "" + count +".0.0";
	}

	public String getModelVersion(String solutionId, File localProtoFile) {
		int count = 0;
		String countMajor = "1";
		String countMinor = "0";
		String countIncremental = "0";
		String countTemp = "";
		FileInputStream fis = null;
		String lastProtobufString = "";
		String currentProtobufString = "";
		String lastVersion = "";
		String version = "";
		String lastRevisionId = "";

		try {

			if (localProtoFile != null) {
				fis = new FileInputStream(localProtoFile);
				currentProtobufString = IOUtils.toString(fis, StandardCharsets.UTF_8);
			}
			logger.debug("Current Protobuf String :- " + currentProtobufString);
			List<MLPSolutionRevision> revList = cdmsClient.getSolutionRevisions(solutionId);
			if (revList != null && !revList.isEmpty() && revList.size() != 0) {
				Collections.sort(revList, new RevisionListDateComaparator());
				count = revList.size();
				logger.debug("Last Version's MLPSolutionRevision : " + revList.get(count-1));
				logger.debug("Last Version's MLPSolutionRevision's Size : " + count);
				lastRevisionId = revList.get(count-1).getRevisionId();
				lastVersion = revList.get(count-1).getVersion();
				logger.debug("Last Version's Revision Id: " + lastRevisionId);
				logger.debug("Last Version : " + lastVersion);

				countTemp = lastVersion;
				if (countTemp != null) {
					if (countTemp.contains(".")) {
						countMajor = countTemp.substring(0, countTemp.indexOf("."));
						countMinor = countTemp.substring(countTemp.indexOf(".") + 1, countTemp.lastIndexOf("."));
						countIncremental = countTemp.substring(countTemp.lastIndexOf(".") + 1);
						countIncremental = (Integer.parseInt(countIncremental) + 1) + "";
					} else {
						if (!countTemp.equals("")) {
							countMajor = countTemp;
							countIncremental = (Integer.parseInt(countIncremental) + 1) + "";
						}
					}
				}

				lastProtobufString = getLastProtobuf(solutionId, lastRevisionId);
				logger.debug("Last Protobuf String :- " + lastProtobufString);
				logger.debug("countMajor = " + countMajor + ", countMinor = " + countMinor + ", countIncremental = "
						+ countIncremental);
				version = getRevisionVersion(lastProtobufString, currentProtobufString, countMajor, countMinor,
						countIncremental);
				return version;
			}
			version = ProtobufRevision.getFullVersion(countMajor, countMinor, countIncremental);
			logger.debug("Set a New Version = "+version);

		} catch (Exception e) {
			logger.error("Failed to fetch and compare the Proto files : " + e.getMessage());
			e.printStackTrace();
		}
		return version;
	}

	public String getLastProtobuf(String solutionId, String revisionId) {

		String lastProtoBuffString = "";
		String modelId = UtilityFunction.getGUID();
		File files = new File("tmp", modelId);;
		files.mkdirs();

		try {

			List<String> artifactNameList = new ArrayList<String>();
			files = new File("model");

			File protoFile = null;

			DownloadModelArtifacts download = new DownloadModelArtifacts();
			logger.debug("solutionId: " + solutionId + ", revisionId: " + revisionId);

			artifactNameList = download.getModelProtoArtifacts(solutionId, revisionId, cmnDataSvcUser, cmnDataSvcPwd,
					nexusEndPointURL, nexusUserName, nexusPassword, cmnDataSvcEndPoinURL);

			logger.debug("Number of artifacts: "+ artifactNameList.size());

			for (String name : artifactNameList) {
				if (name.toLowerCase().contains(".proto")) {
					logger.debug("Last ProtoFile: " + name);
					protoFile = download.getArtifactFile();
				}

			}
			if (protoFile != null && protoFile.exists()) {

				FileInputStream fisProto = new FileInputStream(protoFile);
				lastProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}
		} catch (Exception e) {
			logger.error("Failed to fetch the Last Protofile : "+e.getMessage());
		}finally {
			UtilityFunction.deleteDirectory(files);
		}

		return lastProtoBuffString;
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
		logger.debug("Upload Artifact for " + file.getName() + " started");
		// Notify add artifacts started
		if (onboardingStatus != null) {
			try {
				onboardingStatus.notifyOnboardingStatus("AddArtifact", "ST",
						"Add Artifact for " + file.getName() + " started");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			int size = (int) file.length();
			String nexusGrpId=nexusGroupId+"."+metadata.getSolutionId();
			logger.debug("MetaData Version before uploading artifact = " +metadata.getVersion());
			UploadArtifactInfo artifactInfo = artifactClient.uploadArtifact(nexusGrpId, nexusArtifactId, metadata.getVersion(), ext, size, fileInputStream);
			logger.debug(
					"Upload Artifact for: " + file.getName() + " successful response: " + artifactInfo.getArtifactId());
			try {
				logger.debug("Add Artifact called for " + file.getName());
				MLPArtifact modelArtifact = new MLPArtifact();
				modelArtifact.setName(file.getName());
				modelArtifact.setDescription(file.getName());
				modelArtifact.setVersion(metadata.getVersion());
				modelArtifact.setArtifactTypeCode(typeCode);
				modelArtifact.setUserId(metadata.getOwnerId());
				modelArtifact.setUri(artifactInfo.getArtifactMvnPath());
				modelArtifact.setSize(size);
				modelArtifact = cdmsClient.createArtifact(modelArtifact);
				logger.debug("Artifact created for " + file.getName());
				logger.debug("addSolutionRevisionArtifact for " + file.getName() + " called");
				try {
					cdmsClient.addSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
							modelArtifact.getArtifactId());
					logger.debug( "addSolutionRevisionArtifact for " + file.getName() + " successful");
					// Notify add artifacts successful
					if (onboardingStatus != null) {
						//onboardingStatus.setArtifactId(modelArtifact.getArtifactId());
						onboardingStatus.notifyOnboardingStatus("AddArtifact", "SU",
								"Add Artifact for " + file.getName() + " Successful");
					}
					return modelArtifact;
				} catch (HttpStatusCodeException e) {
					logger.error("Fail to call addSolutionRevisionArtifact: " + e);
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
							"Fail to call addSolutionRevisionArtifact for " + file.getName() + " - "
									+ e.getResponseBodyAsString(),
									e);
				}
			} catch (HttpStatusCodeException e) {
				logger.error( "Fail to create artificate for " + file.getName() + " - " + e.getResponseBodyAsString(), e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
						"Fail to create artificat for " + file.getName() + " - " + e.getResponseBodyAsString(), e);
			}
		} catch (AcumosServiceException e) {
			logger.error("Error: " + e);
			throw e;
		} catch (Exception e) {
			// Notify add artifacts failed
			if (onboardingStatus != null) {
				try {
					onboardingStatus.notifyOnboardingStatus("AddArtifact", "FA", e.getMessage());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			logger.error( "Fail to upload artificat for " + file.getName() + " - " + e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificat for " + file.getName() + " - " + e.getMessage(), e);
		}

	}

	public MLPArtifact addArtifact(Metadata metadata, File file, String typeCode, String nexusArtifactId, OnboardingNotification onboardingStatus, LogBean logBean)
			throws AcumosServiceException {
		MLPArtifact mlpArtifact;
		try {
			mlpArtifact = addArtifact(metadata, file, "LG", nexusArtifactId, onboardingStatus);
			logger.debug("Upload Artifact for " + file.getName() + " started", logBean);
			// Notify add artifacts started
			if (onboardingStatus != null) {
				onboardingStatus.notifyOnboardingStatus("AddArtifact", "ST",
						"Add Artifact for " + file.getName() + " started", logBean);
			}
			// logger.debug(
			// "Upload Artifact for: " + file.getName() + " successful response: " +
			// artifactInfo.getArtifactId());
			logger.debug("Add Artifact called for " + file.getName(), logBean);
			logger.debug("Artifact created for " + file.getName(), logBean);
			logger.debug("addSolutionRevisionArtifact for " + file.getName() + " called", logBean);
			logger.debug("addSolutionRevisionArtifact for " + file.getName() + " successful", logBean);
			if (onboardingStatus != null) {
				onboardingStatus.notifyOnboardingStatus("AddArtifact", "SU",
						"Add Artifact for " + file.getName() + " Successful", logBean);
			}
			return mlpArtifact;
		} catch (AcumosServiceException e) {
			logger.error("Error: " + e);
			throw e;
		} catch (Exception e) {
			// Notify add artifacts failed
			if (onboardingStatus != null) {
				try {
					onboardingStatus.notifyOnboardingStatus("AddArtifact", "FA", e.getMessage(), logBean);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			logger.error( "Fail to upload artificat for " + file.getName() + " - " + e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificat for " + file.getName() + " - " + e.getMessage(), e);
		}
	}

	public MLPArtifact addArtifact(Metadata metadata, String uri, String typeCode, OnboardingNotification onboardingStatus)
			throws AcumosServiceException {

		try {
			try {
				logger.debug("Add Artifact - "+uri + " for solution - "+metadata.getSolutionId()+ " started");
				// Notify add artifacts started
				if (onboardingStatus != null) {
					onboardingStatus.notifyOnboardingStatus("AddDockerImage", "ST",
							"Add Artifact for" + uri + " started");
				}
				if (metadata.getVersion() == null || metadata.getVersion().isEmpty()) {
					metadata.setVersion("1.0.0");
				}
				logger.debug("MetaData Version before uploading artifact = " +metadata.getVersion());
				MLPArtifact modelArtifact = new MLPArtifact();
				modelArtifact.setName(metadata.getModelName());
				modelArtifact.setDescription(uri);
				modelArtifact.setVersion(metadata.getVersion());
				modelArtifact.setArtifactTypeCode(typeCode);
				modelArtifact.setUserId(metadata.getOwnerId());
				modelArtifact.setUri(uri);
				modelArtifact.setSize(-1);
				modelArtifact = cdmsClient.createArtifact(modelArtifact);
				logger.debug("create Artifact - "+uri + " for solution - "+metadata.getSolutionId()+ " Successful");
				try {
					cdmsClient.addSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
							modelArtifact.getArtifactId());
					logger.debug("addSolutionRevisionArtifact - "+uri+" for solution - " +metadata.getSolutionId( )+" Successful");
					if (onboardingStatus != null) {
						//onboardingStatus.setArtifactId(modelArtifact.getArtifactId());
						onboardingStatus.notifyOnboardingStatus("AddDockerImage","SU", "Add Artifact - "+uri + " for solution - "+metadata.getSolutionId()+ " Successful");
					}
					return modelArtifact;

				} catch (HttpStatusCodeException e) {

					logger.error("Fail to call addSolutionRevisionArtifact for solutoin "+metadata.getSolutionId()+ e.getResponseBodyAsString(), e);
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
							"Fail to call addSolutionRevisionArtifact for " + e.getResponseBodyAsString(), e);
				}
			} catch (HttpStatusCodeException e) {
				logger.error( "Fail to create artificate for solutoin " +metadata.getSolutionId()+ e.getResponseBodyAsString(), e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
						"Fail to create artificate for " + e.getResponseBodyAsString(), e);
			}
		} catch (AcumosServiceException e) {
			logger.error( "Error: "+ e);
			throw e;
		} catch (Exception e) {
			// Notify model artifact upload failed
			if (onboardingStatus != null) {
				try {
					onboardingStatus.notifyOnboardingStatus("AddDockerImage", "FA", e.getMessage());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
			logger.error( "Fail to upload artificate for " + e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificate for " + e.getMessage(), e);
		}

	}

	public MLPArtifact addArtifact(Metadata metadata, String uri, String typeCode, OnboardingNotification onboardingStatus, LogBean logBean)
			throws AcumosServiceException {

		MLPArtifact mlpArtifact;
		try {
			mlpArtifact = addArtifact(metadata, uri, typeCode, onboardingStatus);
			logger.debug("Add Artifact - " + uri + " for solution - " + metadata.getSolutionId() + " started", logBean);
			// Notify add artifacts started
			if (onboardingStatus != null) {
				onboardingStatus.notifyOnboardingStatus("AddDockerImage", "ST", "Add Artifact for" + uri + " started",
						logBean);
			}

			logger.debug("create Artifact - " + uri + " for solution - " + metadata.getSolutionId() + " Successful",
					logBean);
			logger.debug("addSolutionRevisionArtifact - " + uri + " for solution - " + metadata.getSolutionId()
			+ " Successful", logBean);
			if (onboardingStatus != null) {
				// onboardingStatus.setArtifactId(modelArtifact.getArtifactId());
				onboardingStatus.notifyOnboardingStatus("AddDockerImage", "SU",
						"Add Artifact - " + uri + " for solution - " + metadata.getSolutionId() + " Successful",
						logBean);
			}
			return mlpArtifact;

		} catch (AcumosServiceException e) {
			logger.error("Error: " + e);
			throw e;
		} catch (Exception e) {
			// Notify model artifact upload failed
			if (onboardingStatus != null) {
				try {
					onboardingStatus.notifyOnboardingStatus("AddDockerImage", "FA", e.getMessage());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			logger.error("Fail to upload artificate for " + e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificate for " + e.getMessage(), e);
		}
	}

	public void generateTOSCA(File localProtobufFile, File localMetadataFile, Metadata metadata, OnboardingNotification onboardingStatus) {
		logger.debug("Generate TOSCA started");
		try {

			logger.debug("nexusGroupId:" + nexusGroupId);
			ToscaGeneratorClient client = new ToscaGeneratorClient(toscaOutputFolder, toscaGeneratorEndPointURL,
					nexusEndPointURL, nexusUserName, nexusPassword, nexusGroupId, cmnDataSvcEndPoinURL, cmnDataSvcUser,
					cmnDataSvcPwd);

			logger.debug("MetaData Version before Generating TOSCA = " + metadata.getVersion());

			String result = null;
			if(localMetadataFile != null) {
				result = client.generateTOSCA(metadata.getOwnerId(), metadata.getSolutionId(), metadata.getVersion(),
						metadata.getRevisionId(), localProtobufFile, localMetadataFile);
			} else {
				logger.debug("Predockerize tosca generation started. ");
				result = client.generateTOSCA(metadata.getOwnerId(), metadata.getSolutionId(), metadata.getVersion(), metadata.getRevisionId(),
						localProtobufFile, metadata.getModelName(), null);
			}



			logger.debug("Generate TOSCA completed and result:" + result);

		} catch (Exception e) {
			// Notify TOSCA generation failed
			if (onboardingStatus != null) {
				try {
					onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "FA", e.getMessage());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			logger.error("Fail to generate TOSCA for solution - " + e);
			// Storage of artifact location references in Common Data
			// Store-failure
		}
	}

	public void revertbackOnboarding(Metadata metadata,String solutionID) throws AcumosServiceException {

		try {

			logger.debug("In RevertbackOnboarding method");
			RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("1");
			repositoryLocation.setUrl(nexusEndPointURL);
			repositoryLocation.setUsername(nexusUserName);
			repositoryLocation.setPassword(nexusPassword);
			NexusArtifactClient nexusClient = new NexusArtifactClient(repositoryLocation);

			if (metadata.getSolutionId() != null) {
				logger.debug("Solution id: " + metadata.getSolutionId() + "  Revision id: " + metadata.getRevisionId());

				// get the Artifact IDs for given solution
				List<MLPArtifact> artifactids = cdmsClient.getSolutionRevisionArtifacts(metadata.getSolutionId(),
						metadata.getRevisionId());

				// check if artifactids is empty
				// Delete all the artifacts for given solution

				for (MLPArtifact mlpArtifact : artifactids) {
					String artifactId = mlpArtifact.getArtifactId();

					if (!(mlpArtifact.getArtifactTypeCode().equals("LG"))) {

						// Delete SolutionRevisionArtifact
						logger.debug( "Deleting Artifact: " + artifactId);
						cdmsClient.dropSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
								artifactId);
						logger.debug(
								"Successfully Deleted the SolutionRevisionArtifact");

						// Delete Artifact
						cdmsClient.deleteArtifact(artifactId);
						logger.debug( "Successfully Deleted the Artifact");

						// Delete the file from the Nexus
						if (!(mlpArtifact.getArtifactTypeCode().equals("DI"))) {
							nexusClient.deleteArtifact(mlpArtifact.getUri());
							logger.debug(
									"Successfully Deleted the Artifact from Nexus");
						}
					}
				}

				// Delete current revision
				/*cdmsClient.deleteSolutionRevision(metadata.getSolutionId(), metadata.getRevisionId());
				logger.debug("Successfully Deleted the Solution Revision");*/

			}
		} catch (Exception e) {
			logger.error("Onboarding failed");
			logger.error(e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to revert back onboarding changes : " + e.getMessage());
		}
	}

	public void listFilesAndFilesSubDirectories(File directory) {

		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile()) {
				logger.debug(file.getName());
			} else if (file.isDirectory()) {
				listFilesAndFilesSubDirectories(file);
			}
		}
	}

	public String getRevisionVersion(String lastProtobufString, String currentProtobufString, String countMajor,
			String countMinor, String countIncremental) {

		ProtobufRevision protoRevision = new ProtobufRevision();
		Protobuf protoBuf1 = null;
		Protobuf protoBuf2 = null;

		//Check if the lastProtobuf is not null
		if (lastProtobufString != null && !lastProtobufString.isEmpty()) {
			protoBuf1 = ProtobufUtil.parseProtobuf(lastProtobufString);
			protoBuf2 = ProtobufUtil.parseProtobuf(currentProtobufString);
		}
		String verA = countMajor;
		String verB = countMinor;
		String verC = countIncremental;
		String version = "";

		List<String> versionList = new ArrayList<>();
		versionList.add(verA);
		versionList.add(verB);
		versionList.add(verC);

		int countA = 0;
		int countB = 0;
		int countC = 0;
		if(Integer.parseInt(countIncremental) != 0) {
			countC = Integer.parseInt(countIncremental);
		}

		if (protoBuf1 != null && protoBuf2 != null) {

			versionList = protoRevision.checkServiceParameters(versionList, protoBuf1, protoBuf2, countA, countB,
					countC, countMajor, countMinor, countIncremental);
			if (versionList.get(0).equals(countMajor) && versionList.get(1).equals(countMinor) && versionList.get(2).equals(countIncremental)) {

				versionList = protoRevision.checkMessageParameters(versionList, protoBuf1, protoBuf2, countA, countB,
						countC, countMajor, countMinor, countIncremental);
			}
		}

		version = ProtobufRevision.getFullVersion(versionList.get(0), versionList.get(1), versionList.get(2));
		logger.debug("New Revision Version = " + version);
		return version;
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
	public String validateLicense(String license) throws LicenseProfileException, AcumosServiceException
	{
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			LicenseProfile licenseProfile= new LicenseProfile(dataServiceRestClient);
			LicenseProfileValidationResults licenseProfileValidationResults=licenseProfile.validate(license);
			Set<ValidationMessage> errMesgList = licenseProfileValidationResults.getJsonSchemaErrors();

			if(errMesgList == null || errMesgList.isEmpty()) {
				logger.debug("License validated Successfully. ");
				return "SUCCESS";
			} else {
				logger.debug("Failed to validate license. ");
				return errMesgList.toString();
			}

		} catch (LicenseProfileException licExp) {
			logger.error("Exception occurred during License validation: "+licExp.getMessage());
			throw licExp;
		} catch (Exception e) {
			logger.error("Exception occurred during License validation: "+e.getMessage());
			throw e;
		}
	}
	public ICommonDataServiceRestClient getClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd, null);
		client.setRequestId(MDC.get(OnboardingLogConstants.MDCs.REQUEST_ID));
		return client;
	}
	public Workflow performSVScan(String solutionId, String revisionId, String workflowId, String loggedInUserId) {
		logger.debug("performSVScan, solutionId=" + solutionId + ", revisionId=" + revisionId + ", workflowId=" + workflowId);
		logger.debug("Security Verificaton enable= "+securityVerificationEnable);
		Workflow workflow = getValidWorkflow();
		if (securityVerificationEnable) {
			try {
				SecurityVerificationClientServiceImpl sv = getSVClient();

				workflow = sv.securityVerificationScan(solutionId, revisionId, workflowId, loggedInUserId);
				if (!workflow.isWorkflowAllowed()) {
					String message = (!UtilityFunction.isEmptyOrNullString(workflow.getSvException()))
							? workflow.getSvException()
									: (!UtilityFunction.isEmptyOrNullString(workflow.getReason())) ? workflow.getReason()
											: "Unknown problem occurred during security verification";
									workflow.setReason(message);
									logger.error("Problem occurred during SV scan: ", message);
				} else {
					logger.debug("SV Scan completed :  "+workflow);
				}
			} catch (Exception e) {
				String message = (e.getMessage() != null) ? e.getMessage() : e.getClass().getName();
				workflow = getInvalidWorkflow(message);
				logger.error("Exception occurred during SV scan: ", message);
			}
		}
		return workflow;
	}

	protected Workflow getValidWorkflow() {
		Workflow workflow = new Workflow();
		workflow.setWorkflowAllowed(true);
		return workflow;
	}

	protected Workflow getInvalidWorkflow(String message) {
		Workflow workflow = new Workflow();
		workflow.setWorkflowAllowed(false);
		workflow.setReason(message);
		return workflow;
	}

	protected SecurityVerificationClientServiceImpl getSVClient() {
		SecurityVerificationClientServiceImpl securityVerificationServiceImpl = new SecurityVerificationClientServiceImpl(
				securityVerificationApiUrl,cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd,
				nexusEndPointURL, nexusUserName, nexusPassword
				);

		return securityVerificationServiceImpl;
	}

}
