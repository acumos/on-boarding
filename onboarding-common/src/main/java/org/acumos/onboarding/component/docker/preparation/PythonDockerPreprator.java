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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.common.utils.UtilityFunction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class PythonDockerPreprator {

	private Metadata metadata;

	private JsonNode metadataJson;

	private String pythonVersion;
	private String pythonhttpProxy;
	
	String extraIndexURL;
	String trustedHost;

	public PythonDockerPreprator(MetadataParser metadataParser, String extraIndexURL, String trustedHost, String httpProxy)
			throws AcumosServiceException {
		this.pythonhttpProxy = httpProxy;
		this.extraIndexURL = extraIndexURL;
		this.trustedHost = trustedHost;

		this.metadata = metadataParser.getMetadata();
		this.metadataJson = metadataParser.getMetadataJson();
		this.pythonVersion=metadata.getRuntimeVersion()+"-slim";
		
	    /*commenting out fixed version of python docker base image*/
		
		/*int[] runtimeVersion = versionAsArray(metadata.getRuntimeVersion());
		this.pythonVersion = Arrays.toString(runtimeVersion);
		
		if (runtimeVersion[0] == 2) {
			int[] baseVersion = new int[] { 2, 7, 13 };
			if (compareVersion(baseVersion, runtimeVersion) >= 0) {

				this.pythonVersion = "2.7-slim";
			} else {
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
						"Unspported python version " + metadata.getRuntimeVersion());
			}
		} else if (runtimeVersion[0] == 3) {
			int[] baseVersion = new int[] { 3, 6, 2 };
			if (compareVersion(baseVersion, runtimeVersion) >= 0) {

				this.pythonVersion = "3.6.2-slim";
			} else {
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
						"Unspported python version " + metadata.getRuntimeVersion());
			}
		} else {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
					"Unspported python version " + metadata.getRuntimeVersion());
		}*/
	}

	public void prepareDockerApp(File outputFolder) throws AcumosServiceException {
		this.prepareYaml(new File(outputFolder, "swagger.yaml"), new File(outputFolder, "swagger.yaml"));
		this.createDockerFile(new File(outputFolder, "Dockerfile"), new File(outputFolder, "Dockerfile"));
		this.createRequirementTxt(new File(outputFolder, "requirements.txt"),
				new File(outputFolder, "requirements.txt"));
	}

	public void prepareDockerAppV2(File outputFolder) throws AcumosServiceException {
		// this.prepareYaml(new File(outputFolder, "swagger.yaml"), new
		// File(outputFolder, "swagger.yaml"));

		this.createDockerFile(new File(outputFolder, "Dockerfile"), new File(outputFolder, "Dockerfile"));
		this.createRequirementTxt(new File(outputFolder, "requirements.txt"),
				new File(outputFolder, "requirements.txt"));
	}

	private JsonNode findPredictMethod() throws AcumosServiceException {
		JsonNode nodes = this.metadataJson.get("methods");
		for (JsonNode node : nodes) {
			String name = node.get("name").asText();
			if (name.equals("predict")) {
				return node;
			}
		}
		throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
				"Method signature for 'predict' not found");
	}

	@SuppressWarnings("deprecation")
	private void prepareYaml(File yamlFileIn, File yamlFileOut) throws AcumosServiceException {

		try {
			YAMLFactory yf = new YAMLFactory();
			ObjectMapper mapper = new ObjectMapper(yf);
			ObjectNode root = (ObjectNode) mapper.readTree(yamlFileIn);
			HashMap<String, JsonNode> infoNode = new HashMap<>();
			infoNode.put("title", JsonNodeFactory.instance.textNode(metadata.getSolutionName()));
			infoNode.put("version", JsonNodeFactory.instance.textNode(metadata.getVersion()));
			((ObjectNode) root.get("info")).putAll(infoNode);

			JsonNode signatureNode = findPredictMethod().get("signature");
			JsonNode parametersNode = signatureNode.get("parameters");
			if (parametersNode.size() < 0 || parametersNode.size() > 1) {
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
						"'predict' method can only have one aragument and provide argument :" + parametersNode.size());
			}
			JsonNode typeNode = parametersNode.get(0).get("type");
			String mediaType = typeNode.get("media").asText();

			if (mediaType.equals("application/json")) {
				JsonNode formatNode = typeNode.get("format");
				JsonNode namesNodes = formatNode.get("names");
				JsonNode typesNodes = formatNode.get("types");
				int counter = 0;
				ObjectNode definitionsNode = (ObjectNode) root.get("definitions").get("FeatureVector");

				ObjectNode itemsNode = JsonNodeFactory.instance.objectNode();
				for (JsonNode pamamTypeNode : typesNodes) {
					String name = namesNodes.has(counter) ? namesNodes.get(counter).asText() : "x" + counter;
					ObjectNode node = JsonNodeFactory.instance.objectNode(); // initializing
					String typeName = pamamTypeNode.asText();
					if (typeName.equals("integer") || typeName.equals("long") || typeName.equals("byte")
							|| typeName.equals("int")) {
						typeName = "integer";
					} else if (typeName.equals("float") || typeName.equals("double")) {
						typeName = "number";
					} else {
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
								"Unsupported data type " + typeName + " for predict");
					}
					node.put("type", typeName);
					itemsNode.put(name, node);
					counter++;

				}
				definitionsNode.put("properties", itemsNode);
			} else {

				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
						"Unsupported media type " + mediaType + " for predict");
			}

			FileOutputStream fos = new FileOutputStream(yamlFileOut);
			SequenceWriter sw = mapper.writerWithDefaultPrettyPrinter().writeValues(fos);
			sw.write(root);
		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to create yamal for input model", e);
		}
	}

	public void createDockerFile(File inDockerFile, File outDockerFile) throws AcumosServiceException {
		try {
			String dockerFileAsString = new String(UtilityFunction.toBytes(inDockerFile));
			dockerFileAsString = MessageFormat.format(dockerFileAsString,
					new Object[] { this.pythonVersion, extraIndexURL, trustedHost,this.pythonhttpProxy});
			FileWriter writer = new FileWriter(outDockerFile);
			try {
				writer.write(dockerFileAsString.trim());
			} finally {
				writer.close();
			}

		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to create dockerFile for input model", e);
		}

	}

	private void createRequirementTxt(File inRequirementFile, File outRequirementFile) throws AcumosServiceException {
		try {
			List<Requirement> requirements1 = getPipRequirements(inRequirementFile);
			Collection<Requirement> finalRequirements = mergePipRequirements(requirements1,
					this.metadata.getRequirements());
			StringBuilder reqAsString = new StringBuilder();
			for (Requirement pipRequirement : finalRequirements) {
				if (pipRequirement.version != null) {
					reqAsString.append(pipRequirement.name + pipRequirement.operator + pipRequirement.version + "\n");
				} else
					reqAsString.append(pipRequirement.name + "\n");
			}
			FileWriter writer = new FileWriter(outRequirementFile);
			try {
				writer.write(reqAsString.toString().trim());
			} finally {
				writer.close();
			}

		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to create requirements.txt for input model", e);
		}

	}

	private static Collection<Requirement> mergePipRequirements(List<Requirement> requirements1,
			List<Requirement> requirements2) {
		Map<String, Requirement> requirements = new LinkedHashMap<>();

		for (Requirement requirement : requirements1) {
			requirements.put(requirement.name, requirement);

		}
		for (Requirement requirement : requirements2) {
			if (requirements.containsKey(requirement.name)) {
				Requirement existing = requirements.get(requirement.name);
				if (existing.version != null) {
					if (requirement.version != null) {
						if (compareVersion(versionAsArray(existing.version), versionAsArray(requirement.version)) < 0) {

							requirements.put(requirement.name, requirement);
						}
					} else {
						requirements.put(requirement.name, requirement);
					}

				}

			} else {
				requirements.put(requirement.name, requirement);
			}

		}
		return requirements.values();
	}

	private static List<Requirement> getPipRequirements(File file) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		try {
			String line = null;
			List<Requirement> requirements = new ArrayList<>();
			while ((line = fileReader.readLine()) != null) {
				Requirement req = new Requirement();
				req.name = line;
				int index = -1;
				index = line.indexOf("==");
				if (index == -1)

					index = line.indexOf(">=");

				if (index != -1) {
					req.name = line.substring(0, index);
					req.operator = line.substring(index, index + 2);
					req.version = line.substring(index + 2);
				}

				requirements.add(req);
			}
			return requirements;
		} finally {
			fileReader.close();
		}
	}

	/**
	 * returns -1 if input version is greater , 0 if equal and 1 if smaller
	 * 
	 * @param baseVersion
	 *            base version
	 * @param currentVersion
	 *            current version
	 * @return -1 if input version is greater , 0 if equal and 1 if smaller
	 */
	public static int compareVersion(int[] baseVersion, int[] currentVersion) {
		int result = 0;

		for (int i = 0; i < baseVersion.length; i++) {
			if (currentVersion.length < i + 1 || baseVersion[i] > currentVersion[i]) {
				result = 1;
				break;
			} else if (baseVersion[i] < currentVersion[i]) {
				result = -1;
				break;
			}
		}
		return result;
	}

	public static int[] versionAsArray(String version) {
		String[] versionArr = version.split("\\.");
		int[] versionIntArr = new int[versionArr.length];
		for (int i = 0; i < versionArr.length; i++) {
			versionIntArr[i] = Integer.parseInt(versionArr[i]);
		}
		return versionIntArr;
	}

}
