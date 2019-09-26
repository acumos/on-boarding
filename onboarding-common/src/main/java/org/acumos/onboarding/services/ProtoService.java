package org.acumos.onboarding.services;

public interface ProtoService {
	
	/**
	 * This Methods converts the input data into protobuf format of input message type for the operation specified in the endpointURL.
	 * @param operationName 
	 * 		The name of the operation for which data need to converted to.
	 * 
	 * @param input
	 * 		The input data as a comma (,) seperated string or JSON.
	 * @return
	 * 		This method return the byte[] are output. 
	 * 
	 * @throws Exception
	 * 		In case of any error it throws the Exception. 
	 */
	public byte[] convertToProtobufFormat(String operationName, String input) throws Exception;
	
	/**
	 * This Methods converts the input data into protobuf format of input message type for the operation specified in the endpointURL.
	 * @param messageName 
	 * 		The protobuf messageName in which data need to be converted.
	 * 
	 * @param input
	 * 		The input data as a comma (,) seperated string or JSON.
	 * @return
	 * 		This method return the byte[] are output. 
	 * 
	 * @throws Exception
	 * 		In case of any error it throws the Exception. 
	 */
	public byte[] convertToProtobufMessage(String messageName, String input) throws Exception;
	
	
	
	/**
	 * The method converts the protobuf input message, for the operation specified in the endpointURL.
	 * 
	 * @param operationName 
	 * 		The name of the operation for which data need to converted from.
	 * 
	 * @param input
	 * 		The processed input message in protobuf format
	 * @return
	 * 		Returns the string value after converting from protobuf format. 
	 * 
	 * @throws Exception
	 * 		In case of any error throws Excerption.
	 */
	public String readProtobufFormat(String operationName, byte[] input) throws Exception;
	
	/**
	 * The method converts the protobuf input message, for the operation specified in the endpointURL.
	 * 
	 * @param messageName 
	 * 		The protobuf messageName from which data need to be converted from. 
	 * 
	 * @param input
	 * 		The processed input message in protobuf format
	 * @return
	 * 		Returns the string value after converting from protobuf format. 
	 * 
	 * @throws Exception
	 * 		In case of any error throws Excerption.
	 */
	public String readProtobufMessage(String messageName, byte[] input) throws Exception;
	
	
}
