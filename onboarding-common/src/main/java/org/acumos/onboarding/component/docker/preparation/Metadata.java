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

import java.util.ArrayList;

import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Metadata {

	private String ownerId;
	private String modelName;
	private String solutionName;
	private String solutionId;
	private String version;
	private String revisionId;
	private String runtimeName;
	private String runtimeVersion;
	private String toolkit;
	private String executable;
	
	

	private static Logger log = LoggerFactory.getLogger(Metadata.class);
	LoggerDelegate logger = new LoggerDelegate(log);
	
	private ArrayList<Requirement> requirements;

	public String getSolutionName() {
		return solutionName;
	}

	public void setModelName(String modelName) {
		this.solutionName = modelName;
		this.modelName = modelName.toLowerCase();		
		logger.debug("Model name :"+modelName);
	}

	public String getModelName() {
		return modelName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String modelVersion) {
		this.version = modelVersion.toLowerCase();

	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String versionId) {
		this.revisionId = versionId;
	}

	public String getRuntimeName() {
		return runtimeName;
	}

	public void setRuntimeName(String runtimeName) {
		this.runtimeName = runtimeName;
	}

	public String getRuntimeVersion() {
		return runtimeVersion;
	}

	public void setRuntimeVersion(String runtimeVersion) {
		this.runtimeVersion = runtimeVersion;
	}

	public String getToolkit() {
		return toolkit;
	}

	public void setToolkit(String toolkit) {
		this.toolkit = toolkit;
	}

	public ArrayList<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(ArrayList<Requirement> requirements) {
		this.requirements = requirements;
	}

	public void setSolutionName(String solutionName) {
		this.solutionName = solutionName;
	}
	
	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

}
