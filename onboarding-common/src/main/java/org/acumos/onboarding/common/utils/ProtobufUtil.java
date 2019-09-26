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

package org.acumos.onboarding.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.acumos.onboarding.common.proto.Protobuf;
import org.acumos.onboarding.common.proto.ProtobufMessage;
import org.acumos.onboarding.common.proto.ProtobufMessageField;
import org.acumos.onboarding.common.proto.ProtobufOption;
import org.acumos.onboarding.common.proto.ProtobufService;
import org.acumos.onboarding.common.proto.ProtobufServiceOperation;




public class ProtobufUtil {
	
	/**
	 * This method parset the input protobufStr and construct the org.acumos.collator.vo.Protobuf
	 * @param protobufStr
	 * 	Protobuf definition as a string.
	 * @return 
	 * 	Returns Protobuf
	 */
	public static Protobuf parseProtobuf(String protobufStr) {
		Scanner scanner = new Scanner(protobufStr);
		Protobuf protobuf= new Protobuf();
		boolean serviceBegin = false; 
		boolean serviceDone = false;
		
		boolean messageBegin = false;
		
		StringBuilder serviceStr = null;
		StringBuilder messageStr = null;
		
		while (scanner.hasNextLine()) {
		  String line = scanner.nextLine().trim();
		  
		  if(serviceBegin && !serviceDone){
			  serviceStr.append(line);
			  serviceStr.append("\n");
			  if(line.contains("}")){
				  serviceBegin = false;
				  ProtobufService service = parserService(serviceStr.toString().trim());
				  protobuf.setService(service);
				  serviceDone = true;
			  }
		  } else if(messageBegin) {
			  messageStr.append(line);
			  messageStr.append("\n");
			  if(line.contains("}")){
				  messageBegin = false;
				  ProtobufMessage message = parseMessage(messageStr.toString().trim());
				  protobuf.getMessages().add(message);
			  }
		  } else {
			  if(line.startsWith("service") && !serviceDone){
				   serviceBegin = true; 
				   serviceStr = new StringBuilder();
				   serviceStr.append(line);
				   serviceStr.append("\n");
			  }
			  
			  if(line.startsWith("message")){
				  messageBegin = true;
				  messageStr = new StringBuilder();
				  messageStr.append(line);
				  messageStr.append("\n");
			  }
			  if(line.startsWith("syntax")){
				  String value = line.substring(line.indexOf("=")+1, line.length()-1);
				  protobuf.setSyntax(value.replace("\"", "").trim());
			  }
			  
			  if(line.startsWith("option")){
				  ProtobufOption option = parseOption(line.trim());
				  protobuf.getOptions().add(option);
			  }
			  
		  }
		}
		scanner.close();
		return protobuf;
	}
	
	/**
	 * This method parse the Protobuf message string
	 * 
	 * @param messageStr
	 * 	Protbuf message string
	 * 
	 * @return
	 *   Returns object ProtobufMessage.
	 */
	public static ProtobufMessage parseMessage(String messageStr) {
		Scanner scanner = new Scanner(messageStr);
		ProtobufMessage message = new ProtobufMessage();
		ProtobufMessageField field = null;
		while (scanner.hasNextLine()) {
			  String line = scanner.nextLine().trim();
			  if(line.startsWith("message")){
				  String name = null;
				  line = line.replace("\t", "").replace("message", "");
				  if(line.contains("{")){
					  name = line.substring(0, line.lastIndexOf("{")).trim();
					  if(line.contains(";")){
						  line = line.substring(line.lastIndexOf("{")+1, line.length()).trim();
						  field = parseMessageField(line);
						  message.getFields().add(field);
					  }
				  } else {
					  name = line.trim();
				  }
				  message.setName(name);
			  } else if(line.length() > 1){
				  if(line.indexOf("{") > - 1){
					  line = line.replace("{", "").trim();
				  }
				  if(line.contains("}")){
					  line = line.replace("}", "").trim();
				  }
				  field = parseMessageField(line);
				  message.getFields().add(field);
			  }
		}
		scanner.close();
		return message;
	}

	private static ProtobufMessageField parseMessageField(String line) {
		ProtobufMessageField field = new ProtobufMessageField();
		line = line.replace(";", "").trim();
		
		String[] fields = line.split(" ");
		int size = fields.length;
		if(size == 5){
			field.setRole(fields[0]);
			field.setType(fields[1]);
			field.setName(fields[2]);
			field.setTag(Integer.valueOf(fields[4]));
		} else if( size == 4){
			field.setRole("optional");
			field.setType(fields[0]);
			field.setName(fields[1]);
			field.setTag(Integer.valueOf(fields[3]));
		} else {
			field = null;
		}
		return field;
	}

	private static ProtobufService parserService(String serviceStr) {
		Scanner scanner = new Scanner(serviceStr);
		ProtobufService service = new ProtobufService();
		try{
		while (scanner.hasNextLine()) {
			  String line = scanner.nextLine().trim();
			  if(line.startsWith("service")){
				  String name = line.replace("\t", "").replace("service", "").trim();
				  if(name.contains("{")){
					  name = name.substring(0, name.lastIndexOf("{")).trim();
				  } else {
					  name = name.trim();
				  }
				  service.setName(name);
			  } else if(line.length() > 1){
				  if(line.indexOf("{") > - 1){
					  line = line.replace("{", "").trim();
				  }
				  if(line.contains("}")){
					  line = line.replace("}", "").trim();
				  }
				  ProtobufServiceOperation operation = parseServiceOperation(line);
				  service.getOperations().add(operation);
			  }
		}
		}
		finally {
			scanner.close();
		}
		return service;
	}

	private static ProtobufServiceOperation parseServiceOperation(String line) {
		ProtobufServiceOperation operation = new ProtobufServiceOperation();
		line = line.replace("\t", "").trim();
		line = line.replace(";", "").replace("\t", "").trim();
		String operationType = "";
		String operationName = "";
		String inputParameterString = "";
		String outputParameterString = "";

		String line1 = line.split("returns")[0];
		operationType = line1.split(" ", 2)[0].trim();
		String line2 = line1.split(" ", 2)[1].replace(" ", "").replace("(", "%br%").replace(")", "").trim();
		operationName = line2.split("%br%")[0].trim();
		inputParameterString = line2.split("%br%")[1].trim();
		outputParameterString = line.split("returns")[1].replace("(", "").replace(")", "").trim();
		String[] inputParamArray = inputParameterString.split(",");
		String[] outputParamArray = outputParameterString.split(",");
		int inputParamSize = inputParamArray.length;
		int outputParamSize = outputParamArray.length;
		List<String> inputParamList = new ArrayList<String>();
		List<String> outputParamList = new ArrayList<String>();
		for(int i =0 ; i < inputParamSize ; i++ ){
			inputParamList.add(inputParamArray[i].trim());
		}
		for(int i =0 ; i < outputParamSize ; i++ ){
			outputParamList.add(outputParamArray[i].trim());
		}
		operation.setName(operationName);
		operation.setType(operationType);
		operation.setInputMessageNames(inputParamList);
		operation.setOutputMessageNames(outputParamList);
		return operation;
	}

	private static ProtobufOption parseOption(String line) {
		ProtobufOption option = new ProtobufOption();
		line = line.replace("\t", "").trim();
		line = line.replace("option", "").trim();
		line = line.trim();
		String name = line.substring(0,line.indexOf("=")-1).trim();
		String value = line.substring(line.indexOf("=")+1, line.length());
		option.setName(name.trim());
		option.setValue(value.replace(";", "").replace("\"", "").trim());
		return option;
	}

}
