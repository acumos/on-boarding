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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.ToolkitTypeCode;
import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.designstudio.toscagenerator.ToscaGeneratorClient;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.nexus.client.data.UploadArtifactInfo;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.common.utils.ResourceUtils;
import org.acumos.onboarding.common.utils.UtilityFunction;
import org.acumos.onboarding.component.docker.DockerClientFactory;
import org.acumos.onboarding.component.docker.DockerConfiguration;
import org.acumos.onboarding.component.docker.cmd.CreateImageCommand;
import org.acumos.onboarding.component.docker.cmd.DeleteImageCommand;
import org.acumos.onboarding.component.docker.cmd.PushImageCommand;
import org.acumos.onboarding.component.docker.cmd.TagImageCommand;
import org.acumos.onboarding.component.docker.preparation.H2ODockerPreparator;
import org.acumos.onboarding.component.docker.preparation.JavaGenericDockerPreparator;
import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.acumos.onboarding.component.docker.preparation.PythonDockerPreprator;
import org.acumos.onboarding.component.docker.preparation.RDockerPreparator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.HttpStatusCodeException;

import com.github.dockerjava.api.DockerClient;

public class CommonOnboarding {
	
	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(CommonOnboarding.class);
	
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
	String toscaOutputFolder;

	@Value("${tosca.GeneratorEndPointURL}")
	String toscaGeneratorEndPointURL;

	@Value("${http_proxy}")
	String http_proxy;

	@Value("${requirements.extraIndexURL}")
	String extraIndexURL;

	@Value("${requirements.trustedHost}")
	String trustedHost;

	@Value("${mktPlace.mktPlaceEndPoinURL}")
	protected String portalURL;

	protected String modelOriginalName = null;
	
	protected boolean dcaeflag = false;
	
	protected OnboardingNotification onboardingStatus;
	
	@Autowired
	protected ResourceLoader resourceLoader;

	@Autowired
	protected DockerConfiguration dockerConfiguration;
	
	protected MetadataParser metadataParser = null;
	
	protected CommonDataServiceRestClientImpl cdmsClient;

	protected PortalRestClientImpl portalClient;

	protected ResourceUtils resourceUtils;
	
	@PostConstruct
	public void init() {
		
		logger.debug(EELFLoggerDelegate.debugLogger,"Creating docker service instance");
		this.cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd);
		this.portalClient = new PortalRestClientImpl(portalURL);
		this.resourceUtils = new ResourceUtils(resourceLoader);
	}
	
	/*
	 * @Method Name : validate Accepts JWT token in the form of String object.
	 * Validates it and returns validity status and ownerId.
	 */
	@SuppressWarnings("unchecked")
	public JsonResponse<Object> validate(String jwtToken, String provider) throws AcumosServiceException {

		JSONObject obj1 = new JSONObject();
		obj1.put("jwtToken", jwtToken);

		JSONObject obj2 = new JSONObject();
		obj2.put("request_body", obj1);

		JsonResponse<Object> valid = portalClient.tokenValidation(obj2, provider);

		return valid;
	}
	/*
	 * @Method Name : getExistingSolution Gives existing solution against
	 * ownerId and Model name if any. *
	 */
	public List<MLPSolution> getExistingSolution(Metadata metadata) {

		String ownerId = metadata.getOwnerId();
		String modelName = metadata.getModelName();

		Map<String, Object> queryParameters = new HashMap<String, Object>();

		queryParameters.put("ownerId", ownerId);
		queryParameters.put("name", modelName);

		/* TRUE - OR , FALSE - AND */
		RestPageResponse<MLPSolution> pageResponse = cdmsClient.searchSolutions(queryParameters, false,
				new RestPageRequest(0, 9));
		return pageResponse.getContent();

	}

	/*
	 * @Method Name : dockerizeFile Performs complete dockerization process.
	 */
	public String dockerizeFile(MetadataParser metadataParser, File localmodelFile, String solutionID) throws AcumosServiceException {
		File outputFolder = localmodelFile.getParentFile();
		Metadata metadata = metadataParser.getMetadata();
		logger.debug(EELFLoggerDelegate.debugLogger,"Preparing app in: {}", outputFolder);
		if (metadata.getRuntimeName().equals("python")) {
			outputFolder = new File(localmodelFile.getParentFile(), "app");
			outputFolder.mkdir();
			
			Resource[] resources = null;
			
			if(dcaeflag)
			{
				resources = this.resourceUtils.loadResources("classpath*:templates/dcae_python/*");
			}
			else
			{
				resources = this.resourceUtils.loadResources("classpath*:templates/python/*");
			}

			PythonDockerPreprator dockerPreprator = new PythonDockerPreprator(metadataParser, extraIndexURL,
					trustedHost,http_proxy);
			
			for (Resource resource : resources) {
				UtilityFunction.copyFile(resource, new File(outputFolder, resource.getFilename()));
			}
			try {
				// TODO: why is this modelFolder variable ignored?
				File modelFolder = new File(outputFolder, "model");
				UtilityFunction.unzip(localmodelFile, modelFolder.getAbsolutePath());
			} catch (IOException e) {
				logger.error(EELFLoggerDelegate.errorLogger,"Python templatization failed: {}", e);
			}
			dockerPreprator.prepareDockerAppV2(outputFolder);
		} else if (metadata.getRuntimeName().equals("r")) {
			RDockerPreparator dockerPreprator = new RDockerPreparator(metadataParser, http_proxy);
			Resource[] resources = this.resourceUtils.loadResources("classpath*:templates/r/*");
			for (Resource resource : resources) {
				UtilityFunction.copyFile(resource, new File(outputFolder, resource.getFilename()));
			}
			dockerPreprator.prepareDockerApp(outputFolder);
		} else if (metadata.getRuntimeName().equals("javaargus")) {
			try {
				String outputFile = UtilityFunction.getFileName(localmodelFile, outputFolder.toString());
				File tarFile = new File(outputFile);
				tarFile = UtilityFunction.deCompressGZipFile(localmodelFile, tarFile);
				UtilityFunction.unTarFile(tarFile, outputFolder);
			} catch (IOException e) {
				logger.error(EELFLoggerDelegate.errorLogger,"Java Argus templatization failed: {}", e);
			}
		} else if (metadata.getRuntimeName().equals("h2o")) {

			File plugin_root = new File(outputFolder, "plugin_root");
			plugin_root.mkdirs();
			File plugin_src = new File(plugin_root, "src");
			plugin_src.mkdirs();
			File plugin_classes = new File(plugin_root, "classes");
			plugin_classes.mkdirs();

			H2ODockerPreparator dockerPreprator = new H2ODockerPreparator(metadataParser);

			Resource[] resources = this.resourceUtils.loadResources("classpath*:templates/h2o/*");
			for (Resource resource : resources) {
				UtilityFunction.copyFile(resource, new File(outputFolder, resource.getFilename()));
			}
			try {
				UtilityFunction.unzip(localmodelFile, outputFolder.getAbsolutePath());

				String mm[] = modelOriginalName.split("\\.");

				File fd = new File(outputFolder.getAbsolutePath() + "/" + mm[0]);

				File ff[] = fd.listFiles();

				if (ff != null) {
					for (File f : ff) {
						FileUtils.copyFileToDirectory(f, outputFolder);
					}
					UtilityFunction.deleteDirectory(new File(outputFolder.getAbsolutePath() + "/" + modelOriginalName));
					UtilityFunction.deleteDirectory(new File(outputFolder.getAbsolutePath() + "/" + mm[0]));
				}

				// Creat solution id - success
			} catch (IOException e) {
				logger.error(EELFLoggerDelegate.errorLogger,"H2O templatization failed", e);
			}
			dockerPreprator.prepareDockerApp(outputFolder);

		} else if (metadata.getRuntimeName().equals("javageneric")) {
			File plugin_root = new File(outputFolder, "plugin_root");
			plugin_root.mkdirs();
			File plugin_src = new File(plugin_root, "src");
			plugin_src.mkdirs();
			File plugin_classes = new File(plugin_root, "classes");
			plugin_classes.mkdirs();

			JavaGenericDockerPreparator dockerPreprator = new JavaGenericDockerPreparator(metadataParser);
			Resource[] resources = this.resourceUtils.loadResources("classpath*:templates/javaGeneric/*");
			for (Resource resource : resources) {
				UtilityFunction.copyFile(resource, new File(outputFolder, resource.getFilename()));
			}

			try {

				UtilityFunction.unzip(localmodelFile, outputFolder.getAbsolutePath());

				String mm[] = modelOriginalName.split("\\.");

				File fd = new File(outputFolder.getAbsolutePath() + "/" + mm[0]);

				File ff[] = fd.listFiles();

				if (ff != null) {
					for (File f : ff) {
						FileUtils.copyFileToDirectory(f, outputFolder);
					}
					UtilityFunction.deleteDirectory(new File(outputFolder.getAbsolutePath() + "/" + modelOriginalName));
					UtilityFunction.deleteDirectory(new File(outputFolder.getAbsolutePath() + "/" + mm[0]));
				}

			} catch (IOException e) {
				logger.error(EELFLoggerDelegate.errorLogger,"Java-Generic templatization failed", e);
			}

			dockerPreprator.prepareDockerApp(outputFolder);

		} else {
			logger.error(EELFLoggerDelegate.errorLogger,"Unspported runtime {}", metadata.getRuntimeName());
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
					"Unspported runtime " + metadata.getRuntimeName());
		}
		logger.debug(EELFLoggerDelegate.debugLogger,"Resource List");
		listFilesAndFilesSubDirectories(outputFolder);
		logger.debug(EELFLoggerDelegate.debugLogger,"End of Resource List");
		logger.debug(EELFLoggerDelegate.debugLogger,"Started docker client");
		DockerClient dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);
		logger.debug(EELFLoggerDelegate.debugLogger,"Docker client created successfully");
		try {
			logger.debug("Docker image creation started");
			String actualModelName = getActualModelName(metadata, solutionID);  
			CreateImageCommand createCMD = new CreateImageCommand(outputFolder, actualModelName,metadata.getVersion(), null, false, true);
			createCMD.setClient(dockerClient);
			createCMD.execute();
			logger.debug(EELFLoggerDelegate.debugLogger,"Docker image creation done");
			// put catch here
			// /Microservice/Docker image nexus creation -success

			// in catch /Microservice/Docker image nexus creation -failure

			// TODO: remove local image

			logger.debug(EELFLoggerDelegate.debugLogger,"Starting docker image tagging");
			String imageTagName = dockerConfiguration.getImagetagPrefix() + File.separator + actualModelName;
			
			String dockerImageURI = imageTagName + ":" + metadata.getVersion();
			
			TagImageCommand tagImageCommand = new TagImageCommand(actualModelName+ ":" + metadata.getVersion(),
					imageTagName, metadata.getVersion(), true, false);
			tagImageCommand.setClient(dockerClient);
			tagImageCommand.execute();
			logger.debug(EELFLoggerDelegate.debugLogger,"Docker image tagging completed successfully");

			logger.debug(EELFLoggerDelegate.debugLogger,"Starting pushing with Imagename:" + imageTagName + " and version : " + metadata.getVersion()
					+ " in nexus");
			PushImageCommand pushImageCmd = new PushImageCommand(imageTagName, metadata.getVersion(), "");
			pushImageCmd.setClient(dockerClient);
			pushImageCmd.execute();

			logger.debug(EELFLoggerDelegate.debugLogger,"Docker image URI : " + dockerImageURI);

			logger.debug(EELFLoggerDelegate.debugLogger,"Docker image pushed in nexus successfully");

			// Microservice/Docker image pushed to nexus -success

			return dockerImageURI;

		} finally {
			try {
				dockerClient.close();
			} catch (IOException e) {
				logger.error(EELFLoggerDelegate.errorLogger,"Fail to close docker client gracefully", e);
			}
		}
	}

	public MLPSolution createSolution(Metadata metadata) throws AcumosServiceException {
		logger.debug(EELFLoggerDelegate.debugLogger,"Create solution call started");
		MLPSolution solution = new MLPSolution();
		solution.setName(metadata.getSolutionName());
		solution.setDescription(metadata.getSolutionName());
		solution.setOwnerId(metadata.getOwnerId());
		// String toolTypeCode = getToolTypeCode(metadata.getToolkit());
		
		logger.debug(EELFLoggerDelegate.debugLogger,"Model name[CreateSolutionMethod] :"+metadata.getSolutionName());

		String toolKit = metadata.getToolkit();

		if (toolKit != null) {
			solution.setToolkitTypeCode(getToolTypeCode(toolKit));
		} else if (dcaeflag) {
			solution.setToolkitTypeCode("ON");
		}

		solution.setAccessTypeCode(AccessTypeCode.PR.name());
		solution.setValidationStatusCode(ValidationStatusCode.IP.name());
		solution.setActive(true);
		try {
			solution = cdmsClient.createSolution(solution);
			metadata.setSolutionId(solution.getSolutionId());
			logger.debug(EELFLoggerDelegate.debugLogger,"Solution created: " + solution.getSolutionId());
			// Creat solution id - success
			// OnboardingNotification notify= new OnboardingNotification();
			// notify.successResponse();

			return solution;

		} catch (HttpStatusCodeException e) {
			// Creat solution id - fail
			// Create Solution failed. Notify
			if (onboardingStatus != null) {
				// notify
				onboardingStatus.notifyOnboardingStatus("CreateMicroservice", "FA", "Create Solution Failed");
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
		revision.setOwnerId(metadata.getOwnerId());

		/******************* Version Management *********************/
		String version = metadata.getVersion();

		if (version == null) {
			version = getModelVersion(metadata.getSolutionId());
			metadata.setVersion(version);
		}

		revision.setVersion(metadata.getVersion());
		revision.setSolutionId(metadata.getSolutionId());
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

	public MLPArtifact addArtifact(Metadata metadata, File file, ArtifactTypeCode typeCode,String actualModelName)
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
			onboardingStatus.notifyOnboardingStatus("AddToRepository", "ST",
					"Add Artifact for" + file.getName() + " Started");
		}
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			int size = fileInputStream.available();
			 UploadArtifactInfo artifactInfo = artifactClient.uploadArtifact(nexusGroupId,actualModelName, metadata.getVersion(), ext, size,fileInputStream);
			 
			logger.debug(EELFLoggerDelegate.debugLogger,
					"Upload Artifact for: {}", file.getName() + " successful response: {}", artifactInfo.getArtifactId());
			try {
				logger.debug(EELFLoggerDelegate.debugLogger,"Add Artifact called for " + file.getName());
				MLPArtifact modelArtifact = new MLPArtifact();
				modelArtifact.setName(file.getName());
				modelArtifact.setDescription(file.getName());
				modelArtifact.setVersion(metadata.getVersion());
				modelArtifact.setArtifactTypeCode(typeCode.name());
				modelArtifact.setOwnerId(metadata.getOwnerId());
				modelArtifact.setUri(artifactInfo.getArtifactMvnPath());
				modelArtifact.setSize(size);
				modelArtifact = cdmsClient.createArtifact(modelArtifact);
				logger.debug(EELFLoggerDelegate.debugLogger,"Artifact created for " + file.getName());
				logger.debug(EELFLoggerDelegate.debugLogger,"addSolutionRevisionArtifact for " + file.getName() + " called");
				try {
					cdmsClient.addSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
							modelArtifact.getArtifactId());
					logger.debug(EELFLoggerDelegate.debugLogger, "addSolutionRevisionArtifact for " + file.getName() + " successful");
					//logger.info("addSolutionRevisionArtifact for " + file.getName() + " successful");
					// Notify add artifacts successful
					if (onboardingStatus != null) {
						onboardingStatus.setArtifactId(modelArtifact.getArtifactId());
						onboardingStatus.notifyOnboardingStatus("AddToRepository", "SU",
								"Add Artifact for" + file.getName() + " Succesful");
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
						"Fail to create artificate for " + file.getName() + " - " + e.getResponseBodyAsString(), e);
			}
		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger,"Error: {}", e);
			throw e;
		} catch (Exception e) {
			// Notify add artifacts failed
			if (onboardingStatus != null) {
				onboardingStatus.notifyOnboardingStatus("AddToRepository", "FA",
						"Add Artifact for" + file.getName() + " Failed");
			}
			logger.error(EELFLoggerDelegate.errorLogger, "Fail to upload artificate for {}", file.getName() + " - {}", e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificate for " + file.getName() + " - " + e.getMessage(), e);
		}

	}

	public MLPArtifact addArtifact(Metadata metadata, String uri, ArtifactTypeCode typeCode)
			throws AcumosServiceException {

		try {
			try {
				logger.debug(EELFLoggerDelegate.debugLogger,"Upload Artifact for " + uri + " started");
				// Notify add artifacts started
				if (onboardingStatus != null) {
					onboardingStatus.notifyOnboardingStatus("AddToRepository", "ST",
							"Add Artifact for" + uri + " Started");
				}
				MLPArtifact modelArtifact = new MLPArtifact();
				modelArtifact.setName(metadata.getModelName());
				modelArtifact.setDescription(uri);
				modelArtifact.setVersion(metadata.getVersion());
				modelArtifact.setArtifactTypeCode(typeCode.name());
				modelArtifact.setOwnerId(metadata.getOwnerId());
				modelArtifact.setUri(uri);
				modelArtifact.setSize(uri.length());
				modelArtifact = cdmsClient.createArtifact(modelArtifact);
				try {
					cdmsClient.addSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
							modelArtifact.getArtifactId());
					logger.debug(EELFLoggerDelegate.debugLogger,"addSolutionRevisionArtifact for " + uri + " successful");
					if (onboardingStatus != null) {
						onboardingStatus.setArtifactId(modelArtifact.getArtifactId());
						onboardingStatus.notifyOnboardingStatus("AddToRepository","SU", "Upload Artifact for" + modelArtifact.getName() +" Successful");
					}
					return modelArtifact;

				} catch (HttpStatusCodeException e) {
					
					logger.error(EELFLoggerDelegate.errorLogger,"Fail to call addSolutionRevisionArtifact for {}", e.getResponseBodyAsString(), e);
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
							"Fail to call addSolutionRevisionArtifact for " + e.getResponseBodyAsString(), e);
				}
			} catch (HttpStatusCodeException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Fail to create artificate for {}", e.getResponseBodyAsString(), e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
						"Fail to create artificate for " + e.getResponseBodyAsString(), e);
			}
		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Error: {}", e);
			throw e;
		} catch (Exception e) {
			// Notify model artifact upload failed
			if (onboardingStatus != null) {
				onboardingStatus.notifyOnboardingStatus("AddToRepository", "FA", "Upload Artifact for" + uri + "Failed");
			}
			logger.error(EELFLoggerDelegate.errorLogger, "Fail to upload artificate for {}", e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificate for " + e.getMessage(), e);
		}

	}

	public void generateTOSCA(File localProtobufFile, File localMetadataFile, Metadata metadata) {
		logger.debug(EELFLoggerDelegate.debugLogger,"Generate TOSCA started");
		try {

			// TODO : Include toscaOutputFolder =/tmp/ and
			// in external configuration SPRING_APPLICATION_JSON
			// And define the variable for the same in the class.
			logger.debug(EELFLoggerDelegate.debugLogger,"toscaOutputFolder : " + toscaOutputFolder);
			logger.debug(EELFLoggerDelegate.debugLogger,"toscaGeneratorEndPointURL : {}", toscaGeneratorEndPointURL);
			logger.debug(EELFLoggerDelegate.debugLogger,"nexusEndPointURL : " + nexusEndPointURL);
			logger.debug(EELFLoggerDelegate.debugLogger,"nexusUserName : " + nexusUserName);
			logger.debug(EELFLoggerDelegate.debugLogger,"nexusPassword : " + nexusPassword);
			logger.debug(EELFLoggerDelegate.debugLogger,"nexusGroupId : " + nexusGroupId);
			logger.debug(EELFLoggerDelegate.debugLogger,"cmnDataSvcEndPoinURL : " + cmnDataSvcEndPoinURL);
			logger.debug(EELFLoggerDelegate.debugLogger,"cmnDataSvcUser : " + cmnDataSvcUser);
			logger.debug(EELFLoggerDelegate.debugLogger,"cmnDataSvcPwd : " + cmnDataSvcPwd);

			ToscaGeneratorClient client = new ToscaGeneratorClient(toscaOutputFolder, toscaGeneratorEndPointURL,
					nexusEndPointURL, nexusUserName, nexusPassword, nexusGroupId, cmnDataSvcEndPoinURL, cmnDataSvcUser,
					cmnDataSvcPwd);

			String result = client.generateTOSCA(metadata.getOwnerId(), metadata.getSolutionId(), metadata.getVersion(),
					metadata.getRevisionId(), localProtobufFile, localMetadataFile);
			logger.debug(EELFLoggerDelegate.debugLogger,"Generate TOSCA completed result:" + result);

		} catch (Exception e) {
			// Notify TOSCA generation failed
			if (onboardingStatus != null) {
				onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "FA", "TOSCA Generation Failed");
			}
			logger.error(EELFLoggerDelegate.errorLogger,"Fail to generate TOSCA for solution - {}", e);
			// Storage of artifact location references in Common Data
			// Store-failure
		}
	}

	public void revertbackOnboarding(Metadata metadata, String imageUri,String solutionID) throws AcumosServiceException {

		try {

			logger.debug(EELFLoggerDelegate.debugLogger,"In RevertbackOnboarding method");

			RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("1");
			repositoryLocation.setUrl(nexusEndPointURL);
			repositoryLocation.setUsername(nexusUserName);
			repositoryLocation.setPassword(nexusPassword);
			NexusArtifactClient nexusClient = new NexusArtifactClient(repositoryLocation);
			DockerClient dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);

			// Remove the image from docker registry
			// Check the value of imageUri, if it is null then do not delete the
			// image
			logger.debug(EELFLoggerDelegate.debugLogger,"Image Name from dockerize file method: " + imageUri);

			if (StringUtils.isNotBlank(imageUri)) {
				String imageTagName = dockerConfiguration.getImagetagPrefix() + "/" + getActualModelName(metadata, solutionID);
				
				logger.debug(EELFLoggerDelegate.debugLogger,"Image Name: " + imageTagName);
				DeleteImageCommand deleteImageCommand = new DeleteImageCommand(imageTagName, metadata.getVersion(), "");
				deleteImageCommand.setClient(dockerClient);
				deleteImageCommand.execute();
				logger.debug(EELFLoggerDelegate.debugLogger,"Successfully Deleted the image from Docker Registry");
			}

			if (metadata.getSolutionId() != null) {
				logger.debug(EELFLoggerDelegate.debugLogger,"Solution id: " + metadata.getSolutionId() + "  Revision id: " + metadata.getRevisionId());

				// get the Artifact IDs for given solution
				List<MLPArtifact> artifactids = cdmsClient.getSolutionRevisionArtifacts(metadata.getSolutionId(),
						metadata.getRevisionId());

				// check if artifactids is empty
				// Delete all the artifacts for given solution

				for (MLPArtifact mlpArtifact : artifactids) {
					String artifactId = mlpArtifact.getArtifactId();

					// Delete SolutionRevisionArtifact
					logger.debug(EELFLoggerDelegate.debugLogger,"Deleting Artifact: " + artifactId);
					cdmsClient.dropSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
							artifactId);
					logger.debug(EELFLoggerDelegate.debugLogger,"Successfully Deleted the SolutionRevisionArtifact");

					// Delete Artifact
					cdmsClient.deleteArtifact(artifactId);
					logger.debug(EELFLoggerDelegate.debugLogger,"Successfully Deleted the Artifact");

					// Delete the file from the Nexus
					if (!(mlpArtifact.getArtifactTypeCode().equals("DI"))) {
						nexusClient.deleteArtifact(mlpArtifact.getUri());
						logger.debug(EELFLoggerDelegate.debugLogger,"Successfully Deleted the Artifact from Nexus");
					}
				}

				// Delete current revision
				cdmsClient.deleteSolutionRevision(metadata.getSolutionId(), metadata.getRevisionId());
				logger.debug(EELFLoggerDelegate.debugLogger,"Successfully Deleted the Solution Revision");

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
				System.out.println(file.getPath());
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