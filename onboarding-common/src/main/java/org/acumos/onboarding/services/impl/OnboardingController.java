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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.models.ServiceResponse;
import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.JsonRequest;
import org.acumos.onboarding.common.utils.JsonResponse;
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
public class OnboardingController extends CommonOnboarding  implements DockerService {
	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(OnboardingController.class);

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
		logger.debug(EELFLoggerDelegate.debugLogger,"Started User Authentication");
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
				logger.debug(EELFLoggerDelegate.debugLogger,"User Authentication Succesful");
				return new ResponseEntity<ServiceResponse>(ServiceResponse.successJWTResponse(token), HttpStatus.OK);
			} else {
				logger.debug(EELFLoggerDelegate.debugLogger,"Either Username/Password is invalid.");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
						"Either Username/Password is invalid.");
			}

		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger,e.getMessage(), e);
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
			@RequestPart(required = true) MultipartFile schema,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestHeader(value = "tracking_id", required = false) String trackingID,
			@RequestHeader(value = "provider", required = false) String provider) throws AcumosServiceException {

		logger.debug(EELFLoggerDelegate.debugLogger,"Started JWT token validation");

		try {
			// 'authorization' represents JWT token here...!
			if (authorization == null) {
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
						"Token Not Available...!");
			}

			// If trackingID is provided in the header create a
			// OnboardingNotification object that will be used to update status
			// against that trackingID
			if (trackingID != null) {
				onboardingStatus = new OnboardingNotification(cmnDataSvcEndPoinURL,cmnDataSvcUser,cmnDataSvcPwd);
				onboardingStatus.setTrackingId(trackingID);
			}
			else{
				
				onboardingStatus=null;
			}

			// Call to validate JWT Token.....!
			JsonResponse<Object> valid = validate(authorization, provider);

			boolean isValidToken = valid.getStatus();

			String ownerId = null;
			String imageUri = null;

			if (isValidToken) {
				logger.debug(EELFLoggerDelegate.debugLogger,"Token validation successful");
				ownerId = valid.getResponseBody().toString();

				if (ownerId == null)
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND,
							"Either  username/password is invalid.");

				// update userId in onboardingStatus
				if (onboardingStatus != null)
					onboardingStatus.setUserId(ownerId);

				logger.debug(EELFLoggerDelegate.debugLogger,"Dockerization request recieved with " + model.getOriginalFilename() + " and metadata :"
						+ metadata);

				// Notify Create solution or get existing solution ID has
				// started
				if (onboardingStatus != null) {
					onboardingStatus.notifyOnboardingStatus("CreateSolution", "ST", "CreateSolution Started");
				}

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
						onboardingStatus.notifyOnboardingStatus("CreateMicroservice", "SU", "CreateSolution Successful");
					}

					// Notify Create docker image has started
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("Dockerize", "ST","Create Docker Image Started");
					}

					try {
						imageUri = dockerizeFile(metadataParser, localmodelFile);
					} catch (Exception e) {
						// Notify Create docker image failed
						if (onboardingStatus != null) {
							onboardingStatus.notifyOnboardingStatus("Dockerize", "FA",
									"Create Docker Image Failed");
						}
						throw e;
					}

					// Notify Create docker image is successful
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("Dockerize", "SU",
								"Created Docker Image Succesful");
					}

					// Add artifacts started. Notification will be handed by
					// addArtifact method itself for started/success/failure
					addArtifact(mData, imageUri, ArtifactTypeCode.DI);

					addArtifact(mData, localmodelFile, ArtifactTypeCode.MI);

					addArtifact(mData, localProtobufFile, ArtifactTypeCode.MI);

					addArtifact(mData, localMetadataFile, ArtifactTypeCode.MD);

					// Notify TOSCA generation started
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "ST", "TOSCA Generation Started");
					}

					generateTOSCA(localProtobufFile, localMetadataFile, mData);

					// Notify TOSCA generation successful
					if (onboardingStatus != null) {
						onboardingStatus.notifyOnboardingStatus("CreateTOSCA", "SU", "TOSCA Generation Successful"); 
					}

					isSuccess = true;

					return new ResponseEntity<ServiceResponse>(ServiceResponse.successResponse(mlpSolution),
							HttpStatus.CREATED);
				} finally {
					if (isSuccess == false) {
						logger.debug(EELFLoggerDelegate.debugLogger,"Onboarding Failed, Reverting failed solutions and artifacts.");
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
			logger.error(EELFLoggerDelegate.errorLogger,e.getErrorCode() + "  " + e.getMessage());
			return new ResponseEntity<ServiceResponse>(ServiceResponse.errorResponse(e.getErrorCode(), e.getMessage()),
					httpCode);
		} catch (HttpClientErrorException e) {
			// Handling #401
			if (HttpStatus.UNAUTHORIZED == e.getStatusCode()) {
				logger.debug(EELFLoggerDelegate.debugLogger,"Unauthorized User - Either Username/Password is invalid.");
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + e.getStatusCode(), "Unauthorized User"),
						HttpStatus.UNAUTHORIZED);
			} else {
				logger.error(EELFLoggerDelegate.errorLogger,e.getMessage());
				e.printStackTrace();
				return new ResponseEntity<ServiceResponse>(
						ServiceResponse.errorResponse("" + e.getStatusCode(), e.getMessage()), e.getStatusCode());
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger,e.getMessage());
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
