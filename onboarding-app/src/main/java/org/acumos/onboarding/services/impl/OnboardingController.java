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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

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
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.JsonRequest;
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
import org.acumos.onboarding.services.DockerService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import com.github.dockerjava.api.DockerClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/v2")
@Api(value = "Operation to to onboard a ML model", tags = "Onboarding Service APIs")
/**
 * 
 * @author *****
 *
 */
public class OnboardingController implements DockerService {
	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(OnboardingController.class);

	@Value("${nexus.nexusEndPointURL}")
	private String nexusEndPointURL;

	@Value("${nexus.nexusUserName}")
	private String nexusUserName;

	@Value("${nexus.nexusPassword}")
	private String nexusPassword;

	@Value("${nexus.nexusGroupId}")
	private String nexusGroupId;

	@Value("${cmndatasvc.cmnDataSvcEndPoinURL}")
	private String cmnDataSvcEndPoinURL;

	@Value("${cmndatasvc.cmnDataSvcUser}")
	private String cmnDataSvcUser;

	@Value("${cmndatasvc.cmnDataSvcPwd}")
	private String cmnDataSvcPwd;

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
	private String portalURL;

	String modelOriginalName;

	String dockerImageURI;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private DockerConfiguration dockerConfiguration;

	private CommonDataServiceRestClientImpl cdmsClient;

	private PortalRestClientImpl portalClient;

	private ResourceUtils resourceUtils;

	public OnboardingController() {
		// Property values are injected after the constructor finishes
	}

	/**
	 * @throws AcumosServiceException
	 */
	@PostConstruct
	public void init() throws AcumosServiceException {
		logger.info("Creating docker service instance");
		this.cdmsClient = new CommonDataServiceRestClientImpl(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd);
		this.portalClient = new PortalRestClientImpl(portalURL);
		this.resourceUtils = new ResourceUtils(resourceLoader);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Check User authentication and returns JWT token", response = ServiceResponse.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Something bad happened", response = ServiceResponse.class),
			@ApiResponse(code = 400, message = "Invalid request", response = ServiceResponse.class) })
	@RequestMapping(value = "/auth", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<ServiceResponse> OnboardingWithAuthentication(@RequestBody JsonRequest<Crediantials> cred,
			HttpServletResponse response) throws AcumosServiceException {
		logger.info("Started User Authentication");
		try {
			Crediantials obj = cred.getBody();

			String user = obj.getUsername();
			String pass = obj.getPassword();

			JSONObject crediantials = new JSONObject();
			crediantials.put("username", user);
			crediantials.put("password", pass);

			JSONObject reqObj = new JSONObject();
			reqObj.put("request_body", crediantials);

			String token = portalClient.loginToAcumos(reqObj);

			if (token != null) {
				// Setting JWT token in header
				response.setHeader("jwtToken", token);
				logger.info("User Authentication Succesful");
				return new ResponseEntity<ServiceResponse>(ServiceResponse.successJWTResponse(token), HttpStatus.OK);
			} else {
				logger.info("Either Username/Password is invalid.");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,"Either Username/Password is invalid.");
			}

		} catch (AcumosServiceException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		}
	}

	/************************************************
	 * End of Authentication
	 *****************************************************/

	@Override
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Upload model file and its meta data as string to dockerize", response = ServiceResponse.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Something bad happened", response = ServiceResponse.class),
			@ApiResponse(code = 400, message = "Invalid request", response = ServiceResponse.class) })
	@RequestMapping(value = "/models", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<ServiceResponse> dockerizePayload(HttpServletRequest request,
			@RequestPart(required = true) MultipartFile model, @RequestPart(required = true) MultipartFile metadata,
			@RequestPart(required = true) MultipartFile schema,@RequestHeader(value="Authorization", required= false) String authorization, @RequestHeader(value="provider", required= false) String provider) throws AcumosServiceException {
		
		logger.info("Started JWT token validation");

		try 
		{
			//'authorization' represents JWT token here...!
			if (authorization == null) {				
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,	"Token Not Available...!");
			}

			// Call to validate JWT Token.....!
			JsonResponse<Object> valid = validate(authorization, provider);

			boolean isValidToken = valid.getStatus();

			String ownerId;
			String imageUri = null;

			if (isValidToken) {
				logger.info("Token validation successful");
				ownerId = valid.getResponseBody().toString();

				if (ownerId == null)
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND, "Either  username/password is invalid.");

				logger.info("Dockerization request recieved with " + model.getOriginalFilename() + " and metadata :"
						+ metadata);

				modelOriginalName = model.getOriginalFilename();
				String modelId = UtilityFunction.getGUID();
				File outputFolder = new File("tmp", modelId);
				outputFolder.mkdirs();
				MetadataParser metadataParser = null;
				boolean isSuccess = false;
				
				try {
					File localmodelFile = new File(outputFolder, model.getOriginalFilename());
					try {
						UtilityFunction.copyFile(model.getInputStream(), localmodelFile);
					} catch (IOException e) {						
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
								"Fail to download model file " + localmodelFile.getName());
					}
					File localMetadataFile = new File(outputFolder, metadata.getOriginalFilename());
					try {
						UtilityFunction.copyFile(metadata.getInputStream(), localMetadataFile);
					} catch (IOException e) {						
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
								"Fail to download metadata file " + localMetadataFile.getName());
					}
					File localProtobufFile = new File(outputFolder, schema.getOriginalFilename());
					try {
						UtilityFunction.copyFile(schema.getInputStream(), localProtobufFile);
					} catch (IOException e) {						
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
								"Fail to download protobuf file " + localProtobufFile.getName());
					}

					metadataParser = new MetadataParser(localMetadataFile);
					Metadata mData = metadataParser.getMetadata();
					mData.setOwnerId(ownerId);

					MLPSolution mlpSolution = null;

					List<MLPSolution> solList = getExistingSolution(mData);

					boolean isListEmpty = solList.isEmpty();

					if (isListEmpty) {
						mlpSolution = createSolution(mData);
					} else {
						mlpSolution = solList.get(0);
						mData.setSolutionId(mlpSolution.getSolutionId());
					}

					createSolutionRevision(mData);
					
					imageUri = dockerizeFile(metadataParser, localmodelFile);

					addArtifact(mData, imageUri, ArtifactTypeCode.DI);

					addArtifact(mData, localmodelFile, ArtifactTypeCode.MI);

					addArtifact(mData, localProtobufFile, ArtifactTypeCode.MI);

					addArtifact(mData, localMetadataFile, ArtifactTypeCode.MD);

					generateTOSCA(localProtobufFile, localMetadataFile, mData);	
					
					isSuccess = true;

					return new ResponseEntity<ServiceResponse>(ServiceResponse.successResponse(mlpSolution),
							HttpStatus.CREATED);
				} finally {
					if (isSuccess == false) {
						logger.info("Onboarding Failed, Reverting failed solutions and artifacts.");
						revertbackOnboarding(metadataParser.getMetadata(), imageUri);
					}
					UtilityFunction.deleteDirectory(outputFolder);
				}
			} else {
				try {
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_TOKEN,
							"Either Username/Password is invalid.");
				} catch (AcumosServiceException e) {
					return new ResponseEntity<ServiceResponse>(
							ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()), HttpStatus.UNAUTHORIZED);
				}
			}

		} catch (AcumosServiceException e) {			
			HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;	
			logger.error(e.getErrorCode() +"  "+ e.getMessage());
			return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()),	httpCode);
		} 
		catch (HttpClientErrorException e) 
		{
			//Handling #401				
			if(HttpStatus.UNAUTHORIZED == e.getStatusCode())
			{
				logger.info("Unauthorized User - Either Username/Password is invalid.");
				return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(""+e.getStatusCode(),"Unauthorized User"), HttpStatus.UNAUTHORIZED);
			}
			else
			{
				logger.error(e.getMessage());
				return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(""+e.getStatusCode(),e.getMessage()),e.getStatusCode());
			}			
		} 
		catch (Exception e) 
		{
			logger.error(e.getMessage());
			if(e instanceof AcumosServiceException)
			{
				return new ResponseEntity<ServiceResponse>(ServiceResponse
						.errorResponse(((AcumosServiceException) e).getErrorCode(), e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);				
			}
			else
			{						
				return new ResponseEntity<ServiceResponse>(ServiceResponse
						.errorResponse(AcumosServiceException.ErrorCode.UNKNOWN.name(), e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);			
			}
		}

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
	private List<MLPSolution> getExistingSolution(Metadata metadata) {

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

	/*
	 * @Method Name : dockerizeFile Performs complete dockerization process.
	 */
	public String dockerizeFile(MetadataParser metadataParser, File localmodelFile) throws AcumosServiceException {
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
				UtilityFunction.unzip(localmodelFile, outputFolder.getAbsolutePath());
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

/*				File mD = new File(outputFolder.getAbsolutePath() + "/" + modelOriginalName);

				if (mD.exists()) {
					UtilityFunction.deleteDirectory(mD);
				}*/

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

/*				File mD = new File(outputFolder.getAbsolutePath() + "/" + modelOriginalName);

				if (mD.exists()) {
					UtilityFunction.deleteDirectory(mD);
				}*/

			} catch (IOException e) {
				logger.warn("Java-Generic templatization failed", e);
			}

			dockerPreprator.prepareDockerApp(outputFolder);

		} else {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
					"Unspported runtime " + metadata.getRuntimeName());
		}
		logger.info("********* Resource List ***********");
        listFilesAndFilesSubDirectories(outputFolder);
        logger.info("********* End of Resource List ***********");    
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


	public MLPSolution createSolution(Metadata metadata) throws AcumosServiceException {
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

	protected MLPSolutionRevision createSolutionRevision(Metadata metadata) throws AcumosServiceException {
		logger.info("Create solution revision call started");
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
			logger.info("Solution revision created: " + revision.getRevisionId());
			return revision;
		} catch (HttpStatusCodeException e) {			
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

	protected MLPArtifact addArtifact(Metadata metadata, File file, ArtifactTypeCode typeCode)
			throws AcumosServiceException {
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

	private void generateTOSCA(File localProtobufFile, File localMetadataFile, Metadata metadata) {
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
	
	public void revertbackOnboarding(Metadata metadata, String imageUri) throws AcumosServiceException {

		try {

			logger.info("In RevertbackOnboarding method");
			
			RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("1");
			repositoryLocation.setUrl(nexusEndPointURL);
			repositoryLocation.setUsername(nexusUserName);
			repositoryLocation.setPassword(nexusPassword);
			NexusArtifactClient nexusClient = new NexusArtifactClient(repositoryLocation);
			DockerClient dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);
			
			// Remove the image from docker registry
			// Check the value of imageUri, if it is null then do not delete the image
			logger.info("Image Name from dockerize file method: " + imageUri);
			
			if (StringUtils.isNotBlank(imageUri)) {
				String imageTagName = dockerConfiguration.getImagetagPrefix() + "/" + metadata.getModelName();
				logger.info("Image Name: " + imageTagName);
				DeleteImageCommand deleteImageCommand = new DeleteImageCommand(imageTagName, metadata.getVersion(), "");
				deleteImageCommand.setClient(dockerClient);
				deleteImageCommand.execute();
				logger.info("--- Successfully Deleted the image from Docker Registry ---");
			}

			if (metadata.getSolutionId() != null) {
				logger.info("Solution id: " + metadata.getSolutionId() + "  Revision id: " + metadata.getRevisionId());

				// get the Artifact IDs for given solution
				List<MLPArtifact> artifactids = cdmsClient.getSolutionRevisionArtifacts(metadata.getSolutionId(),
						metadata.getRevisionId());

				// check if artifactids is empty
				// Delete all the artifacts for given solution

				for (MLPArtifact mlpArtifact : artifactids) {
					String artifactId = mlpArtifact.getArtifactId();

					// Delete SolutionRevisionArtifact
					logger.info("Deleting Artifact: " + artifactId);
					cdmsClient.dropSolutionRevisionArtifact(metadata.getSolutionId(), metadata.getRevisionId(),
							artifactId);
					logger.info("--- Successfully Deleted the SolutionRevisionArtifact ---");

					// Delete Artifact
					cdmsClient.deleteArtifact(artifactId);
					logger.info("--- Successfully Deleted the Artifact ---");

					// Delete the file from the Nexus
					if (!(mlpArtifact.getArtifactTypeCode().equals("DI"))) {
						nexusClient.deleteArtifact(mlpArtifact.getUri());
						logger.info("--- Successfully Deleted the Artifact from Nexus ---");
					}
				}
				
				// Delete current revision
				cdmsClient.deleteSolutionRevision(metadata.getSolutionId(), metadata.getRevisionId());
				logger.info("--- Successfully Deleted the Solution Revision ---");
				
				// get other revision under the solution, if they exist
				List<MLPSolutionRevision> solRev = cdmsClient.getSolutionRevisions(metadata.getSolutionId());

				// Delete the solution ID if no other revision is associated
				// with it
				if (solRev.isEmpty()) {
					cdmsClient.deleteSolution(metadata.getSolutionId());
					logger.info("Deleting Solution: " + metadata.getSolutionId());
				}
								
			}
		} catch (Exception e) {
			logger.info("Onboarding failed");
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to revert back onboarding changes : " + e.getMessage());
		}
	}
	public void listFilesAndFilesSubDirectories(File directory)
    {

           File[] fList = directory.listFiles();

           for (File file : fList)
           {
                  if (file.isFile())
                  {
                        System.out.println(file.getPath());
                  }
                  else if (file.isDirectory())
                  {
                        listFilesAndFilesSubDirectories(file);
                  }
           }
    }

}
