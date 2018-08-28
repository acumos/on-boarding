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
import java.io.IOException;
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
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.AuthorTransport;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.JsonRequest;
import org.acumos.onboarding.common.utils.JsonResponse;
import org.acumos.onboarding.common.utils.LogBean;
import org.acumos.onboarding.common.utils.LogThreadLocal;
import org.acumos.onboarding.common.utils.OnboardingConstants;
import org.acumos.onboarding.common.utils.UtilityFunction;
import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.component.docker.preparation.MetadataParser;
import org.acumos.onboarding.services.DockerService;
import org.json.simple.JSONObject;
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
	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(OnboardingController.class);
	Map<String, String> artifactsDetails = new HashMap<>();
	public static final String lOG_DIR_LOC = "/maven/logs/on-boarding/applog";
	
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
		logger.debug(EELFLoggerDelegate.debugLogger, "Started User Authentication");
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
				logger.debug(EELFLoggerDelegate.debugLogger, "User Authentication Successful");
				return new ResponseEntity<ServiceResponse>(ServiceResponse.successJWTResponse(token), HttpStatus.OK);
			} else {
				logger.debug(EELFLoggerDelegate.debugLogger, "Either Username/Password is invalid.");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
						"Either Username/Password is invalid.");
			}

		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger, e.getMessage(), e);
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
	public ResponseEntity<ServiceResponse> onboardModel(HttpServletRequest request,
			@RequestPart(required = true) MultipartFile model, @RequestPart(required = true) MultipartFile metadata,
			@RequestPart(required = true) MultipartFile schema,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "tracking_id", required = false) String trackingID,
			@RequestHeader(value = "provider", required = false) String provider,
			@RequestHeader(value = "shareUserName", required = false) String shareUserName,
	        @RequestHeader(value = "modName", required = false) String modName,
            @RequestHeader(value = "deployment_env", required = false) Integer deployment_env)
			throws AcumosServiceException {
		
		// If trackingID is provided in the header create a
		// OnboardingNotification object that will be used to update status
		// against that trackingID
		OnboardingNotification onboardingStatus = null;
		onboardingStatus = new OnboardingNotification(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd);
		if (trackingID != null) {
			logger.debug(EELFLoggerDelegate.debugLogger, "Tracking ID: {}", trackingID);
			onboardingStatus.setTrackingId(trackingID);
		} else {
			trackingID = UUID.randomUUID().toString();
			onboardingStatus.setTrackingId(trackingID);
			logger.debug(EELFLoggerDelegate.debugLogger, "Tracking ID: {}", trackingID);
		}
		
		String fileName ="onboardingLog_"+trackingID+".log";
		//setting log filename in ThreadLocal	
		LogBean logBean = new LogBean();
		logBean.setLogPath(lOG_DIR_LOC);
		logBean.setFileName(fileName);
		LogThreadLocal logThread = new LogThreadLocal();
		logThread.set(logBean);
		//create log file to capture logs as artifact
		UtilityFunction.createLogFile();
					
		logger.debug(EELFLoggerDelegate.debugLogger, "Started JWT token validation");
		MLPUser shareUser = null;
		Metadata mData = null;
		String modelName = null;

		try {
			// 'authorization' represents JWT token here...!
			if (authorization == null) {
				logger.error(EELFLoggerDelegate.errorLogger, "Token Not Available...!");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
						"Token Not Available...!");
			}

			if (shareUserName != null) {
				RestPageResponse<MLPUser> user = cdmsClient.findUsersBySearchTerm(shareUserName,
						new RestPageRequest(0, 9));

				List<MLPUser> uList = user.getContent();

				if (uList.isEmpty()) {
					logger.error(EELFLoggerDelegate.errorLogger,
							"User " + shareUserName + " not found: cannot share model; onboarding aborted");
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
							"User " + shareUserName + " not found: cannot share model; onboarding aborted");
				} else {
					shareUser = uList.get(0);
				}
			}
			
			// Call to validate JWT Token.....!
			JsonResponse<Object> valid = validate(authorization, provider);

			boolean isValidToken = valid.getStatus();
			String ownerId = null;
		
			if (isValidToken) {
				logger.debug(EELFLoggerDelegate.debugLogger, "Token validation successful");
				ownerId = valid.getResponseBody().toString();

				if (ownerId == null) {
					logger.error(EELFLoggerDelegate.errorLogger, "Either  username/password is invalid.");
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
							"Either  username/password is invalid.");
				}

				// update userId in onboardingStatus
				if (onboardingStatus != null)
					onboardingStatus.setUserId(ownerId);

				logger.debug(EELFLoggerDelegate.debugLogger, "Onboarding request recieved with "
						+ model.getOriginalFilename());

				// Notify Create solution or get existing solution ID has
				// started
				if (onboardingStatus != null) {
					onboardingStatus.notifyOnboardingStatus("CreateSolution", "ST", "CreateSolution Started");
				}

				modelOriginalName = model.getOriginalFilename();
				String modelId = UtilityFunction.getGUID();
				File outputFolder = new File("tmp", modelId);
				outputFolder.mkdirs();
				boolean isSuccess = false;
				MLPSolution mlpSolution = null;
				try {
					File localmodelFile = new File(outputFolder, model.getOriginalFilename());
					try {
						UtilityFunction.copyFile(model.getInputStream(), localmodelFile);
					} catch (IOException e) {
						logger.error(EELFLoggerDelegate.errorLogger, "Fail to download model file {}",
								localmodelFile.getName());
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
								"Fail to download model file " + localmodelFile.getName());
					}
					File localMetadataFile = new File(outputFolder, metadata.getOriginalFilename());
					try {
						UtilityFunction.copyFile(metadata.getInputStream(), localMetadataFile);
					} catch (IOException e) {
						logger.error(EELFLoggerDelegate.errorLogger, "Fail to download metadata file {}",
								localMetadataFile.getName());
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
								"Fail to download metadata file " + localMetadataFile.getName());
					}
					File localProtobufFile = new File(outputFolder, schema.getOriginalFilename());
					try {
						UtilityFunction.copyFile(schema.getInputStream(), localProtobufFile);
					} catch (IOException e) {
						logger.error(EELFLoggerDelegate.errorLogger, "Fail to download protobuf file {}",
								localProtobufFile.getName());
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
								"Fail to download protobuf file " + localProtobufFile.getName());
					}

					metadataParser = new MetadataParser(localMetadataFile);
					mData = metadataParser.getMetadata();

					mData.setOwnerId(ownerId);

					List<MLPSolution> solList = getExistingSolution(mData);

					boolean isListEmpty = solList.isEmpty();

					if (isListEmpty) {
						mlpSolution = createSolution(mData, onboardingStatus);
						mData.setSolutionId(mlpSolution.getSolutionId());
						logger.debug(EELFLoggerDelegate.debugLogger, "New solution created Successfully " + mlpSolution.getSolutionId());
					} else {
						logger.debug(EELFLoggerDelegate.debugLogger, "Existing solution found for model name " + solList.get(0).getName());
						mlpSolution = solList.get(0);
						mData.setSolutionId(mlpSolution.getSolutionId());
					}

					MLPSolutionRevision revision = createSolutionRevision(mData);
					
					modelName = mData.getModelName() + "_" + mData.getSolutionId();

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

					String actualModelName = getActualModelName(mData, mlpSolution.getSolutionId());  
					// Add artifacts started. Notification will be handed by
					// addArtifact method itself for started/success/failure
					artifactsDetails =  getArtifactsDetails();
					
					addArtifact(mData, localmodelFile, getArtifactTypeCode("Model Image"), mData.getModelName(), onboardingStatus);

					addArtifact(mData, localProtobufFile, getArtifactTypeCode("Model Image"), mData.getModelName(), onboardingStatus);

					addArtifact(mData, localMetadataFile, getArtifactTypeCode("Metadata"), mData.getModelName(), onboardingStatus);

					// Notify TOSCA generation started
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "ST", "TOSCA Generation Started");
					}

					generateTOSCA(localProtobufFile, localMetadataFile, mData, onboardingStatus);

					// Notify TOSCA generation successful
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "SU", "TOSCA Generation Successful");
					}
						
					//call microservice
					logger.debug(EELFLoggerDelegate.debugLogger,
							"Before microservice call Parameters : SolutionId " + mlpSolution.getSolutionId() + " and RevisionId " + revision.getRevisionId());
					try{
					ResponseEntity<ServiceResponse> response = microserviceClient.generateMicroservice(mlpSolution.getSolutionId(),revision.getRevisionId(),provider,authorization,trackingID,modName,deployment_env);
					if (response.getStatusCodeValue() == 200 || response.getStatusCodeValue() == 201) {
						isSuccess = true;
					}
					}catch(Exception e){
						logger.error(EELFLoggerDelegate.errorLogger,"Exception occured while invoking microservice API " +e);
						throw e;
					}
					

					// Model Sharing
					if (isSuccess && (shareUserName != null) && revision.getRevisionId()!= null) {
						try {
							AuthorTransport author = new AuthorTransport(shareUserName, shareUser.getEmail());
							AuthorTransport authors[]= new AuthorTransport[1];
							logger.debug(EELFLoggerDelegate.debugLogger,"Author Name " + author.getName() + " and Email " + author.getContact());
							authors[0]=author;
							revision.setAuthors(authors);
							cdmsClient.updateSolutionRevision(revision);
							logger.debug(EELFLoggerDelegate.debugLogger, "Model Shared Successfully with " + shareUserName);
						} catch (Exception e) {
							isSuccess = false;
							logger.error(EELFLoggerDelegate.errorLogger, " Failed to share Model", e);
							throw e;
						}
					}
					
					ResponseEntity<ServiceResponse> res = new ResponseEntity<ServiceResponse>(ServiceResponse.successResponse(mlpSolution), HttpStatus.CREATED);
					logger.debug(EELFLoggerDelegate.debugLogger, "Onboarding is successful for model name: "+mlpSolution.getName()+", SolutionID: "+   mlpSolution.getSolutionId() +", Status Code: "+ res.getStatusCode());
					return res;
				} finally {

					try {
						UtilityFunction.deleteDirectory(outputFolder);
						if (isSuccess == false) {
							logger.debug(EELFLoggerDelegate.debugLogger,
									"Onboarding Failed, Reverting failed solutions and artifacts.");
							if (metadataParser != null && mData != null) {
								revertbackOnboarding(metadataParser.getMetadata(),
										mlpSolution.getSolutionId());
							}
						}

						// push docker build log into nexus
						
						File file = new java.io.File(OnboardingConstants.lOG_DIR_LOC + File.separator + fileName);
						logger.debug(EELFLoggerDelegate.debugLogger, "Log file length " + file.length(), file.getPath(),
								fileName);
						if (metadataParser != null && mData != null) {
							logger.debug(EELFLoggerDelegate.debugLogger,
									"Adding of log artifacts into nexus started " + fileName);

							addArtifact(mData, file, getArtifactTypeCode(OnboardingConstants.ARTIFACT_TYPE_LOG),
									fileName, onboardingStatus);
							logger.debug(EELFLoggerDelegate.debugLogger, "Artifacts log pushed to nexus successfully",
									fileName);
						}
						
						// delete log file
						UtilityFunction.deleteDirectory(file);
						logThread.unset();
						mData = null;
					} catch (AcumosServiceException e) {
						mData = null;
						logger.error(EELFLoggerDelegate.errorLogger, "RevertbackOnboarding Failed");
						HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
						return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage(), modelName),
								httpCode);
					}
				}
			} else {
				try {
					logger.error(EELFLoggerDelegate.errorLogger, "Either Username/Password is invalid.");
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_TOKEN,
							"Either Username/Password is invalid.");
				} catch (AcumosServiceException e) {
					return new ResponseEntity<ServiceResponse>(
							ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()), HttpStatus.UNAUTHORIZED);
				}
			}

		} catch (AcumosServiceException e) {
			HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error(EELFLoggerDelegate.errorLogger, e.getErrorCode() + "  " + e.getMessage());
			if(e.getErrorCode().equalsIgnoreCase(OnboardingConstants.INVALID_PARAMETER)) {
                httpCode =  HttpStatus.BAD_REQUEST;
            }
			return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage(), modelName),
					httpCode);
		} catch (HttpClientErrorException e) {
			// Handling #401
			if (HttpStatus.UNAUTHORIZED == e.getStatusCode()) {
				logger.debug(EELFLoggerDelegate.debugLogger,
						"Unauthorized User - Either Username/Password is invalid.");
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + e.getStatusCode(), "Unauthorized User", modelName),
						HttpStatus.UNAUTHORIZED);
			} else {
				logger.error(EELFLoggerDelegate.errorLogger, e.getMessage());
				e.printStackTrace();
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + e.getStatusCode(), e.getMessage(),modelName), e.getStatusCode());
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, e.getMessage());
			e.printStackTrace();
			if (e instanceof AcumosServiceException) {
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse(((AcumosServiceException) e).getErrorCode(), e.getMessage(), modelName),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse(AcumosServiceException.ErrorCode.UNKNOWN.name(), e.getMessage(),modelName),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

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
