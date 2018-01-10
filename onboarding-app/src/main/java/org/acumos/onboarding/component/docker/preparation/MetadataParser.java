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
import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.UtilityFunction;
import org.acumos.onboarding.services.impl.OnboardingController;

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

	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(OnboardingController.class);

	public MetadataParser(File dataFile) throws AcumosServiceException {
		try {

			String schemafile = null;

			this.metadataJson = JsonLoader.fromFile(dataFile);
			String schemaVersion = metadataJson.get("schema").asText();
			System.out.println("schemaVersion: " + schemaVersion);

			/*
			 * String schemaVersion1 = schemaVersion.replace(".", ""); schemafile =
			 * "SCHEMA_FILE"+"_"+schemaVersion1;
			 * 
			 * System.out.println("schemafile: " + schemafile);
			 */
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
			}

			final JsonNode schema = JsonLoader.fromResource(schemafile);

			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			com.github.fge.jsonschema.main.JsonValidator validator = factory.getValidator();
			ProcessingReport report = validator.validate(schema, this.metadataJson);

			if (!report.isSuccess()) {
				logger.info(report.toString());
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
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
						"Invalid Model Name - " + modelName);
			}

			metadata.setModelName(modelName);

			if (metadataJson.hasNonNull("modelVersion"))
				metadata.setVersion(metadataJson.get("modelVersion").asText());

			JsonNode runtimeNode = metadataJson.get("runtime");
			String runtimeName = runtimeNode.get("name").asText().toLowerCase();
			metadata.setRuntimeName(runtimeName);
			metadata.setRuntimeVersion(runtimeNode.get("version").asText());

			if (metadataJson.hasNonNull("toolkit"))
				metadata.setToolkit(runtimeNode.get("toolkit").asText().toLowerCase());

			if (runtimeNode.get("toolkit") != null) {
				metadata.setToolkit(runtimeNode.get("toolkit").asText().toLowerCase());
			}

			JsonNode requirementsNode = null;

			if (("python").equals(runtimeName)) {
				requirementsNode = runtimeNode.get("dependencies").get("pip").get("requirements");
			} else if (("r").equals(runtimeName)) {
				requirementsNode = runtimeNode.get("dependencies").get("packages");
			} else if (("h2o").equals(runtimeName)) {
				requirementsNode = runtimeNode.get("dependencies").get("java").get("requirements");
			} else if (("javageneric").equals(runtimeName)) {
				requirementsNode = runtimeNode.get("dependencies").get("java").get("requirements");
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
