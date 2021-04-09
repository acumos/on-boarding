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
import java.net.ConnectException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.AuthorTransport;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.JsonRequest;
import org.acumos.onboarding.common.utils.LogBean;
import org.acumos.onboarding.common.utils.LogThreadLocal;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.common.utils.OnboardingConstants;
import org.acumos.onboarding.common.utils.UtilityFunction;
import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.acumos.onboarding.logging.OnboardingLogConstants;
import org.acumos.onboarding.services.DockerService;
import org.acumos.securityverification.domain.Workflow;
import org.acumos.securityverification.utils.SVConstants;
import org.apache.http.conn.HttpHostConnectException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

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
public class OnboardingController extends CommonOnboarding implements DockerService {
	private static Logger log = LoggerFactory.getLogger(OnboardingController.class);
	LoggerDelegate logger = new LoggerDelegate(log);
	Map<String, String> artifactsDetails = new HashMap<>();
	public static String lOG_DIR_LOC = "/maven/logs/on-boarding/applog";

	@Autowired
	private Environment env;

	public OnboardingController() {
		// Property values are injected after the constructor finishes
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
		logger.debug( "Started User Authentication");
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
				logger.debug( "User Authentication Successful");
				return new ResponseEntity<ServiceResponse>(ServiceResponse.successJWTResponse(token), HttpStatus.OK);
			} else {
				logger.debug( "Either Username/Password is invalid.");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
						"Either Username/Password is invalid.");
			}

		} catch (AcumosServiceException e) {
			logger.error( e.getMessage(), e);
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
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created", response = ServiceResponse.class),
			@ApiResponse(code = 500, message = "Something bad happened", response = ServiceResponse.class),
			@ApiResponse(code = 400, message = "Invalid request", response = ServiceResponse.class),
			@ApiResponse(code = 401, message = "Unauthorized User", response = ServiceResponse.class) })
	@RequestMapping(value = "/models", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<ServiceResponse> onboardModel(HttpServletRequest request,
			@RequestPart(required = true) MultipartFile model, @RequestPart(required = true) MultipartFile metadata,
			@RequestPart(required = true) MultipartFile schema,
			@RequestPart(required = false) MultipartFile license,
			@RequestPart(required = false) MultipartFile source,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "isCreateMicroservice", required = false, defaultValue = "true") boolean isCreateMicroservice,
			@RequestHeader(value = "deploy", required = false, defaultValue = "false") boolean deploy,
			@RequestHeader(value = "tracking_id", required = false) String trackingID,
			@RequestHeader(value = "provider", required = false) String provider,
			@RequestHeader(value = "shareUserName", required = false) String shareUserName,
			@RequestHeader(value = "modName", required = false) String modName,
			@RequestHeader(value = "deployment_env", required = false) Integer deployment_env,
			@RequestHeader(value = "Request-ID", required = false) String request_id) throws AcumosServiceException {

		OnboardingNotification onboardingStatus = null;

		if (trackingID != null) {
			logger.debug( "Tracking ID: " + trackingID);
		} else {
			trackingID = UUID.randomUUID().toString();
			logger.debug( "Tracking ID Created: " + trackingID);
		}

		if (request_id != null) {
			logger.debug( "Request ID: " + request_id);
		} else {
			request_id = UUID.randomUUID().toString();
			logger.debug( "Request ID Created: " + request_id);
		}

		// code to retrieve the current pom version
		// UtilityFunction.getCurrentVersion();
		onboardingStatus = new OnboardingNotification(env.getProperty("cmndatasvc.cmnDataSvcEndPoinURL"), env.getProperty("cmndatasvc.cmnDataSvcUser"), env.getProperty("cmndatasvc.cmnDataSvcPwd"), request_id);
		onboardingStatus.setRequestId(request_id);
		MDC.put(OnboardingLogConstants.MDCs.REQUEST_ID, request_id);

		String fileName = "OnboardingLog.txt";
		LogBean logBean = new LogBean();
		logBean.setLogPath(lOG_DIR_LOC + File.separator + trackingID);
		logBean.setFileName(fileName);
		LogThreadLocal logThread = new LogThreadLocal();
		logThread.set(logBean);
		// create log file to capture logs as artifact
		UtilityFunction.createLogFile();

		String version = UtilityFunction.getProjectVersion();
		logger.debug( "On-boarding version : " + version);

		MLPUser shareUser = null;
		Metadata mData = null;
		String modelName = null;
		MLPTask task = null;
		long taskId = 0;

		try {
			// 'authorization' represents JWT token here...!
			if (authorization == null) {
				logger.error( "Token Not Available...!");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
						"Token Not Available...!");
			}

			if (shareUserName != null) {
				RestPageResponse<MLPUser> user = cdmsClient.findUsersBySearchTerm(shareUserName,
						new RestPageRequest(0, 9));

				List<MLPUser> uList = user.getContent();

				if (uList.isEmpty()) {
					logger.error(
							"User " + shareUserName + " not found: cannot share model; onboarding aborted");
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
							"User " + shareUserName + " not found: cannot share model; onboarding aborted");
				} else {
					shareUser = uList.get(0);
				}
			}

			// Call to validate Token .....!
			String ownerId = validate(authorization, provider);

			if (ownerId != null && !ownerId.isEmpty()) {

				logger.debug( "Token validation successful");

				logger.debug(
						"Onboarding request recieved with " + model.getOriginalFilename());

				MDC.put(OnboardingLogConstants.MDCs.USER, ownerId);
				modelOriginalName = model.getOriginalFilename();
				String modelId = UtilityFunction.getGUID();
				File outputFolder = new File("tmp", modelId);
				outputFolder.mkdirs();
				boolean isSuccess = false;
				MLPSolution mlpSolution = null;
				File localmodelFile = new File(outputFolder, model.getOriginalFilename());
				File localMetadataFile = new File(outputFolder, metadata.getOriginalFilename());
				File localProtobufFile = new File(outputFolder, schema.getOriginalFilename());

				MLPSolutionRevision revision;
				File licenseFile = null;

				if (license != null && !license.isEmpty()) {

					String licenseFileName = license.getOriginalFilename();
					String licenseFileExtension = licenseFileName.substring(licenseFileName.indexOf('.'));

					if (!licenseFileExtension.toLowerCase().equalsIgnoreCase(OnboardingConstants.LICENSE_EXTENSION)) {
						logger.debug("License file extension of " + licenseFileName + " should be \".json\"");
						return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(
								OnboardingConstants.BAD_REQUEST_CODE,
								OnboardingConstants.LICENSE_FILENAME_ERROR + ". Original File : " + licenseFileName),
								HttpStatus.BAD_REQUEST);
					}
					if (!licenseFileName.toLowerCase().equalsIgnoreCase(OnboardingConstants.LICENSE_FILENAME)) {
						logger.debug("Changing License file name = " + licenseFileName + " to \"license.json\"");
						licenseFileName = OnboardingConstants.LICENSE_FILENAME;
					}
					String inputLicense = new String(license.getBytes());
					String result =  validateLicense(inputLicense.toString());
					if(result.equals("SUCCESS")) {
						logger.debug("License validation is successfull.");
						licenseFile = new File(outputFolder, licenseFileName);
						UtilityFunction.copyFile(license.getInputStream(), licenseFile);

					}
					else {
						logger.error( "License validation failed. ");
						return new ResponseEntity<ServiceResponse>(
								ServiceResponse.errorResponse(AcumosServiceException.ErrorCode.UNKNOWN.name(),
										"License Validaton failed "+result),HttpStatus.BAD_REQUEST);
					}
				}

				try {

					try {
						// Notify Create solution or get existing solution ID
						// has
						// started
						if (onboardingStatus != null) {

							task = new MLPTask();
							task.setTaskCode("OB");
							task.setStatusCode("ST");
							task.setName("OnBoarding");
							task.setUserId(ownerId);
							task.setCreated(Instant.now());
							task.setModified(Instant.now());
							task.setTrackingId(trackingID);
							onboardingStatus.setTrackingId(trackingID);
							onboardingStatus.setUserId(ownerId);

							task = cdmsClient.createTask(task);

							logger.debug( "TaskID: " + task.getTaskId());
							taskId = task.getTaskId();
							onboardingStatus.setTaskId(task.getTaskId());
							onboardingStatus.notifyOnboardingStatus("CreateSolution", "ST", "CreateSolution Started");
						}

						UtilityFunction.copyFile(model.getInputStream(), localmodelFile);

						UtilityFunction.copyFile(metadata.getInputStream(), localMetadataFile);

						UtilityFunction.copyFile(schema.getInputStream(), localProtobufFile);

						metadataParser = new MetadataParser(localMetadataFile);
						mData = metadataParser.getMetadata();

						mData.setOwnerId(ownerId);

						List<MLPSolution> solList = getExistingSolution(mData);

						boolean isListEmpty = solList.isEmpty();

						if (isListEmpty) {
							mlpSolution = createSolution(mData, onboardingStatus);
							mData.setSolutionId(mlpSolution.getSolutionId());
							logger.debug(
									"New solution created Successfully " + mlpSolution.getSolutionId());
						} else {
							logger.debug(
									"Existing solution found for model name " + solList.get(0).getName());
							mlpSolution = solList.get(0);
							mData.setSolutionId(mlpSolution.getSolutionId());
						}

						revision = createSolutionRevision(mData, localProtobufFile);
						modelName = mData.getModelName() + "_" + mData.getSolutionId();
						if (license != null && !license.isEmpty()) {
							Workflow workflow = performSVScan(mlpSolution.getSolutionId(), mData.getRevisionId(), SVConstants.CREATED, ownerId);
							if (!workflow.isWorkflowAllowed()) {
								logger.debug("SV Scan failed, "+workflow.getReason());
								return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(
										OnboardingConstants.BAD_REQUEST_CODE,
										"License Security Verification Scan failed, "+workflow.getReason()),
										HttpStatus.BAD_REQUEST);
							}
						}
						// Solution id creation completed
						// Notify Creation of solution ID is successful
						if (onboardingStatus != null) {
							// set solution Id
							if (mlpSolution.getSolutionId() != null) {
								onboardingStatus.setSolutionId(mlpSolution.getSolutionId());
							}
							// set revision id
							if (mData.getRevisionId() != null) {
								onboardingStatus.setRevisionId(mData.getRevisionId());
							}
							// notify
							onboardingStatus.notifyOnboardingStatus("CreateSolution", "SU",
									"CreateSolution Successful");
						}
					} catch (AcumosServiceException e) {
						HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
						logger.error( e.getErrorCode() + "  " + e.getMessage());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
						if (e.getErrorCode().equalsIgnoreCase(OnboardingConstants.INVALID_PARAMETER)) {
							httpCode = HttpStatus.BAD_REQUEST;
							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, httpCode.toString());
						}
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, httpCode.toString());
						// Create Solution failed. Notify
						if (onboardingStatus != null) {
							// notify
							onboardingStatus.notifyOnboardingStatus("CreateSolution", "FA", e.getMessage());
						}
						return new ResponseEntity<ServiceResponse>(
								ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage(), modelName), httpCode);
					} catch (Exception e) {
						logger.error( e.getMessage());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						// Create Solution failed. Notify
						if (onboardingStatus != null) {
							// notify
							onboardingStatus.notifyOnboardingStatus("CreateSolution", "FA", e.getMessage());
						}
						if (e instanceof AcumosServiceException) {
							return new ResponseEntity<ServiceResponse>(
									ServiceResponse.errorResponse(((AcumosServiceException) e).getErrorCode(),
											e.getMessage(), modelName),
									HttpStatus.INTERNAL_SERVER_ERROR);
						} else {
							return new ResponseEntity<ServiceResponse>(
									ServiceResponse.errorResponse(AcumosServiceException.ErrorCode.UNKNOWN.name(),
											e.getMessage(), modelName),
									HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}

					String actualModelName = getActualModelName(mData, mlpSolution.getSolutionId());
					// Add artifacts started. Notification will be handed by
					// addArtifact method itself for started/success/failure
					artifactsDetails = getArtifactsDetails();

					logger.debug("Metadata Version before adding Artifacts = "+mData.getVersion());
					addArtifact(mData, localmodelFile, getArtifactTypeCode("Model Image"), mData.getModelName(),
							onboardingStatus);

					addArtifact(mData, localProtobufFile, getArtifactTypeCode("Model Image"), mData.getModelName(),
							onboardingStatus);

					addArtifact(mData, localMetadataFile, getArtifactTypeCode("Metadata"), mData.getModelName(),
							onboardingStatus);

					if (license != null && !license.isEmpty()) {
						addArtifact(mData, licenseFile, getArtifactTypeCode(OnboardingConstants.ARTIFACT_TYPE_LICENSE_LOG),
								"license", onboardingStatus);
					}

					if(source != null && !source.isEmpty()) {

						String sourceFileName = source.getOriginalFilename();
						String sourceFileExtension = sourceFileName.substring(sourceFileName.indexOf('.'));

						if (!sourceFileExtension.toLowerCase().equalsIgnoreCase(".r")) {
							logger.debug("R file extension of " + sourceFileName + " should be \".r or .R\"");
							return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(
									OnboardingConstants.BAD_REQUEST_CODE,
									"Error Occured: .r or .R File Required . Original File : " + sourceFileName),
									HttpStatus.BAD_REQUEST);
						}

						File localRsourceFile = new File(outputFolder, source.getOriginalFilename());
						UtilityFunction.copyFile(source.getInputStream(), localRsourceFile);

						addArtifact(mData, localRsourceFile, getArtifactTypeCode("Code"), mData.getModelName(),
								onboardingStatus);
					}

					// Notify TOSCA generation started
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "ST", "TOSCA Generation Started");
					}

					generateTOSCA(localProtobufFile, localMetadataFile, mData, onboardingStatus);

					// Notify TOSCA generation successful
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "SU", "TOSCA Generation Successful");
					}

					logger.debug( "Generate Microservice Flag: " +isCreateMicroservice);

					ResponseEntity<ServiceResponse> response = null;
					String dockerUri = null;

					if (isCreateMicroservice) {
						// call microservice
						logger.debug(  "Before microservice call Parameters : SolutionId "
								+ mlpSolution.getSolutionId() + " and RevisionId " + revision.getRevisionId());
						try {
							response = microserviceClient.generateMicroservice(
									mlpSolution.getSolutionId(), revision.getRevisionId(), provider, authorization,
									trackingID, modName, deployment_env, request_id, deploy);
							if (response.getStatusCodeValue() == 200 || response.getStatusCodeValue() == 201) {
								isSuccess = true;
							}
							taskId = response.getBody().getTaskId();
						} catch (Exception e) {
							if (e instanceof HttpHostConnectException || e.getCause() instanceof ConnectException) {
								if (onboardingStatus != null) {
									onboardingStatus.notifyOnboardingStatus("Dockerize", "FA", e.getMessage());
								}
								logger.debug( "Dockerize Failed due to connectException: " + e.getMessage());
								throw new ConnectException("ConnectException occured while invoking microservice API " + e.getMessage());
							}
							logger.error(
									"Exception occured while invoking microservice API " + e);
							throw e;
						}
					} else {
						isSuccess = true;
					}

					// Model Sharing
					if (isSuccess && (shareUserName != null) && revision.getRevisionId() != null) {
						try {
							AuthorTransport author = new AuthorTransport(shareUserName, shareUser.getEmail());
							AuthorTransport authors[] = new AuthorTransport[1];
							logger.debug(
									"Author Name " + author.getName() + " and Email " + author.getContact());
							authors[0] = author;
							revision.setAuthors(authors);
							cdmsClient.updateSolutionRevision(revision);
							logger.debug(
									"Model Shared Successfully with " + shareUserName);
						} catch (Exception e) {
							isSuccess = false;
							logger.error( " Failed to share Model", e);
							throw e;
						}
					}

					if(response != null){
						dockerUri = response.getBody().getDockerImageUri();
					}
					ResponseEntity<ServiceResponse> res = new ResponseEntity<ServiceResponse>(
							ServiceResponse.successResponse(mlpSolution, taskId, trackingID, dockerUri), HttpStatus.CREATED);
					logger.debug(
							"Onboarding is successful for model name: " + mlpSolution.getName() + ", SolutionID: "
									+ mlpSolution.getSolutionId() + ", Status Code: " + res.getStatusCode());
					return res;
				} finally {

					try {
						UtilityFunction.deleteDirectory(outputFolder);

						if (isSuccess == false) {
							if (metadataParser != null && mData != null) {
								task.setSolutionId(mData.getSolutionId());
								task.setRevisionId(mData.getRevisionId());
								task.setStatusCode("FA");
								logger.debug("MLP task updating with the values =" + task.toString());
								cdmsClient.updateTask(task);
								logger.debug("Onboarding Failed, Reverting failed solutions and artifacts.");
								revertbackOnboarding(metadataParser.getMetadata(), mlpSolution.getSolutionId());
							}
						}

						if (isSuccess == true) {

							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.COMPLETED.name());
							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, "Onboarding Completed");
							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.CREATED.toString());

							task.setSolutionId(mData.getSolutionId());
							task.setRevisionId(mData.getRevisionId());
							task.setStatusCode("SU");
							logger.debug("MLP task updating with the values =" + task.toString());
							cdmsClient.updateTask(task);
						}

						// push docker build log into nexus

						File file = new java.io.File(
								lOG_DIR_LOC + File.separator + trackingID + File.separator + fileName);
						logger.debug( "Log file length " + file.length(), file.getPath(),
								fileName);
						if (metadataParser != null && mData != null) {
							logger.debug(
									"Adding of log artifacts into nexus started " + fileName);

							// String nexusArtifactID = "onboardingLog_"+trackingID;
							String nexusArtifactID = "OnboardingLog";

							addArtifact(mData, file, getArtifactTypeCode(OnboardingConstants.ARTIFACT_TYPE_LOG),
									nexusArtifactID, onboardingStatus);
							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE,
									OnboardingLogConstants.ResponseStatus.COMPLETED.name());
							logger.debug( "Artifacts log pushed to nexus successfully",
									fileName);
						}

						// delete log file
						UtilityFunction.deleteDirectory(file);
						logThread.unset();
						mData = null;
					} catch (AcumosServiceException e) {
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						mData = null;
						logger.error( "RevertbackOnboarding Failed: ", e.getMessage());
						HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
						return new ResponseEntity<ServiceResponse>(
								ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage(), modelName), httpCode);
					}
				}
			} else {
				try {
					MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
					MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, "Either Username/Password is invalid.");
					MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.UNAUTHORIZED.toString());
					logger.error( "Either Username/Password is invalid.");
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_TOKEN,
							"Either Username/Password is invalid.");
				} catch (AcumosServiceException e) {
					return new ResponseEntity<ServiceResponse>(
							ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()), HttpStatus.UNAUTHORIZED);
				}
			}

		} catch (AcumosServiceException e) {

			HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());

			logger.error( e.getErrorCode() + "  " + e.getMessage());
			if (e.getErrorCode().equalsIgnoreCase(OnboardingConstants.INVALID_PARAMETER)) {
				httpCode = HttpStatus.BAD_REQUEST;
			}
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, httpCode.toString());
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, httpCode.toString());

			return new ResponseEntity<ServiceResponse>(
					ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage(), modelName), httpCode);
		} catch (HttpClientErrorException e) {
			// Handling #401 and 400(BAD_REQUEST) is added as CDS throws 400 if apitoken is
			// invalid.
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, e.getMessage());
			if (HttpStatus.UNAUTHORIZED == e.getStatusCode() || HttpStatus.BAD_REQUEST == e.getStatusCode()) {
				logger.error( e.getStatusCode() + "  " + e.getMessage());
				MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, e.getStatusCode().toString());
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + HttpStatus.UNAUTHORIZED, "Unauthorized User", modelName),
						HttpStatus.UNAUTHORIZED);
			} else {
				logger.error( e.getMessage(), e);
				MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, e.getStatusCode().toString());
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + e.getStatusCode(), e.getMessage(), modelName),
						e.getStatusCode());
			}
		} catch (Exception e) {
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			logger.error( "onboardModel Failed Exception " + e.getMessage(), e);
			if (e instanceof AcumosServiceException) {
				return new ResponseEntity<ServiceResponse>(ServiceResponse
						.errorResponse(((AcumosServiceException) e).getErrorCode(), e.getMessage(), modelName),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				return new ResponseEntity<ServiceResponse>(ServiceResponse
						.errorResponse(AcumosServiceException.ErrorCode.UNKNOWN.name(), e.getMessage(), modelName),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	}

	@Override
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Onboard the interchange models", response = ServiceResponse.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Created", response = ServiceResponse.class),
			@ApiResponse(code = 500, message = "Something bad happened", response = ServiceResponse.class),
			@ApiResponse(code = 400, message = "Invalid request", response = ServiceResponse.class),
			@ApiResponse(code = 401, message = "Unauthorized User", response = ServiceResponse.class) })
	@RequestMapping(value = "/advancedModel", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<ServiceResponse> advancedModelOnboard(HttpServletRequest request,
			@RequestPart(required = false) MultipartFile model, @RequestPart(required = false) MultipartFile license,
			@RequestPart(required = false) MultipartFile protobuf,
			@RequestHeader(value = "modelname", required = true) String modName,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "isCreateMicroservice", required = false) boolean isCreateMicroservice,
			@RequestHeader(value = "deploy", required = false, defaultValue = "false") boolean deploy,
			@RequestHeader(value = "dockerfileURL", required = false) String dockerfileURL,
			@RequestHeader(value = "provider", required = false) String provider,
			@RequestHeader(value = "tracking_id", required = false) String trackingID,
			@RequestHeader(value = "Request-ID", required = false) String request_id,
			@RequestHeader(value = "shareUserName", required = false) String shareUserName)
					throws AcumosServiceException {

		OnboardingNotification onboardingStatus = null;

		if (trackingID != null) {
			logger.debug( "Tracking ID: " + trackingID);
		} else {
			trackingID = UUID.randomUUID().toString();
			logger.debug( "Tracking ID Created: " + trackingID);
		}

		if (request_id != null) {
			logger.debug( "Request ID: " + request_id);
		} else {
			request_id = UUID.randomUUID().toString();
			logger.debug( "Request ID Created: " + request_id);
		}
		logger.debug("dockerFileURL: "+dockerfileURL);

		// code to retrieve the current pom version
		// UtilityFunction.getCurrentVersion();
		onboardingStatus = new OnboardingNotification(env.getProperty("cmndatasvc.cmnDataSvcEndPoinURL"), env.getProperty("cmndatasvc.cmnDataSvcUser"), env.getProperty("cmndatasvc.cmnDataSvcPwd"), request_id);
		onboardingStatus.setRequestId(request_id);
		MDC.put(OnboardingLogConstants.MDCs.REQUEST_ID, request_id);

		String fileName = "AdvancedModelOnboardLog.txt";
		LogBean logBean = new LogBean();
		logBean.setLogPath(lOG_DIR_LOC + File.separator + trackingID);
		logBean.setFileName(fileName);
		LogThreadLocal logThread = new LogThreadLocal();
		logThread.set(logBean);
		// create log file to capture logs as artifact
		UtilityFunction.createLogFile();

		String version = UtilityFunction.getProjectVersion();
		logger.debug( "On-boarding version : " + version);

		MLPUser shareUser = null;
		Metadata mData = new Metadata();
		mData.setSolutionName(modName);
		String modelName = null;
		MLPTask task = null;
		long taskId = 0;

		String modelId = UtilityFunction.getGUID();
		File outputFolder = new File("tmp", modelId);
		outputFolder.mkdirs();
		boolean isSuccess = false;
		MLPSolution mlpSolution = null;
		String modelType = null;

		try {

			if((model !=null && !model.isEmpty()) && (dockerfileURL!=null && !dockerfileURL.isEmpty())) {

				logger.error( "Either pass Model File or Docker Uri for this request");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
						"Either pass Model File or Docker Uri for this request");
			}

			// 'authorization' represents JWT token here...!
			if (authorization == null) {
				logger.error( "Token Not Available...!");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
						"Token Not Available...!");
			}

			if (shareUserName != null) {
				RestPageResponse<MLPUser> user = cdmsClient.findUsersBySearchTerm(shareUserName,
						new RestPageRequest(0, 9));

				List<MLPUser> uList = user.getContent();

				if (uList.isEmpty()) {
					logger.error(
							"User " + shareUserName + " not found: cannot share model; onboarding aborted");
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
							"User " + shareUserName + " not found: cannot share model; onboarding aborted");
				} else {
					shareUser = uList.get(0);
				}
			}

			modelType = getModelType(model);

			// Call to validate Token .....!
			String ownerId = validate(authorization, provider);

			if (ownerId != null && !ownerId.isEmpty()) {

				logger.debug( "Token validation successful");

				MLPSolutionRevision revision;
				File localmodelFile = null;
				File licenseFile = null;

				if (license != null && !license.isEmpty()) {
					String licenseFileName = license.getOriginalFilename();
					String licenseFileExtension = licenseFileName.substring(licenseFileName.indexOf('.'));

					if (!licenseFileExtension.toLowerCase().equalsIgnoreCase(OnboardingConstants.LICENSE_EXTENSION)) {
						logger.debug("License file extension of " + licenseFileName + " should be \".json\"");
						return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(
								OnboardingConstants.BAD_REQUEST_CODE,
								OnboardingConstants.LICENSE_FILENAME_ERROR + ". Original File : " + licenseFileName),
								HttpStatus.BAD_REQUEST);
					}

					if (!licenseFileName.toLowerCase().equalsIgnoreCase(OnboardingConstants.LICENSE_FILENAME)) {
						logger.debug("Changing License file name = " + licenseFileName + " to \"license.json\"");
						licenseFileName = OnboardingConstants.LICENSE_FILENAME;
					}

					licenseFile = new File(outputFolder, licenseFileName);
					UtilityFunction.copyFile(license.getInputStream(), licenseFile);
				}

				File localProtobufFile = null;

				if(protobuf != null && !protobuf.isEmpty()) {

					String protobufFileName = protobuf.getOriginalFilename();
					String protobufFileExtension = protobufFileName.substring(protobufFileName.indexOf('.'));

					if (!protobufFileExtension.toLowerCase().equalsIgnoreCase(".proto")) {
						logger.debug("Protobuf file extension of " + protobufFileName + " should be \".proto\"");
						return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(
								OnboardingConstants.BAD_REQUEST_CODE,
								"Error Occurred: proto File Required . Original File : " + protobufFileName),
								HttpStatus.BAD_REQUEST);
					}

					localProtobufFile = new File(outputFolder, protobuf.getOriginalFilename());
					UtilityFunction.copyFile(protobuf.getInputStream(), localProtobufFile);

				}

				try {

					try {
						// Notify Create solution or get existing solution ID
						// has
						// started
						if (onboardingStatus != null) {

							task = new MLPTask();
							task.setTaskCode("OB");
							task.setStatusCode("ST");
							task.setName("OnBoarding");
							task.setUserId(ownerId);
							task.setCreated(Instant.now());
							task.setModified(Instant.now());
							task.setTrackingId(trackingID);
							onboardingStatus.setTrackingId(trackingID);
							onboardingStatus.setUserId(ownerId);
							task = cdmsClient.createTask(task);

							logger.debug( "TaskID: " + task.getTaskId());
							taskId = task.getTaskId();
							onboardingStatus.setTaskId(task.getTaskId());
							onboardingStatus.notifyOnboardingStatus("CreateSolution", "ST", "CreateSolution Started");
						}

						if (modelType.equalsIgnoreCase("interchangedModel")) {

							localmodelFile = new File(outputFolder, model.getOriginalFilename());
							UtilityFunction.copyFile(model.getInputStream(), localmodelFile);
						}

						logger.debug( "Set the owner ID and Model Name");
						mData.setOwnerId(ownerId);
						mData.setModelName(modName);

						List<MLPSolution> solList = getExistingSolution(mData);
						boolean isListEmpty = solList.isEmpty();

						if (isListEmpty) {
							mlpSolution = createSolution(mData, onboardingStatus);
							mData.setSolutionId(mlpSolution.getSolutionId());
							logger.debug(
									"New solution created Successfully " + mlpSolution.getSolutionId());
						} else {
							logger.debug(
									"Existing solution found for model name " + solList.get(0).getName());
							mlpSolution = solList.get(0);
							mData.setSolutionId(mlpSolution.getSolutionId());
						}

						revision = createSolutionRevision(mData, localProtobufFile);
						modelName = mData.getModelName();

						// Solution id creation completed
						// Notify Creation of solution ID is successful
						if (onboardingStatus != null) {
							// set solution Id
							if (mlpSolution.getSolutionId() != null) {
								onboardingStatus.setSolutionId(mlpSolution.getSolutionId());
							}
							// set revision id
							if (mData.getRevisionId() != null) {
								onboardingStatus.setRevisionId(mData.getRevisionId());
							}
							// notify
							onboardingStatus.notifyOnboardingStatus("CreateSolution", "SU",
									"CreateSolution Successful");
						}
					} catch (AcumosServiceException e) {
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
						logger.error( e.getErrorCode() + "  " + e.getMessage());
						if (e.getErrorCode().equalsIgnoreCase(OnboardingConstants.INVALID_PARAMETER)) {
							httpCode = HttpStatus.BAD_REQUEST;
						}
						// Create Solution failed. Notify
						if (onboardingStatus != null) {
							// notify
							onboardingStatus.notifyOnboardingStatus("CreateSolution", "FA", e.getMessage());
						}
						return new ResponseEntity<ServiceResponse>(
								ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage(), modelName), httpCode);
					} catch (Exception e) {
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						logger.error( e.getMessage());
						// Create Solution failed. Notify
						if (onboardingStatus != null) {
							// notify
							onboardingStatus.notifyOnboardingStatus("CreateSolution", "FA", e.getMessage());
						}
						if (e instanceof AcumosServiceException) {
							return new ResponseEntity<ServiceResponse>(
									ServiceResponse.errorResponse(((AcumosServiceException) e).getErrorCode(),
											e.getMessage(), modelName),
									HttpStatus.INTERNAL_SERVER_ERROR);
						} else {
							return new ResponseEntity<ServiceResponse>(
									ServiceResponse.errorResponse(AcumosServiceException.ErrorCode.UNKNOWN.name(),
											e.getMessage(), modelName),
									HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}

					String dockerImageUri = null;
					logger.debug("model type="+modelType);
					artifactsDetails = getArtifactsDetails();

					if (dockerfileURL != null) {
						addArtifact(mData, dockerfileURL, getArtifactTypeCode("Docker Image"), null);
					} else if (modelType.equalsIgnoreCase("interchangedModel")) {
						addArtifact(mData, localmodelFile, getArtifactTypeCode("Model Image"), mData.getModelName(),
								onboardingStatus);
					} else if(modelType.equalsIgnoreCase("other")) {
						//Need to add modelType.equalsIgnoreCase("dockerImage")
						dockerImageUri = imagetagProxyPrefix+ File.separator + modelName +":" +mData.getVersion();
						logger.debug( "dockerImageUri: " + dockerImageUri);
						addArtifact(mData, dockerImageUri, getArtifactTypeCode("Docker Image"),
								onboardingStatus);
					}

					if (license != null && !license.isEmpty()) {
						addArtifact(mData, licenseFile, getArtifactTypeCode(OnboardingConstants.ARTIFACT_TYPE_LICENSE_LOG),
								"license", onboardingStatus);
					}

					if(protobuf != null && !protobuf.isEmpty()) {
						addArtifact(mData, localProtobufFile, getArtifactTypeCode("Model Image"), mData.getModelName(),
								onboardingStatus);
					}

					// Notify TOSCA generation started
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "ST", "TOSCA Generation Started");
					}

					if(protobuf != null && !protobuf.isEmpty()) {
						generateTOSCA(localProtobufFile, null, mData, onboardingStatus);
					}

					// Notify TOSCA generation successful
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "SU", "TOSCA Generation Successful");
					}

					logger.debug( "isCreateMicroservice: " + isCreateMicroservice);

					ResponseEntity<ServiceResponse> response = null;

					// call microservice
					if (isCreateMicroservice) {
						logger.debug( "Before microservice call Parameters : SolutionId "
								+ mlpSolution.getSolutionId() + " and RevisionId " + revision.getRevisionId());
						try {
							response = microserviceClient.generateMicroservice(
									mlpSolution.getSolutionId(), revision.getRevisionId(), provider, authorization,
									trackingID, mData.getModelName(), null, request_id, deploy);
							if (response.getStatusCodeValue() == 200 || response.getStatusCodeValue() == 201) {
								isSuccess = true;
							}
							taskId = response.getBody().getTaskId();
						} catch (Exception e) {
							if (e instanceof HttpHostConnectException || e.getCause() instanceof ConnectException) {
								if (onboardingStatus != null) {
									onboardingStatus.notifyOnboardingStatus("Dockerize", "FA", e.getMessage());
								}
								logger.debug( "Dockerize Failed due to connectException: " + e.getMessage());
								throw new ConnectException("ConnectException occured while invoking microservice API " + e.getMessage());
							}

							logger.error(
									"Exception occured while invoking microservice API " + e);
							throw e;
						}
					} else {
						isSuccess = true;
					}

					// Model Sharing
					if (isSuccess && (shareUserName != null) && revision.getRevisionId() != null) {
						try {
							AuthorTransport author = new AuthorTransport(shareUserName, shareUser.getEmail());
							AuthorTransport authors[] = new AuthorTransport[1];
							logger.debug(
									"Author Name " + author.getName() + " and Email " + author.getContact());
							authors[0] = author;
							revision.setAuthors(authors);
							cdmsClient.updateSolutionRevision(revision);
							logger.debug(
									"Model Shared Successfully with " + shareUserName);
							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE,
									HttpStatus.CREATED.toString());
						} catch (Exception e) {
							isSuccess = false;
							logger.error( " Failed to share Model", e);
							throw e;
						}
					}

					ResponseEntity<ServiceResponse> res = new ResponseEntity<ServiceResponse>(
							ServiceResponse.successResponse(mlpSolution, taskId, trackingID,dockerImageUri), HttpStatus.CREATED);
					logger.debug(
							"Onboarding is successful for model name: " + mlpSolution.getName() + ", SolutionID: "
									+ mlpSolution.getSolutionId() + ", Status Code: " + res.getStatusCode());
					return res;
				} finally {

					try {
						UtilityFunction.deleteDirectory(outputFolder);

						if (isSuccess == false) {
							task.setSolutionId(mData.getSolutionId());
							task.setRevisionId(mData.getRevisionId());
							task.setStatusCode("FA");
							logger.debug("MLP task updating with the values =" + task.toString());
							cdmsClient.updateTask(task);
							logger.debug("Onboarding Failed, Reverting failed solutions and artifacts.");
							if (metadataParser != null && mData != null) {
								revertbackOnboarding(metadataParser.getMetadata(), mlpSolution.getSolutionId());
							}
						}

						if (isSuccess == true) {
							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.COMPLETED.name());
							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, "Advanced Model Onboarding Completed");
							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.CREATED.toString());

							task.setSolutionId(mData.getSolutionId());
							task.setRevisionId(mData.getRevisionId());
							task.setStatusCode("SU");
							logger.debug("MLP task updating with the values =" + task.toString());
							cdmsClient.updateTask(task);
						}

						// push docker build log into nexus

						File file = new java.io.File(
								lOG_DIR_LOC + File.separator + trackingID + File.separator + fileName);
						logger.debug( "Log file length " + file.length(), file.getPath(),
								fileName);
						if (mData != null) {
							logger.debug(
									"Adding of log artifacts into nexus started " + fileName);

							// String nexusArtifactID = "onboardingLog_"+trackingID;
							String nexusArtifactID = "OnboardingLog";

							addArtifact(mData, file, getArtifactTypeCode(OnboardingConstants.ARTIFACT_TYPE_LOG),
									nexusArtifactID, onboardingStatus);
							MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE,
									OnboardingLogConstants.ResponseStatus.COMPLETED.name());
							logger.debug( "Artifacts log pushed to nexus successfully",
									fileName);
						}

						// delete log file
						UtilityFunction.deleteDirectory(file);
						logThread.unset();
						mData = null;
					} catch (AcumosServiceException e) {
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.INTERNAL_SERVER_ERROR.toString());
						mData = null;
						logger.error( "RevertbackOnboarding Failed");
						HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
						return new ResponseEntity<ServiceResponse>(
								ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage(), modelName), httpCode);
					}
				}
			} else {
				try {
					MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
					MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, "Either Username/Password is invalid.");
					MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.UNAUTHORIZED.toString());
					logger.error( "Either Username/Password is invalid.");
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_TOKEN,
							"Either Username/Password is invalid.");
				} catch (AcumosServiceException e) {
					return new ResponseEntity<ServiceResponse>(
							ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()), HttpStatus.UNAUTHORIZED);
				}
			}

		} catch (AcumosServiceException e) {

			HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
			logger.error( e.getErrorCode() + "  " + e.getMessage());
			if (e.getErrorCode().equalsIgnoreCase(OnboardingConstants.INVALID_PARAMETER)) {
				httpCode = HttpStatus.BAD_REQUEST;
			}
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION,  httpCode.toString());
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, httpCode.toString());
			return new ResponseEntity<ServiceResponse>(
					ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage(), modelName), httpCode);
		} catch (HttpClientErrorException e) {
			// Handling #401 and 400(BAD_REQUEST) is added as CDS throws 400 if apitoken is
			// invalid.
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, e.getMessage());
			if (HttpStatus.UNAUTHORIZED == e.getStatusCode() || HttpStatus.BAD_REQUEST == e.getStatusCode()) {
				logger.debug(
						"Unauthorized User - Either Username/Password is invalid.");
				MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, e.getStatusCode().toString());
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + HttpStatus.UNAUTHORIZED, "Unauthorized User", modelName),
						HttpStatus.UNAUTHORIZED);
			} else {
				logger.error( e.getMessage(), e);
				MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, e.getStatusCode().toString());
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + e.getStatusCode(), e.getMessage(), modelName),
						e.getStatusCode());
			}
		} catch (Exception e) {
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_STATUS_CODE, OnboardingLogConstants.ResponseStatus.ERROR.name());
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_DESCRIPTION, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			MDC.put(OnboardingLogConstants.MDCs.RESPONSE_CODE, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			logger.error( "onboardModel Failed Exception " + e.getMessage(), e);
			if (e instanceof AcumosServiceException) {
				return new ResponseEntity<ServiceResponse>(ServiceResponse
						.errorResponse(((AcumosServiceException) e).getErrorCode(), e.getMessage(), modelName),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				return new ResponseEntity<ServiceResponse>(ServiceResponse
						.errorResponse(AcumosServiceException.ErrorCode.UNKNOWN.name(), e.getMessage(), modelName),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}


	/**
	 *  This method returns model type
	 * @param model
	 * @return model type
	 */
	private String getModelType(MultipartFile model) {

		String modelType;
		if (model != null && !model.isEmpty()) {

			String fileExt = getExtensionOfFile(model.getOriginalFilename());
			if (fileExt.equalsIgnoreCase("onnx") || fileExt.equalsIgnoreCase("pfa")) {
				modelType = "interchangedModel";
				logger.debug( "ModelType is " + modelType);
			} else if (fileExt.equalsIgnoreCase("tar")) {
				modelType = "dockerImage";
				logger.debug( "ModelType is " + modelType);
			} else {
				modelType = "other";
				logger.debug( "ModelType is " + modelType);
			}

		} else {
			modelType = "other";
			logger.debug( "ModelType is " + modelType);
		}

		return modelType;

	}

	private Map<String, String> getArtifactsDetails() {
		List<MLPCodeNamePair> typeCodeList = cdmsClient.getCodeNamePairs(CodeNameType.ARTIFACT_TYPE);
		Map<String, String> artifactsDetails = new HashMap<>();
		if (!typeCodeList.isEmpty()) {
			for (MLPCodeNamePair codeNamePair : typeCodeList) {
				artifactsDetails.put(codeNamePair.getName(), codeNamePair.getCode());
			}
		}
		return artifactsDetails;
	}

	private String getArtifactTypeCode(String artifactTypeName) {
		String typeCode = artifactsDetails.get(artifactTypeName);
		return typeCode;
	}

	@Override
	public String getCmnDataSvcEndPoinURL() {
		return cmnDataSvcEndPoinURL;
	}

	@Override
	public String getCmnDataSvcUser() {
		return cmnDataSvcUser;
	}

	@Override
	public String getCmnDataSvcPwd() {
		return cmnDataSvcPwd;
	}

}
