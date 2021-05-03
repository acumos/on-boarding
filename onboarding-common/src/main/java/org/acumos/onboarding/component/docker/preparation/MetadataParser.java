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

package org.acumos.onboarding.component.docker.preparation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.acumos.onboarding.common.utils.UtilityFunction;
import org.acumos.onboarding.services.impl.OnboardingController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class MetadataParser {

	private Metadata metadata;

	private JsonNode metadataJson;

	private static Logger log = LoggerFactory.getLogger(OnboardingController.class);
	LoggerDelegate logger = new LoggerDelegate(log);
	
	public MetadataParser(File dataFile) throws AcumosServiceException {
		if(dataFile == null){
			throw new IllegalArgumentException("metadata file must be defined");
		}
		try {

			logger.debug("::Parsing of metadata file started::");
			String schemafile = null;

			this.metadataJson = JsonLoader.fromFile(dataFile);
			JsonNode schameNode = metadataJson.get("schema");

			if(schameNode == null){
				throw new IllegalArgumentException("metadata schema must be defined");
			}
			String schemaVersion = schameNode.asText();
			logger.debug("schemaVersion: " + schemaVersion);

			if (schemaVersion.contains("1")) {
				schemafile = "/model-schema-0.1.0.json";
			} else if (schemaVersion.contains("2")) {
				schemafile = "/model-schema-0.2.0.json";
			} else if (schemaVersion.contains("3")) {
				schemafile = "/model-schema-0.3.0.json";
			} else if (schemaVersion.contains("4")) {
				schemafile = "/model-schema-0.4.0.json";
			} else if (schemaVersion.contains("5")) {
				schemafile = "/model-schema-0.5.0.json";
			} else if (schemaVersion.contains("6")) {
                                schemafile = "/model-schema-0.6.0.json";
			}

			if(schemafile == null){
				throw new IllegalArgumentException("No matching schema found for schemaVersion:" + schemaVersion);
			}

			final JsonNode schema = JsonLoader.fromResource(schemafile);

			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			com.github.fge.jsonschema.main.JsonValidator validator = factory.getValidator();
			ProcessingReport report = validator.validate(schema, this.metadataJson);

			if (!report.isSuccess()) {
				logger.debug(report.toString());
				StringBuilder sb = new StringBuilder();
				for (ProcessingMessage processingMessage : report) {
					if (!processingMessage.getMessage()
							.equals("the following keywords are unknown and will be ignored: [self]"))
						sb.append(processingMessage.getMessage() + "\n");// Collect
																			// all
																			// message
																			// and
																			// throw
																			// an
																			// exception
				}
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
						"Input JSON is not as per schema cause: '" + sb.toString() + "'");
			}

			this.metadata = new Metadata();
			String modeldummy = UtilityFunction.getGUID();
			String modelName;
			if (metadataJson.hasNonNull("name"))
				modeldummy = metadataJson.get("name").asText();

			modelName = modeldummy.replaceAll("\\s", "");

			// validating Model-Name
			if (!modelName.matches("^[a-zA-Z0-9_-]*$")) {
				logger.debug("Invalid Model name [Metadata Parsing]:"+modelName);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
						"Invalid Model Name - " + modelName);
			}

			logger.debug("Model name [Metadata Parsing]:"+modelName);

			int modelNameLength = modelName.length();
			if (modelNameLength <= 100) {
				metadata.setModelName(modelName);
			} else {
				String newModelName = modelName.substring(0,
						Math.min(modelNameLength, 100));
				metadata.setModelName(newModelName);
				logger.warn("[Metadata Parsing] Modified Model name " +newModelName+" due to length more than 100 char : " + modelNameLength);
			}

			if (metadataJson.hasNonNull("modelVersion"))
				metadata.setVersion(metadataJson.get("modelVersion").asText());

			String runtimeName;
			JsonNode requirementsNode = null;
			JsonNode runtimeNode = metadataJson.get("runtime");

			if(runtimeNode.isArray()){

				for(JsonNode trav : runtimeNode){
					runtimeName = trav.get("name").asText().toLowerCase();
					metadata.setRuntimeName(runtimeName);
					metadata.setRuntimeVersion(trav.get("version").asText());

					if (metadataJson.hasNonNull("toolkit"))
						metadata.setToolkit(trav.get("toolkit").asText().toLowerCase());

					if (trav.get("toolkit") != null) {
						metadata.setToolkit(trav.get("toolkit").asText().toLowerCase());
					}

					if (("python").equals(runtimeName)) {
						requirementsNode = trav.get("dependencies").get("pip").get("requirements");
					} else if (("r").equals(runtimeName)) {
						requirementsNode = trav.get("dependencies").get("packages");
					} else if (("h2o").equals(runtimeName)) {
						requirementsNode = trav.get("dependencies").get("java").get("requirements");
					} else if (("javageneric").equals(runtimeName)) {
						requirementsNode = trav.get("dependencies").get("java").get("requirements");
					} else if (("javaspark").equals(runtimeName)) {
						requirementsNode = trav.get("dependencies").get("java").get("requirements");
					} else if (("c++").equals(runtimeName)) {
						requirementsNode = trav.get("dependencies").get("pip").get("requirements");
						metadata.setExecutable(trav.get("executable").asText());
					}
				}
			}
			else {
				runtimeName = runtimeNode.get("name").asText().toLowerCase();
				metadata.setRuntimeName(runtimeName);
				metadata.setRuntimeVersion(runtimeNode.get("version").asText());
	

				if (metadataJson.hasNonNull("toolkit"))
					metadata.setToolkit(runtimeNode.get("toolkit").asText().toLowerCase());

				if (runtimeNode.get("toolkit") != null) {
					metadata.setToolkit(runtimeNode.get("toolkit").asText().toLowerCase());
				}

				if (("python").equals(runtimeName)) {
					requirementsNode = runtimeNode.get("dependencies").get("pip").get("requirements");
				} else if (("r").equals(runtimeName)) {
					requirementsNode = runtimeNode.get("dependencies").get("packages");
				} else if (("h2o").equals(runtimeName)) {
					requirementsNode = runtimeNode.get("dependencies").get("java").get("requirements");
				} else if (("javageneric").equals(runtimeName)) {
					requirementsNode = runtimeNode.get("dependencies").get("java").get("requirements");
				}  else if (("javaspark").equals(runtimeName)) {
					requirementsNode = runtimeNode.get("dependencies").get("java").get("requirements");
				} else if (("c++").equals(runtimeName)) {
					requirementsNode = runtimeNode.get("dependencies").get("pip").get("requirements");
					metadata.setExecutable(runtimeNode.get("executable").asText());
				} 
			}

			if (requirementsNode != null) {

				ArrayList<Requirement> requirements = new ArrayList<>();
				for (JsonNode requirementNode : requirementsNode) {

					Requirement requirement = new Requirement();
					requirement.name = requirementNode.get("name").asText();
					if (requirementNode.hasNonNull("version")) {
						requirement.version = requirementNode.get("version").asText();
						requirement.operator = "==";
					}
					requirements.add(requirement);
				}
				this.metadata.setRequirements(requirements);
			}

		} catch (JsonProcessingException | ProcessingException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, "Invalid input JSON",
					e);
		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
					"Fail to read input JSON", e);
		}
	}

	public JsonNode getMetadataJson() {
		return metadataJson;
	}

	public Metadata getMetadata() {
		return metadata;
	}
}
