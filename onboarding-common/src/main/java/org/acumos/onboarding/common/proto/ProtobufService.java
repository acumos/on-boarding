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
import java.util.ArrayList;
import java.util.List;

public class ProtobufService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6149386852915447136L;
	
	private String name; 
	private List<ProtobufServiceOperation> operations;
	
	public ProtobufService(){
		operations = new ArrayList<ProtobufServiceOperation>();
	}
	/**
	 * @return the name
	 * 		This method returns name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 * 		This method accepts name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the operations
	 * 			This method returns operations	
	 */
	public List<ProtobufServiceOperation> getOperations() {
		return operations;
	}
	/**
	 * @param operations
	 * 			This method accepts operations
	 */
	public void setOperations(List<ProtobufServiceOperation> operations) {
		this.operations = operations;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("service " + name + " {\n");
		for(ProtobufServiceOperation o : operations){
			sb.append(o.toString());
		}
		sb.append("}\n");
		return sb.toString();
	}
	
	
	
}
