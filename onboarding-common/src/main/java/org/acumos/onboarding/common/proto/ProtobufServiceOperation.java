/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 - 2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.onboarding.common.proto;

import java.io.Serializable;
import java.util.List;

public class ProtobufServiceOperation implements Serializable {

	private static final long serialVersionUID = -508292588013129426L;
	
	
	private String name;
	private String type;
	private List<String> outputMessageNames;
	private List<String> inputMessageNames;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the outputMessageNames
	 */
	public List<String> getOutputMessageNames() {
		return outputMessageNames;
	}
	/**
	 * @param outputMessageNames the outputMessageNames to set
	 */
	public void setOutputMessageNames(List<String> outputMessageNames) {
		this.outputMessageNames = outputMessageNames;
	}
	/**
	 * @return the inputMessageNames
	 */
	public List<String> getInputMessageNames() {
		return inputMessageNames;
	}
	/**
	 * @param inputMessageNames the inputMessageNames to set
	 */
	public void setInputMessageNames(List<String> inputMessageNames) {
		this.inputMessageNames = inputMessageNames;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(type + " "  + name + "(");
		for(String s : inputMessageNames){
			sb.append(s);
			sb.append(",");
		}
		sb.setLength(sb.length()-1);
		sb.append(") returns (");
		for(String s : outputMessageNames){
			sb.append(s);
			sb.append(",");
		}
		sb.setLength(sb.length()-1);
		sb.append(");\n");
		return sb.toString();
	}
	
	
}
