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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPUser;
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
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

	@SuppressWarnings("unchecked")
	@Override
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Onboarding DCAE Models", response = ServiceResponse.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Something bad happened", response = ServiceResponse.class),
			@ApiResponse(code = 400, message = "Invalid request", response = ServiceResponse.class) })
	@RequestMapping(value = "/dcae_models", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<ServiceResponse> onboardingWithDCAE(HttpServletRequest request,
			@RequestParam(required = false) String modName, String solutioId, String revisionId,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "tracking_id", required = false) String trackingID,
			@RequestHeader(value = "provider", required = false) String provider,
			@RequestHeader(value = "shareUserName", required = false) String shareUserName)
			throws AcumosServiceException {
		logger.debug(EELFLoggerDelegate.debugLogger, "Started DCAE Onboarding");

		logger.info("Fetching model from Nexus...!");

		String artifactName = null;
		File files = null;
		dcaeflag = true;
		Metadata mData = null;
		OnboardingNotification onboardingStatus = null;
		try {
			
			if (trackingID != null) {
				onboardingStatus = new OnboardingNotification(cmnDataSvcEndPoinURL, cmnDataSvcUser, cmnDataSvcPwd);
				onboardingStatus.setTrackingId(trackingID);
				logger.debug(EELFLoggerDelegate.debugLogger, "Tracking ID: {}", trackingID);
			} else {

				onboardingStatus = null;
			}
            
			/* Nexus Integration....! */

			DownloadModelArtifacts download = new DownloadModelArtifacts();
			artifactName = download.getModelArtifacts(solutioId, revisionId, cmnDataSvcUser, cmnDataSvcPwd,
					nexusEndPointURL, nexusUserName, nexusPassword, cmnDataSvcEndPoinURL);

			if (artifactName.indexOf(".") > 0)
				artifactName = artifactName.substring(0, artifactName.lastIndexOf("."));

			logger.info("Invoking Onboarding API");

			files = new File("dcae_model");

			MultipartFile model = null, meta = null, proto = null;

			File modelFile = new File(files, artifactName + ".zip");
			File MetaFile = new File(files, artifactName + ".json");
			File protoFile = new File(files, artifactName + ".proto");

			if (modName != null) {
				Object obj = new JSONParser().parse(new FileReader(MetaFile));
				JSONObject jo = (JSONObject) obj;
				jo.put("name", modName);
				String jsonFile = jo.toString();
				FileOutputStream fout = new FileOutputStream(MetaFile);
				fout.write(jsonFile.getBytes());
				fout.close();
			}

			if ((modelFile.exists()) && (MetaFile.exists()) && (protoFile.exists())) {
				metadataParser = new MetadataParser(MetaFile);
				mData = metadataParser.getMetadata();

				String runTime = mData.getRuntimeName();

				if (!runTime.equals("python")) {
					logger.error(EELFLoggerDelegate.errorLogger, "Invalid Runtime [Only 'python' runtime allowed..!]");
					throw new AcumosServiceException("Invalid Runtime [Only 'python' runtime allowed..!]");
				}

				FileInputStream fisModel = new FileInputStream(modelFile);
				model = new MockMultipartFile("Model", modelFile.getName(), "", fisModel);

				FileInputStream fisMeta = new FileInputStream(MetaFile);
				meta = new MockMultipartFile("Metadata", MetaFile.getName(), "", fisMeta);

				FileInputStream fisProto = new FileInputStream(protoFile);
				proto = new MockMultipartFile("Proto", protoFile.getName(), "", fisProto);

				return dockerizePayload(request, model, meta, proto, authorization, trackingID, provider,
						shareUserName);

			} else {
				logger.error(EELFLoggerDelegate.errorLogger, "Model artifacts not available..!");
				throw new AcumosServiceException("Model artifacts not available..!");
			}
		} catch (IOException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Unable to read Model artifacts..!");
			throw new AcumosServiceException("Unable to read Model artifacts..!");
		} catch (AcumosServiceException e) {
			HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error(EELFLoggerDelegate.errorLogger, e.getErrorCode() + "  " + e.getMessage());
			return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()),
					httpCode);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Unable to read Model artifacts..!");
			throw new AcumosServiceException("Unable to read Model artifacts..!");
		} finally {
			UtilityFunction.deleteDirectory(files);
		}
	}

	@Override
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Upload model file and its meta data as string to dockerize", response = ServiceResponse.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Something bad happened", response = ServiceResponse.class),
			@ApiResponse(code = 400, message = "Invalid request", response = ServiceResponse.class) })
	@RequestMapping(value = "/models", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<ServiceResponse> dockerizePayload(HttpServletRequest request,
			@RequestPart(required = true) MultipartFile model, @RequestPart(required = true) MultipartFile metadata,
			@RequestPart(required = true) MultipartFile schema,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "tracking_id", required = false) String trackingID,
			@RequestHeader(value = "provider", required = false) String provider,
			@RequestHeader(value = "shareUserName", required = false) String shareUserName)
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
		
		String fileName =trackingID+".log";
		//setting log filename in ThreadLocal	
		LogBean logBean = new LogBean();
		logBean.setFileName(fileName);
		LogThreadLocal logThread = new LogThreadLocal();
		logThread.set(logBean);
		//create log file to capture logs as artifact
		UtilityFunction.createLogFile();
					
		logger.debug(EELFLoggerDelegate.debugLogger, "Started JWT token validation");
		MLPUser shareUser = null;
		Metadata mData = null;
		

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
			String imageUri = null;

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

				logger.debug(EELFLoggerDelegate.debugLogger, "Dockerization request recieved with "
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

					createSolutionRevision(mData);

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
						onboardingStatus.notifyOnboardingStatus("CreateMicroservice", "SU",
								"CreateSolution Successful");
					}

					// Notify Create docker image has started
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("Dockerize", "ST", "Create Docker Image Started for solution "+mData.getSolutionId());
					}

					try {
						imageUri = dockerizeFile(metadataParser, localmodelFile, mlpSolution.getSolutionId());
					} catch (Exception e) {
						// Notify Create docker image failed
						if (onboardingStatus != null) {
							onboardingStatus.notifyOnboardingStatus("Dockerize", "FA", e.getMessage());
						}
						logger.error(EELFLoggerDelegate.errorLogger, "Error {}", e);
						throw e;
					}

					// Notify Create docker image is successful
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("Dockerize", "SU", "Created Docker Image Successfully for solution "+mData.getSolutionId());
					}
					String actualModelName = getActualModelName(mData, mlpSolution.getSolutionId());  
					// Add artifacts started. Notification will be handed by
					// addArtifact method itself for started/success/failure
					artifactsDetails =  getArtifactsDetails();
					addArtifact(mData, imageUri, getArtifactTypeCode("Docker Image"), onboardingStatus);

					addArtifact(mData, localmodelFile, getArtifactTypeCode("Model Image"), actualModelName, onboardingStatus);

					addArtifact(mData, localProtobufFile, getArtifactTypeCode("Model Image"), actualModelName, onboardingStatus);

					addArtifact(mData, localMetadataFile, getArtifactTypeCode("Metadata"), actualModelName, onboardingStatus);

					if (dcaeflag) {
						addDCAEArrtifacts(mData, outputFolder, mlpSolution.getSolutionId(), onboardingStatus);
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

					isSuccess = true;

					// Model Sharing
					if (isSuccess && (shareUserName != null)) {
						try {
							cdmsClient.addSolutionUserAccess(mlpSolution.getSolutionId(), shareUser.getUserId());
							logger.debug("Model Shared Successfully with " + shareUserName);
						} catch (Exception e) {
							logger.error(EELFLoggerDelegate.errorLogger, " Failed to share Model");
							logger.error(EELFLoggerDelegate.errorLogger, "  " + e);
							throw e;
						}
					}
					
					ResponseEntity<ServiceResponse> res = new ResponseEntity<ServiceResponse>(ServiceResponse.successResponse(mlpSolution), HttpStatus.CREATED);
					logger.debug("Onboarding is successful for model name: "+mlpSolution.getName()+", SolutionID: "+   mlpSolution.getSolutionId() +", Status Code: "+ res.getStatusCode());
					return res;
				} finally {

					try {
						UtilityFunction.deleteDirectory(outputFolder);
						if (isSuccess == false) {
							logger.debug(EELFLoggerDelegate.debugLogger,
									"Onboarding Failed, Reverting failed solutions and artifacts.");
							if (metadataParser != null && mData != null) {
								revertbackOnboarding(metadataParser.getMetadata(), imageUri,
										mlpSolution.getSolutionId());
							}
						}

						dcaeflag = false;
						// push docker build log into nexus
						File file = new java.io.File(OnboardingConstants.lOG_DIR_LOC + File.separator + fileName);
						logger.debug(EELFLoggerDelegate.debugLogger, "Log file length " + file.length(), file.getPath(),
								fileName);
						if (metadataParser != null && mData != null) {
							logger.debug(EELFLoggerDelegate.debugLogger,
									"Adding of log artifacts into nexus started " + fileName);

							addArtifact(mData, file, getArtifactTypeCode(OnboardingConstants.ARTIFACT_TYPE_LOG),
									getActualModelName(mData, mlpSolution.getSolutionId()), onboardingStatus);
							logger.debug(EELFLoggerDelegate.debugLogger, "Artifacts log pushed to nexus successfully",
									fileName);
							// info as log file not available to write
							logger.info("Artifacts log file deleted successfully", fileName);
						}
						
						// delete log file
						UtilityFunction.deleteDirectory(file);
						logThread.unset();
						mData = null;
					} catch (AcumosServiceException e) {
						mData = null;
						dcaeflag = false;
						logger.error(EELFLoggerDelegate.errorLogger, "RevertbackOnboarding Failed");
						HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
						return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()),
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
			return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()),
					httpCode);
		} catch (HttpClientErrorException e) {
			// Handling #401
			if (HttpStatus.UNAUTHORIZED == e.getStatusCode()) {
				logger.debug(EELFLoggerDelegate.debugLogger,
						"Unauthorized User - Either Username/Password is invalid.");
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + e.getStatusCode(), "Unauthorized User"),
						HttpStatus.UNAUTHORIZED);
			} else {
				logger.error(EELFLoggerDelegate.errorLogger, e.getMessage());
				e.printStackTrace();
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + e.getStatusCode(), e.getMessage()), e.getStatusCode());
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, e.getMessage());
			e.printStackTrace();
			if (e instanceof AcumosServiceException) {
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse(((AcumosServiceException) e).getErrorCode(), e.getMessage()),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse(AcumosServiceException.ErrorCode.UNKNOWN.name(), e.getMessage()),
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

	private void addDCAEArrtifacts(Metadata mData, File outputFolder, String solutionID, OnboardingNotification onboardingStatus) {

		File filePathoutputF = new File(outputFolder, "app");

		File anoIn = new File(filePathoutputF, "anomaly-in.json");
		File anoOut = new File(filePathoutputF, "anomaly-out.json");
		File compo = new File(filePathoutputF, "component.json");
		File ons = new File(filePathoutputF, "onsdemo1.yaml");

		try {
			addArtifact(mData, anoIn, getArtifactTypeCode("Metadata"), solutionID, onboardingStatus);
			addArtifact(mData, anoOut, getArtifactTypeCode("Metadata"), solutionID, onboardingStatus);
			addArtifact(mData, compo, getArtifactTypeCode("Metadata"), solutionID, onboardingStatus);
			addArtifact(mData, ons, getArtifactTypeCode("Metadata"), solutionID, onboardingStatus);
		}

		catch (AcumosServiceException e) {
			e.printStackTrace();
		}
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
