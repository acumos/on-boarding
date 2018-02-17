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
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.common.utils.ResourceUtils;
import org.acumos.onboarding.common.utils.UtilityFunction;
import org.acumos.onboarding.component.docker.DockerClientFactory;
import org.acumos.onboarding.component.docker.DockerConfiguration;
import org.acumos.onboarding.component.docker.cmd.CreateImageCommand;
import org.acumos.onboarding.component.docker.cmd.PushImageCommand;
import org.acumos.onboarding.component.docker.cmd.TagImageCommand;
import org.acumos.onboarding.component.docker.preparation.H2ODockerPreparator;
import org.acumos.onboarding.component.docker.preparation.JavaGenericDockerPreparator;
import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.acumos.onboarding.component.docker.preparation.PythonDockerPreprator;
import org.acumos.onboarding.component.docker.preparation.RDockerPreparator;
import org.apache.commons.io.FileUtils;

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

	protected String dockerImageURI = null;
	
	
	@Autowired
	protected ResourceLoader resourceLoader;

	@Autowired
	protected DockerConfiguration dockerConfiguration;


	protected CommonDataServiceRestClientImpl cdmsClient;

	protected PortalRestClientImpl portalClient;

	protected ResourceUtils resourceUtils;
	
	@PostConstruct
	public void init() throws AcumosServiceException {
		
		this.cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd);
		this.portalClient = new PortalRestClientImpl(portalURL);
		this.resourceUtils = new ResourceUtils(resourceLoader);
	}
	
	
	@SuppressWarnings("unchecked")
	protected JsonResponse<Object> validate(String jwtToken, String provider) throws AcumosServiceException {

		JSONObject obj1 = new JSONObject();
		obj1.put("jwtToken", jwtToken);

		JSONObject obj2 = new JSONObject();
		obj2.put("request_body", obj1);

		JsonResponse<Object> valid = portalClient.tokenValidation(obj2, provider);

		return valid;
	}
	
	
	public String dockerizeFile(MetadataParser metadataParser, File localmodelFile) throws AcumosServiceException 
	{
		File outputFolder = localmodelFile.getParentFile();
		Metadata metadata = metadataParser.getMetadata();
		logger.info("Preparing app in " + outputFolder);
		if (metadata.getRuntimeName().equals("python")) {
			outputFolder = new File(localmodelFile.getParentFile(), "app");
			outputFolder.mkdir();

			PythonDockerPreprator dockerPreprator = new PythonDockerPreprator(metadataParser, extraIndexURL,
					trustedHost);

			Resource[] resources = this.resourceUtils.loadResources("classpath*:templates/python/*");
			for (Resource resource : resources) {
				UtilityFunction.copyFile(resource, new File(outputFolder, resource.getFilename()));
			}
			try {
				File modelFolder = new File(outputFolder, "model");
				UtilityFunction.unzip(localmodelFile, modelFolder.getAbsolutePath());
			} catch (IOException e) {
				logger.warn("Python templatization failed", e);
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
				logger.warn("Java Argus templatization failed", e);
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

				File mD = new File(outputFolder.getAbsolutePath() + "/" + modelOriginalName);

				if (mD.exists()) {
					UtilityFunction.deleteDirectory(mD);
				}

			} catch (IOException e) {
				logger.warn("H2O templatization failed", e);			
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

				File mD = new File(outputFolder.getAbsolutePath() + "/" + modelOriginalName);

				if (mD.exists()) {
					UtilityFunction.deleteDirectory(mD);
				}

			} catch (IOException e) {
				logger.warn("Java-Generic templatization failed", e);
			}

			dockerPreprator.prepareDockerApp(outputFolder);

		} else {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
					"Unspported runtime " + metadata.getRuntimeName());
		}
		
		logger.info("Started docker client");
		DockerClient dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);
		logger.info("Docker client created successfully");
		try {
			logger.info("Docker image creation started");
			CreateImageCommand createCMD = new CreateImageCommand(outputFolder, metadata.getModelName(),
					metadata.getVersion(), null, false, true);
			createCMD.setClient(dockerClient);
			createCMD.execute();
			logger.info("Docker image creation done");
			String imageId = createCMD.getImageId();
			// TODO: remove local image

			logger.info("Starting docker image tagging");
			String imageTagName = dockerConfiguration.getImagetagPrefix() + "/" + metadata.getModelName();

			TagImageCommand tagImageCommand = new TagImageCommand(metadata.getModelName() + ":" + metadata.getVersion(),
					imageTagName, metadata.getVersion(), true, false);
			tagImageCommand.setClient(dockerClient);
			tagImageCommand.execute();
			logger.info("Docker image tagging completed successfully");

			logger.info("Starting pushing with Imagename:" + imageTagName + " and version : " + metadata.getVersion()
					+ " in nexus");
			PushImageCommand pushImageCmd = new PushImageCommand(imageTagName, metadata.getVersion(), "");
			pushImageCmd.setClient(dockerClient);
			pushImageCmd.execute();

			dockerImageURI = imageTagName + ":" + metadata.getVersion();

			logger.info("Docker image URI : " + dockerImageURI);

			logger.info("Docker image pushed in nexus successfully");

			return dockerImageURI;

		} finally {
			try {
				dockerClient.close();
			} catch (IOException e) {
				logger.warn("Fail to close docker client gracefully", e);
			}
		}
	}
	
	
	protected MLPArtifact addArtifact(Metadata metadata, File file, ArtifactTypeCode typeCode) throws AcumosServiceException {
		String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(nexusEndPointURL);
		repositoryLocation.setUsername(nexusUserName);
		repositoryLocation.setPassword(nexusPassword);
		NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
		logger.info("Upload Artifact for " + file.getName() + " started");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			int size = fileInputStream.available();
			UploadArtifactInfo artifactInfo = artifactClient.uploadArtifact(nexusGroupId, metadata.getModelName(),
					metadata.getVersion(), ext, size, fileInputStream);
			logger.info(
					"Upload Artifact for " + file.getName() + " successful response: " + artifactInfo.getArtifactId());
			try {
				logger.info("Add Artifact called for " + file.getName());
				MLPArtifact modelArtifact = new MLPArtifact();
				modelArtifact.setName(file.getName());
				modelArtifact.setDescription(file.getName());
				modelArtifact.setVersion(metadata.getVersion());
				modelArtifact.setArtifactTypeCode(typeCode.name());
				modelArtifact.setOwnerId(metadata.getOwnerId());
				modelArtifact.setUri(artifactInfo.getArtifactMvnPath());
				modelArtifact.setSize(size);
				modelArtifact = cdmsClient.createArtifact(modelArtifact);
				logger.info("Artifact created for " + file.getName());
				logger.info("addSolutionRevisionArtifact for " + file.getName() + " called");
				try {
					cdmsClient.addSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
							modelArtifact.getArtifactId());
					logger.info("addSolutionRevisionArtifact for " + file.getName() + " successful");
					return modelArtifact;
				} catch (HttpStatusCodeException e) {					
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
							"Fail to call addSolutionRevisionArtifact for " + file.getName() + " - "
									+ e.getResponseBodyAsString(),
							e);
				}
			} catch (HttpStatusCodeException e) {				
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
						"Fail to create artificate for " + file.getName() + " - " + e.getResponseBodyAsString(), e);
			}
		} catch (AcumosServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificate for " + file.getName() + " - " + e.getMessage(), e);
		}

	}
	
	
	
	/*
	 * @Method Name : getExistingSolution Gives existing solution against
	 * ownerId and Model name if any. *
	 */
	protected List<MLPSolution> getExistingSolution(Metadata metadata) {

		String ownerId = metadata.getOwnerId();
		String modelName = metadata.getModelName();

		Map<String, Object> queryParameters = new HashMap<String, Object>();

		queryParameters.put("ownerId", ownerId);
		queryParameters.put("name", modelName);
      
		/*this below commneted logic is for CDS 1.10.0 */
		
	/*	int page = 0;
		int size = 9;
		RestPageRequest pageRequest = new RestPageRequest(page, size);

		SearchCriteria onboardSearchCriteria = new SearchCriteria(
				new SearchCriterion("ownerId", SearchOperation.EQUALS, metadata.getOwnerId()))
						.and(new SearchCriterion("name", SearchOperation.EQUALS, metadata.getModelName()));
		RestPageResponse<MLPSolution> onboardMatches = cdmsClient.searchSolutions(onboardSearchCriteria, new RestPageRequest(0, 9));
		
		List<MLPSolution>  list = onboardMatches.getContent();*/
		
		/* TRUE - OR , FALSE - AND */
		List<MLPSolution> list = cdmsClient.searchSolutions(queryParameters, false);

		return list;

	}
	
	
	protected MLPSolution createSolution(Metadata metadata) throws AcumosServiceException {
		logger.info("Create solution call started");
		MLPSolution solution = new MLPSolution();
		solution.setName(metadata.getSolutionName());
		solution.setDescription(metadata.getSolutionName());
		solution.setOwnerId(metadata.getOwnerId());
		// String toolTypeCode = getToolTypeCode(metadata.getToolkit());

		String toolKit = metadata.getToolkit();

		if (toolKit != null)
			solution.setToolkitTypeCode(getToolTypeCode(toolKit));

		solution.setAccessTypeCode(AccessTypeCode.PR.name());
		solution.setValidationStatusCode(ValidationStatusCode.IP.name());
		solution.setActive(true);
		try {
			solution = cdmsClient.createSolution(solution);
			metadata.setSolutionId(solution.getSolutionId());
			logger.info("Solution created: " + solution.getSolutionId());
			return solution;
		} catch (HttpStatusCodeException e) {			
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Creation of solution failed - " + e.getResponseBodyAsString(), e);
		}
	}

	private String getToolTypeCode(String toolkit) {
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

	protected MLPSolutionRevision createSolutionRevision(Metadata metadata) throws AcumosServiceException {
		logger.info("Create solution revision call started");
		MLPSolutionRevision revision = new MLPSolutionRevision();
		revision.setOwnerId(metadata.getOwnerId());

		//******************* Version Management *********************//*
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
			logger.info("Solution revision created: " + revision.getRevisionId());
			return revision;
		} catch (HttpStatusCodeException e) {			
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Creation of solution revision failed - " + e.getResponseBodyAsString(), e);
		}
		/*metadata.setVersion("1.0.0");
		return null;*/
	}

	protected String getModelVersion(String solutionId) {
		int count = 0;
		List<MLPSolutionRevision> revList = cdmsClient.getSolutionRevisions(solutionId);

		if (revList != null)
			count = revList.size();

		count++;

		return "" + count;
	}
	
	
	protected MLPArtifact addArtifact(Metadata metadata, String uri, ArtifactTypeCode typeCode)
			throws AcumosServiceException {

		try {
			try {
				logger.info("Upload Artifact for " + uri + " started");
				MLPArtifact modelArtifact = new MLPArtifact();
				modelArtifact.setName(uri);
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
					logger.info("addSolutionRevisionArtifact for " + uri + " successful");
					return modelArtifact;

				} catch (HttpStatusCodeException e) {					
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
							"Fail to call addSolutionRevisionArtifact for " + e.getResponseBodyAsString(), e);
				}
			} catch (HttpStatusCodeException e) {				
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
						"Fail to create artificate for " + e.getResponseBodyAsString(), e);
			}
		} catch (AcumosServiceException e) {
			throw e;
		} catch (Exception e) {			
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to upload artificate for " + e.getMessage(), e);
		}

	}
	

	
	protected void generateTOSCA(File localProtobufFile, File localMetadataFile, Metadata metadata) {
		logger.info("Generate TOSCA started");
		try {

			// TODO : Include toscaOutputFolder =/tmp/ and
			// toscaGeneratorEndPointURL=http://cognita-demo-core:8080/model_create
			// in external configuration SPRING_APPLICATION_JSON
			// And define the variable for the same in the class.
			logger.debug("******************************");
			logger.debug("toscaOutputFolder : " + toscaOutputFolder);
			logger.debug("toscaGeneratorEndPointURL : " + toscaGeneratorEndPointURL);
			logger.debug("nexusEndPointURL : " + nexusEndPointURL);
			logger.debug("nexusUserName : " + nexusUserName);
			logger.debug("nexusPassword : " + nexusPassword);
			logger.debug("nexusGroupId : " + nexusGroupId);
			logger.debug("cmnDataSvcEndPoinURL : " + cmnDataSvcEndPoinURL);
			logger.debug("cmnDataSvcUser : " + cmnDataSvcUser);
			logger.debug("cmnDataSvcPwd : " + cmnDataSvcPwd);

			ToscaGeneratorClient client = new ToscaGeneratorClient(toscaOutputFolder, toscaGeneratorEndPointURL,
					nexusEndPointURL, nexusUserName, nexusPassword, nexusGroupId, cmnDataSvcEndPoinURL, cmnDataSvcUser,
					cmnDataSvcPwd);

			String result = client.generateTOSCA(metadata.getOwnerId(), metadata.getSolutionId(), metadata.getVersion(),
					metadata.getRevisionId(), localProtobufFile, localMetadataFile);
			logger.info("Generate TOSCA completed result:" + result);

		} catch (Exception e) {
			logger.warn("Fail to generate TOSCA for solution - " + e.getMessage(), e);
		}
	}


}
