package org.acumos.onboarding.services;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.acumos.onboarding.common.proto.Protobuf;
import org.acumos.onboarding.common.proto.ProtobufMessage;
import org.acumos.onboarding.common.proto.ProtobufMessageField;
import org.acumos.onboarding.common.proto.ProtobufService;
import org.acumos.onboarding.common.proto.ProtobufServiceOperation;
import org.acumos.onboarding.common.utils.Constants;
import org.acumos.onboarding.common.utils.ProtobufUtil;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protobuf.dynamic.MessageDefinition;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.DynamicMessage;

public class ProtoServiceBase implements ProtoService {

	private String protobufStr;
	
	static private ProtoServiceBase base; 
	
	private Protobuf protobuf; 
	
	private DynamicSchema protobufSchema;
	
	
	public ProtoServiceBase(String protobufStr) throws Exception {
		this.protobufStr = protobufStr;
		processProtobuf();
	}
	
	
	public byte[] convertToProtobufFormat(String operationName, String input) throws Exception {
		byte[] result = null;
		ProtobufService rpc = protobuf.getService();
		List<ProtobufServiceOperation> operations = rpc.getOperations();
		//get operation details for operationName 
		
		String inputmessageName = null; 
		
		for(ProtobufServiceOperation opt : operations) {
			if(opt.getName().equals(operationName)) { 
				inputmessageName = opt.getInputMessageNames().get(0).trim(); 
				break;
			}
		}
		result = convertToProtobufMessage(inputmessageName, input);
		return result;
	}

	@Override
	public byte[] convertToProtobufMessage(String messageName, String input) throws Exception {
		byte[] result = null;
		if (null != messageName && !messageName.equals("") && null != input && !input.trim().equals("")){
			DynamicMessage msg = null;
			msg = constructProtobufMessageData(messageName, input);
			result =  msg.toByteArray();
		}
		return result;
	}

	@Override
	public String readProtobufMessage(String messageName, byte[] input) throws Exception {
		String result = null;
		if (null != messageName && !messageName.equals("") && null != input){
			DynamicMessage msg = null;
			DynamicMessage.Builder msgBuilder = protobufSchema.newMessageBuilder(messageName);
			Descriptor msgDesc = msgBuilder.getDescriptorForType();
			//System.out.println("---> \n\n"+msgDesc.getFields());
			msg = DynamicMessage.parseFrom(msgDesc, input);
			//System.out.println(msg.toString());
			result = msg.toString();
		}
		return result;
	}
	
	public String readProtobufFormat(String operationName, byte[] input) throws Exception {
		String result = null;
		ProtobufService rpc = protobuf.getService();
		List<ProtobufServiceOperation> operations = rpc.getOperations();
		//get operation details for operationName 
		String messageName = null; 
		for(ProtobufServiceOperation opt : operations) {
			if(opt.getName().equals(operationName)) { 
				messageName = opt.getOutputMessageNames().get(0).trim(); 
				break;
			}
		}
		result = readProtobufMessage(messageName, input);
		return result;
	}
	
	
	/**
	 * This method process the protobuf and sets the details.
	 * @param conf
	 * 		This method accepts conf
	 * @throws NullPointerException
	 * 		In case of any exception, this method throws the ServiceException
	 */
	private void processProtobuf() throws Exception {
		try{
			protobuf = ProtobufUtil.parseProtobuf(protobufStr);
			setProbufSchem(protobuf);
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private void setProbufSchem(Protobuf protobuf) throws DescriptorValidationException {
		DynamicSchema.Builder schemaBuilder = DynamicSchema.newBuilder();
		schemaBuilder.setName("DatabrokerSchemaDynamic.proto");
		Map<String, MessageDefinition> msgDefinitions = new HashMap<String,MessageDefinition>();
		List<ProtobufMessage> nestedMessage = new ArrayList<ProtobufMessage>();
		
		MessageDefinition msgDef = null;
		MessageDefinition.Builder builder = null;
		List<ProtobufMessageField> fields = null;
		boolean isNestedMessage = false;
		List<ProtobufMessage> messages = protobuf.getMessages();
		
		for(ProtobufMessage msg : messages){ //add MessageDefinition to msgDefinitions
			if(!msgDefinitions.containsKey(msg.getName())){
				MessageDefinition msgDefintion = constructMessageDefinition(msg, messages, msgDefinitions);
				msgDefinitions.put(msg.getName(), msgDefintion);
			}
		}
		
		/*for(ProtobufMessage msg : nestedMessage){
			builder = MessageDefinition.newBuilder(msg.getName());
			fields = msg.getFields();
			for(ProtobufMessageField f : fields){
				if(Constants.PROTOBUF_DATA_TYPE.contains(f.getType())){
					builder.addField(f.getRole(), f.getType(), f.getName(), f.getTag());
				} else if(f.getType().contains("enum")){
					//TODO : Include Enum 
				} else { 
					builder.addMessageDefinition(getNestedMsgDefinitionFrom(msgDefinitions, f.getType()));
					builder.addField(f.getRole(), f.getType(), f.getName(), f.getTag());
				}
			}
			msgDef = builder.build();
			msgDefinitions.put(msg.getName(), msgDef);
		}*/
		
		
		for(String key : msgDefinitions.keySet()){
			schemaBuilder.addMessageDefinition(msgDefinitions.get(key));
		}
		protobufSchema = schemaBuilder.build();
	}
	
	public DynamicSchema getProtobufSchem() {
		return protobufSchema;
	}
	
	private MessageDefinition constructMessageDefinition(ProtobufMessage msg, List<ProtobufMessage> messages, Map<String, MessageDefinition> msgDefinitions) {
		MessageDefinition.Builder builder = null;
		List<ProtobufMessageField> fields = null;
		
		builder = MessageDefinition.newBuilder(msg.getName());
		fields = msg.getFields();
		for(ProtobufMessageField f : fields){
			if(Constants.PROTOBUF_DATA_TYPE.contains(f.getType())){
				builder.addField(f.getRole(), f.getType(), f.getName(), f.getTag());
			} else if(f.getType().contains("enum")){
				//TODO : Include Enum 
			} else {
				//check if definition is available in msgDefinitions map 
				boolean ispresent = msgDefinitions.containsKey(f.getType());
				MessageDefinition childMsgdefinition = null;
				if(ispresent){
					childMsgdefinition = getNestedMsgDefinitionFrom(msgDefinitions, f.getType());
					
				} else {
					ProtobufMessage childMsg = getProtobufMessagefromList(f.getType(), messages);
					childMsgdefinition = constructMessageDefinition(childMsg, messages, msgDefinitions);
					msgDefinitions.put(f.getType(), childMsgdefinition);
				}
				
				builder.addMessageDefinition(childMsgdefinition);
				builder.addField(f.getRole(), f.getType(), f.getName(), f.getTag());
			}
		}
		return builder.build();
	}
	
	private DynamicMessage constructProtobufMessageData(String msgName, String line) throws CloneNotSupportedException {
		DynamicMessage msg = null;
		ProtobufMessage message = protobuf.getMessage(msgName);
		List<ProtobufMessageField> fields = message.getFields();
		DynamicMessage.Builder msgBuilder = protobufSchema.newMessageBuilder(msgName);
		Descriptor msgDesc = msgBuilder.getDescriptorForType();
		for(ProtobufMessageField f : fields){
			if(f.getRole().equalsIgnoreCase("repeated")){
				List<Object> values = getRepeatedValues(message.getName(),f,line);
				for(Object value : values){
					msgBuilder.addRepeatedField(msgDesc.findFieldByName(f.getName()), value);
				}
			} else {
				msgBuilder.setField(msgDesc.findFieldByName(f.getName()), getValuefor(message.getName(), f, line));
			}
			
		}
		msg = msgBuilder.build();
		return msg;
	}
	
	/**
	 * This method process the data structure  collection, collection, ....
	 * e.g. [1,2,3],[4,5,6],1,2,3 and first two fields might be part of nested structure and might be repeated. 
	 * 
	 * @param messageName
	 * 		This method accepts messageName
	 * @param field
	 * 		This method accepts field
	 * @param line
	 * 		This method accepts line
	 * @return
	 * 		This method returns list of Object
	 * @throws CloneNotSupportedException
	 * 		In case of any exception, this method throws the CloneNotSupportedException
	 */
	private List<Object> getRepeatedValues(String messageName, ProtobufMessageField field, String line) throws CloneNotSupportedException {
		String fieldType = field.getType();
		List<Object> values = new ArrayList<Object>();
		Object value = null;
		int mappedColumn ;
		if(Constants.PROTOBUF_DATA_TYPE.contains(fieldType)){
			mappedColumn = getMappedColumn(messageName, field);
			Object input = getValue(mappedColumn, line); 
			List<Object> inputs = getObjectList(input,fieldType);
			 for(Object o : inputs){
				 value = convertTo(o, fieldType);
				 values.add(value);
			 }
		} else {
			List<Object> inputs = getObjectList(line,fieldType);
			for(Object o : inputs){
				value = constructProtobufMessageData(messageName + "." + field.getType(), o.toString());
				values.add(value);
			}
			
		}
		return values;
	}

	private Object getValuefor(String messageName, ProtobufMessageField field, String line) throws CloneNotSupportedException {
		Object value = null;
		int mappedColumn;
		if(Constants.PROTOBUF_DATA_TYPE.contains(field.getType())){
			mappedColumn = getMappedColumn(messageName, field);
			if (mappedColumn >= 0) {
				value = getValue(mappedColumn, line);
				value = convertTo(value, field.getType());
			}
		} else {
			mappedColumn = field.getTag() -1;
			String compositevalue = getValue(mappedColumn, line).toString();
			value = constructProtobufMessageData(messageName + "." + field.getType(), compositevalue);
		}
		return value;
	}

	
	private List<Object> getObjectList(Object value, String type) {
		List<Object> result = null;
		if(value.getClass().getSimpleName().equals("String")){
			String val = (String) value;
			if(checkBeginEnd(val,"\"","\"")){
				val = val.substring(1, val.length()-1);
			}
			
			if(checkBeginEnd(val,"{","}")){
				val = val.substring(1, val.length()-1);
			} else if(checkBeginEnd(val,"[","]")){
				val = val.substring(1, val.length()-1);
			} else if(checkBeginEnd(val,"(",")")){
				val = val.substring(1, val.length()-1);
			} 
			
			if(Constants.PROTOBUF_DATA_TYPE.contains(type)) {
				Object[] inputs = val.split(",");
				result = Arrays.asList(inputs);
			} else {
				Stack<String> parenthesis = new Stack<String>();
				result = new ArrayList<Object>();
				char[] chars = val.toCharArray();
				int start = 0;
				int end = 0;
				for(int i = 0; i < chars.length ; i ++){
					if(Constants.BEGIN_PARENTHESIS.indexOf(chars[i])  >= 0){ //begin paranthesis, push to stack
						if(parenthesis.isEmpty()){
							start = i;
						}
						parenthesis.push(String.valueOf(chars[i]));
					} else if(Constants.END_PARENTHESIS.indexOf(chars[i])  >= 0){
						parenthesis.pop();
						//if paranthesis stack is empty then get the input string
						if(parenthesis.isEmpty()){
							end = i;
							String v = val.substring(start+1,end);
							result.add(v);
						}
					}
				}
			}
			
		} else if(value.getClass().getSimpleName().equals("ArrayList")){ //ArrayList
			//TODO : Need to be implemented 
		} else if(value.getClass().getSimpleName().contains("[")) { //Array
			//TODO : Need to be implemented 
		}
		
		return result;
	}
	
	private boolean checkBeginEnd(String input, String beginChar, String endChar) {
		boolean resultFlag = false;
		String startwith = String.valueOf(input.charAt(0));
		String endwith = String.valueOf(input.charAt(input.length()-1));
		if(startwith.equals(beginChar) && endwith.equals(endChar)){
			resultFlag = true;
		}
		
		return resultFlag;
	}


	private Object convertTo(Object value, String type) {
		Object result = null;
		String input = (String) value;
		if ("double".equals(type)) {
			result = Double.parseDouble(input);
		} else if ("float".equals(type)) {
			result = Float.parseFloat(input);
		} else if ("int32".equals(type)) {
			result = Integer.parseInt(input);
		} else if ("int64".equals(type)) {
			result = Long.parseLong(input);
		} else if ("unit32".equals(type)) {
			result = Integer.parseInt(input);
		} else if ("unit64".equals(type)) {
			result = Long.parseLong(input);
		} else if ("sint32".equals(type)) {
			result = Integer.parseInt(input);
		} else if ("sint64".equals(type)) {
			result = Long.parseLong(input);
		} else if ("fixed32".equals(type)) {
			result = Integer.parseInt(input);
		} else if ("fixed64".equals(type)) {
			result = Long.parseLong(input);
		} else if ("sfixed32".equals(type)) {
			result = Integer.parseInt(input);
		} else if ("sfixed64".equals(type)) {
			result = Long.parseLong(input);
		} else if ("bool".equals(type)) {
			result = Boolean.parseBoolean(input);
		} else if ("string".equals(type)) {
			result = input;
		} else if ("bytes".equals(type)) {
			result = input.getBytes(StandardCharsets.UTF_8);
		}
		return result;
	}

	
	private Object getValue(int mappedColumn, String line) throws CloneNotSupportedException {
		char splitby = ',';
		Object result = null;
		char[] chars = line.toCharArray();
		Stack<String> parenthesis = new Stack<String>();
		int start = 0;
		int end; 
		String v = "";
		List<String> columns = new ArrayList<String>();
		for(int i =0 ; i < chars.length; i ++){
			if(Constants.BEGIN_PARENTHESIS.indexOf(chars[i])  >= 0){ //begin parenthesis, push to stack
				if(parenthesis.isEmpty()){
					start = i;
				}
				parenthesis.push(String.valueOf(chars[i]));
			} else if(Constants.END_PARENTHESIS.indexOf(chars[i])  >= 0){
				parenthesis.pop();
				if(parenthesis.isEmpty()){
					end = i;
					v = null;
					v = line.substring(start+1,end);
				}
			} else if(chars[i] == splitby) {
				if(parenthesis.isEmpty()){
					columns.add(v.trim());
					v = "";
				}
			} else {
				if(parenthesis.isEmpty()){
					v = v+chars[i];
				}
			}
		}
		columns.add(v);
		if(null != columns && columns.size() > 0 ){
			result = columns.get(mappedColumn);
		}
		return result;
	}


	private int getMappedColumn(String messageName, ProtobufMessageField field) throws CloneNotSupportedException {
		int mappedColumn = -1;
		mappedColumn = field.getTag() - 1;
		return mappedColumn;
	}

	private MessageDefinition getNestedMsgDefinitionFrom(Map<String, MessageDefinition> msgDefinitions, String type) {
		MessageDefinition m = msgDefinitions.get(type);
		return m;
	}
	
	private ProtobufMessage getProtobufMessagefromList(String name, List<ProtobufMessage> messages) { 
		ProtobufMessage result = null;
		for(ProtobufMessage msg : messages){
			if(msg.getName().equals(name)){
				result = msg; 
				break;
			}
		}
		return result;
	}
	
	public Protobuf getProtobuf() {
		return protobuf;
	}


	public void setProtobuf(Protobuf protobuf) {
		this.protobuf = protobuf;
	}


}
