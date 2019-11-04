package org.acumos.onboarding.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.acumos.onboarding.common.proto.MessageFieldNameComparator;
import org.acumos.onboarding.common.proto.MessageNameComparator;
import org.acumos.onboarding.common.proto.Protobuf;
import org.acumos.onboarding.common.proto.ProtobufMessage;
import org.acumos.onboarding.common.proto.ProtobufMessageField;
import org.acumos.onboarding.common.proto.ProtobufServiceOperation;
import org.acumos.onboarding.common.proto.ServiceNameComparator;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufRevision extends CommonOnboarding {

	private static Logger log = LoggerFactory.getLogger(ProtobufRevision.class);
	static LoggerDelegate logger = new LoggerDelegate(log);

	public List<String> checkMessageParameters(List<String> versionList, Protobuf protoBuf1, Protobuf protoBuf2,
			int countA, int countB, int countC, String countMajor, String countMinor, String countIncremental) {

		String version = "";
		int tempVer = 0;

		String verA = versionList.get(0);
		String verB = versionList.get(1);
		String verC = versionList.get(2);

		List<ProtobufMessage> messageList1 = new ArrayList<>();
		List<ProtobufMessage> messageList2 = new ArrayList<>();

		List<ProtobufMessageField> fieldList1 = new ArrayList<>();
		List<ProtobufMessageField> fieldList2 = new ArrayList<>();

		ProtobufMessage protobufMessage1 = null;
		ProtobufMessage protobufMessage2 = null;

		//Check if any of the protobuf is null
		if (protoBuf1.getMessages() != null && protoBuf2.getMessages() != null) {

			messageList1 = protoBuf1.getMessages().subList(0, protoBuf1.getMessages().size());
			messageList2 = protoBuf2.getMessages().subList(0, protoBuf2.getMessages().size());

			//Sort the message lists on the basis of Message Name
			Collections.sort(messageList1, new MessageNameComparator());
			Collections.sort(messageList2, new MessageNameComparator());

			//Check that Message Lists are not empty
			if (!messageList1.isEmpty() && !messageList2.isEmpty()) {

				//Compare the size of the Message Lists
				if (messageList1.size() == messageList2.size()) {

					//processs_i is the loop on Last Protobuf Message list
					process_i: for (int i = 0; i < messageList1.size(); i++) {

						//process_j is the loop on Current Protobuf Message list
						process_j: for (int j = i; j < messageList2.size(); j++) {

							//fetch the individual Message objects
							protobufMessage1 = messageList1.get(i);
							protobufMessage2 = messageList2.get(j);

							//Check that the Message objects are not null
							if (protobufMessage1 != null && protobufMessage2 != null) {

								if (countA == 0) {

									//Check that the names of the Messages are NOT equal
									if (!protobufMessage1.getName().equals(protobufMessage2.getName())) {

										// Since Message Names are not equal. Hence MAJOR CHANGE!!!
										logger.debug("Since Message Names are not equal. Hence MAJOR CHANGE!!!");
										tempVer = Integer.parseInt(verA) + 1;
										verA = new Integer(tempVer).toString();
										tempVer = 0;
										countA = countA + 1;
										verB = "0";
										verC = "0";
										version = getFullVersion(verA, verB, verC);

										versionList.clear();
										versionList.add(verA);
										versionList.add(verB);
										versionList.add(verC);

										return versionList;
									}
								}

								//Check that the Message Names are equal
								if (protobufMessage1.getName().equals(protobufMessage2.getName())) {

									logger.debug(
											"Protobuf Message Names --> " + protobufMessage1.getName() + ", " + protobufMessage2.getName());

									//Fetch the field list from the Message objects
									fieldList1 = protobufMessage1.getFields();
									fieldList2 = protobufMessage2.getFields();

									//Remove the entries from the field list
									fieldList1.removeIf(s -> s == null);
									fieldList2.removeIf(s -> s == null);

									//Sort the field lists by Field Name
									Collections.sort(fieldList1, new MessageFieldNameComparator());
									Collections.sort(fieldList2, new MessageFieldNameComparator());

									//Check that the field list is NOT Empty
									if (!fieldList1.isEmpty() && !fieldList2.isEmpty()) {

										// if the size of message field lists are equal, execute this code
										if (fieldList1.size() == fieldList2.size()) {

											versionList = returnVersionWhenEqualNumberOfMessageFields(fieldList1,
													fieldList2, countA, countB, countC, countMajor,
													 countMinor, countIncremental);

											if (!versionList.get(0).equals(countMajor)) {
												version = getFullVersion(versionList.get(0), versionList.get(1),
														versionList.get(2));
												break process_i;
											} else if (!versionList.get(1).equals(countMinor)) {
												version = getFullVersion(versionList.get(0), versionList.get(1),
														versionList.get(2));
												break process_i;
											} else if (!versionList.get(2).equals(countIncremental)) {
												version = getFullVersion(versionList.get(0), versionList.get(1),
														versionList.get(2));
												break process_i;
											}

										} else {

											// if the size of the Message fields List is not equal

											versionList = returnVersionWhenUnEqualNumberOfMessageFields(fieldList1,
													fieldList2, countA, countB, countC, countMajor,
													 countMinor, countIncremental);

											if (!versionList.get(0).equals(countMajor)) {
												version = getFullVersion(versionList.get(0), versionList.get(1),
														versionList.get(2));
												break process_i;
											} else if (!versionList.get(1).equals(countMinor)) {
												version = getFullVersion(versionList.get(0), versionList.get(1),
														versionList.get(2));
												break process_i;
											} else if (!versionList.get(2).equals(countIncremental)) {
												version = getFullVersion(versionList.get(0), versionList.get(1),
														versionList.get(2));
												break process_i;
											}

										}
									} else {

										// One of the Message Fields List is empty -> So MAJOR CHANGE !!
										logger.debug(
												"One of the Message Fields List is empty -> So MAJOR CHANGE !!");
										if (countA == 0) {
											tempVer = Integer.parseInt(verA) + 1;
											verA = new Integer(tempVer).toString();
											tempVer = 0;
											countA = countA + 1;
											verB = "0";
											verC = "0";
											version = getFullVersion(verA, verB, verC);

											versionList.clear();
											versionList.add(verA);
											versionList.add(verB);
											versionList.add(verC);

											return versionList;
										}

									}
								}
							} else {

								// One of the Message is Null. Hence MAJOR CHANGE !!!
								logger.debug("One of the Message is Null. Hence MAJOR CHANGE !!!");
								if (countA == 0) {
									tempVer = Integer.parseInt(verA) + 1;
									verA = new Integer(tempVer).toString();
									tempVer = 0;
									countA = countA + 1;
									verB = "0";
									verC = "0";
									version = getFullVersion(verA, verB, verC);

									versionList.clear();
									versionList.add(verA);
									versionList.add(verB);
									versionList.add(verC);

									return versionList;
								}

							}
							continue process_i;
						}
					}
				} else {

					// Size of Message Lists are not equal. That means, message has/have been
					// added/removed. Hence MAJOR CHANGE!!!
					logger.debug(
							"Size of Message Lists are not equal. That means, message has/have been added/removed. Hence MAJOR CHANGE!!!");
					tempVer = Integer.parseInt(verA) + 1;
					verA = new Integer(tempVer).toString();
					version = verA + "." + verB + "." + verC;
					tempVer = 0;

					versionList.clear();
					versionList.add(verA);
					versionList.add(verB);
					versionList.add(verC);

				}
			}
		}
		return versionList;
	}

	public List<String> checkServiceParameters(List<String> versionList, Protobuf protoBuf1,
			Protobuf protoBuf2, int countA, int countB, int countC, String countMajor,
			String countMinor, String countIncremental) {

		String verA = versionList.get(0);
		String verB = versionList.get(1);
		String verC = versionList.get(2);

		String version = "";
		int tempVer = 0;

		List<ProtobufServiceOperation> serviceOperationsList1 = new ArrayList<>();
		List<ProtobufServiceOperation> serviceOperationsList2 = new ArrayList<>();

		List<String> serviceInputFieldList1 = new ArrayList<>();
		List<String> serviceInputFieldList2 = new ArrayList<>();

		List<String> serviceOutputFieldList1 = new ArrayList<>();
		List<String> serviceOutputFieldList2 = new ArrayList<>();

		ProtobufServiceOperation protobufService1 = null;
		ProtobufServiceOperation protobufService2 = null;

		if (protoBuf1.getService() != null && protoBuf2.getService() != null) {

			if (protoBuf1.getService().getName().equals(protoBuf2.getService().getName())) {

				if (protoBuf1.getService().getOperations() != null && protoBuf2.getService().getOperations() != null) {

					serviceOperationsList1 = protoBuf1.getService().getOperations().subList(0,
							protoBuf1.getService().getOperations().size());
					serviceOperationsList2 = protoBuf2.getService().getOperations().subList(0,
							protoBuf2.getService().getOperations().size());

					Collections.sort(serviceOperationsList1, new ServiceNameComparator());
					Collections.sort(serviceOperationsList2, new ServiceNameComparator());

					if (!serviceOperationsList1.isEmpty() && !serviceOperationsList2.isEmpty()) {

						if (serviceOperationsList1.size() == serviceOperationsList2.size()) {

							process_m: for (int m = 0; m < serviceOperationsList1.size(); m++) {

								process_n: for (int n = m; n < serviceOperationsList2.size(); n++) {

									protobufService1 = serviceOperationsList1.get(m);
									protobufService2 = serviceOperationsList2.get(n);

									if (protobufService1 != null && protobufService2 != null) {

										if (countA == 0) {

											if (!protobufService1.getName().equals(protobufService2.getName())) {

												// Since Message Names are not equal. Hence MAJOR CHANGE!!!
												logger.debug(
														"Since Service Names are not equal. Hence MAJOR CHANGE!!!");
												tempVer = Integer.parseInt(verA) + 1;
												verA = new Integer(tempVer).toString();
												tempVer = 0;
												countA = countA + 1;
												verB = "0";
												verC = "0";
												version = getFullVersion(verA, verB, verC);
												versionList.clear();
												versionList.add(verA);
												versionList.add(verB);
												versionList.add(verC);

												return versionList;
											}
										}

										if (protobufService1.getName().equals(protobufService2.getName())
												&& protobufService1.getType().equals(protobufService2.getType())) {

											logger.debug("Service Field Name --> " + protobufService1.getName()
													+ ", " + protobufService2.getName());
											logger.debug("Service Field Type --> " + protobufService1.getType()
													+ ", " + protobufService2.getType());

											serviceInputFieldList1 = protobufService1.getInputMessageNames();
											serviceInputFieldList2 = protobufService2.getInputMessageNames();

											serviceInputFieldList1.removeIf(s -> s == null);
											serviceInputFieldList2.removeIf(s -> s == null);

											Collections.sort(serviceInputFieldList1);
											Collections.sort(serviceInputFieldList2);

											if (!serviceInputFieldList1.isEmpty()
													&& !serviceInputFieldList2.isEmpty()) {

												// if the number of service Input fields are equal, execute this
												// code
												if (serviceInputFieldList1.size() == serviceInputFieldList2.size()) {

													versionList = returnVersionWhenEqualNumberOfServiceFields(
															serviceInputFieldList1, serviceInputFieldList2, countA,
															countB, countC, countMajor,
															 countMinor, countIncremental);

													if (!versionList.get(0).equals(countMajor)) {
														version = getFullVersion(versionList.get(0), versionList.get(1),
																versionList.get(2));
														break process_m;
													} else if (!versionList.get(1).equals(countMinor)) {
														version = getFullVersion(versionList.get(0), versionList.get(1),
																versionList.get(2));
														break process_m;
													} else if (!versionList.get(2).equals(countIncremental)) {
														version = getFullVersion(versionList.get(0), versionList.get(1),
																versionList.get(2));
														break process_m;
													}

													if (versionList.get(0).equals(countMajor) && versionList.get(1).equals(countMinor)
															&& versionList.get(2).equals(countIncremental)) {

														if (!serviceOutputFieldList1.isEmpty()
																&& !serviceOutputFieldList2.isEmpty()) {

															// if the number of service Output fields are equal,
															// execute this code
															if (serviceOutputFieldList1
																	.size() == serviceOutputFieldList2.size()) {

																versionList = returnVersionWhenEqualNumberOfServiceFields(
																		serviceOutputFieldList1,
																		serviceOutputFieldList2, countA, countB,
																		countC, countMajor,
																		 countMinor, countIncremental);

																if (!versionList.get(0).equals(countMajor)) {
																	version = getFullVersion(versionList.get(0),
																			versionList.get(1), versionList.get(2));
																	break process_m;
																} else if (!versionList.get(1).equals(countMinor)) {
																	version = getFullVersion(versionList.get(0),
																			versionList.get(1), versionList.get(2));
																	break process_m;
																} else if (!versionList.get(2).equals(countIncremental)) {
																	version = getFullVersion(versionList.get(0),
																			versionList.get(1), versionList.get(2));
																	break process_m;
																}

															} else {

																// if the size of the Service Output Name fields
																// List is not equal
																if (countA == 0) {
																	tempVer = Integer.parseInt(verA) + 1;
																	verA = new Integer(tempVer).toString();
																	tempVer = 0;
																	countA = countA + 1;
																	verB = "0";
																	verC = "0";
																	version = getFullVersion(verA, verB, verC);

																	versionList.clear();
																	versionList.add(verA);
																	versionList.add(verB);
																	versionList.add(verC);

																	return versionList;
																}

															}
														} else if (serviceOutputFieldList1.isEmpty()
																&& serviceOutputFieldList2.isEmpty()) {

															// do nothing

															return versionList;

														} else {

															// One of the Service Output Name Fields List is
															// empty
															// -> So MAJOR CHANGE !!
															logger.debug(
																	"One of the Service Output Name Fields List is empty -> So MAJOR CHANGE !!");
															if (countA == 0) {
																tempVer = Integer.parseInt(verA) + 1;
																verA = new Integer(tempVer).toString();
																tempVer = 0;
																countA = countA + 1;
																verB = "0";
																verC = "0";
																version = getFullVersion(verA, verB, verC);

																versionList.clear();
																versionList.add(verA);
																versionList.add(verB);
																versionList.add(verC);

																return versionList;
															}

														}

													}

												} else {

													// if the size of the Service Input Name fields List is not
													// equal
													if (countA == 0) {
														tempVer = Integer.parseInt(verA) + 1;
														verA = new Integer(tempVer).toString();
														tempVer = 0;
														countA = countA + 1;
														verB = "0";
														verC = "0";
														version = getFullVersion(verA, verB, verC);

														versionList.clear();
														versionList.add(verA);
														versionList.add(verB);
														versionList.add(verC);

														return versionList;
													}

												}
											} else if (serviceInputFieldList1.isEmpty()
													&& serviceInputFieldList2.isEmpty()) {

												// do nothing
												return versionList;

											} else {

												// One of the Service Input Name Fields List is empty -> So
												// MAJOR
												// CHANGE !!
												logger.debug(
														"One of the Service Name Fields List is empty -> So MAJOR CHANGE !!");
												if (countA == 0) {
													tempVer = Integer.parseInt(verA) + 1;
													verA = new Integer(tempVer).toString();
													tempVer = 0;
													countA = countA + 1;
													verB = "0";
													verC = "0";
													version = getFullVersion(verA, verB, verC);
													versionList.clear();
													versionList.add(verA);
													versionList.add(verB);
													versionList.add(verC);

													return versionList;
												}

											}
										} else {

											// Service Input Name/Type is different -> So MAJOR CHANGE !!
											logger.debug(
													"Service Input Name/Type is different -> So MAJOR CHANGE !!");
											if (countA == 0) {
												tempVer = Integer.parseInt(verA) + 1;
												verA = new Integer(tempVer).toString();
												tempVer = 0;
												countA = countA + 1;
												verB = "0";
												verC = "0";
												version = getFullVersion(verA, verB, verC);
												versionList.clear();
												versionList.add(verA);
												versionList.add(verB);
												versionList.add(verC);

												return versionList;
											}

										}
									} else {

										// One of the Service is Null. Hence MAJOR CHANGE !!!
										logger.debug("One of the Service is Null. Hence MAJOR CHANGE !!!");
										if (countA == 0) {
											tempVer = Integer.parseInt(verA) + 1;
											verA = new Integer(tempVer).toString();
											tempVer = 0;
											countA = countA + 1;
											verB = "0";
											verC = "0";
											version = getFullVersion(verA, verB, verC);
											versionList.clear();
											versionList.add(verA);
											versionList.add(verB);
											versionList.add(verC);

											return versionList;
										}

									}
									continue process_m;
								}
							}
						} else {

							// Size of Service Operation Lists are not equal. That means, service has/have
							// been
							// added/removed. Hence MAJOR CHANGE!!!
							logger.debug(
									"Size of Service Operation Lists are not equal. That means, service has/have been added/removed. Hence MAJOR CHANGE!!!");
							tempVer = Integer.parseInt(verA) + 1;
							verA = new Integer(tempVer).toString();
							version = verA + "." + verB + "." + verC;
							tempVer = 0;
							versionList.clear();
							versionList.add(verA);
							versionList.add(verB);
							versionList.add(verC);

							return versionList;
						}
					}
				}
			} else {

				// Name of Services are not equal. That means, service has/have been altered.
				// Hence MAJOR CHANGE!!!
				logger.debug(
						"Name of Services are not equal. That means, service has/have been altered. Hence MAJOR CHANGE!!!");
				tempVer = Integer.parseInt(verA) + 1;
				verA = new Integer(tempVer).toString();
				version = verA + "." + verB + "." + verC;
				tempVer = 0;
				versionList.clear();
				versionList.add(verA);
				versionList.add(verB);
				versionList.add(verC);

				return versionList;
			}
		}
		return versionList;
	}

	//This method returns the Version List when the size of the Field lists in Messages(currently compared) are equal
	public static List<String> returnVersionWhenEqualNumberOfMessageFields(List<ProtobufMessageField> fieldList1,
			List<ProtobufMessageField> fieldList2, int countA, int countB, int countC, String countMajor,
			String countMinor, String countIncremental) {

		ProtobufMessageField protobufMessageField1 = null;
		ProtobufMessageField protobufMessageField2 = null;

		String messageName1 = "";
		String messageName2 = "";

		String messageType1 = "";
		String messageType2 = "";

		String messageRole1 = "";
		String messageRole2 = "";

		String verA = countMajor;
		String verB = countMinor;
		String verC = countIncremental;

		String version = "";
		int tempVer = 0;

		List<String> versionList = new ArrayList<>();

		logger.debug("No. of Protobuf Messages are equal");

		//process_k iterates over field list of last protobuf Message object
		process_k: for (int k = 0; k < fieldList1.size(); k++) {

			//process_l iterates over field list of current protobuf Message object
			process_l: for (int l = k; l < fieldList2.size(); l++) {

				//Fetch the Message Field Objects
				protobufMessageField1 = fieldList1.get(k);
				protobufMessageField2 = fieldList2.get(l);

				//Check that the  Message Field Objects are NOT null
				if (protobufMessageField1 != null && protobufMessageField2 != null) {

					//Get Name of the Message Field
					messageName1 = protobufMessageField1.getName();
					messageName2 = protobufMessageField2.getName();

					//Get Type of the Message Field
					messageType1 = protobufMessageField1.getType();
					messageType2 = protobufMessageField2.getType();

					//If eithe the Name Or the Type is not equal, execute this block
					if (!(messageName1.equals(messageName2)) || !((messageType1.equals(messageType2)))) {

						// Since Message Field Names/Types are not equal. That means, new field has been
						// added. Hence MAJOR CHANGE!!!
						logger.debug(
								"Since Message Field Names OR Types are not equal. That means, field has been altered. Hence MAJOR CHANGE!!!");
						if (countA == 0) {
							tempVer = Integer.parseInt(verA) + 1;
							verA = new Integer(tempVer).toString();
							tempVer = 0;
							countA = countA + 1;
							verB = "0";
							verC = "0";
							break process_k;
						}

					} else {

						//If Message Field Names/types are equal, execute this block

						logger.debug("Message Names = " + messageName1 + ", " + messageName2);
						logger.debug("Message Types = " + messageType1 + ", " + messageType2);

						//Fetch the Message Field Roles
						messageRole1 = protobufMessageField1.getRole();
						messageRole2 = protobufMessageField2.getRole();

						//Check that the Message Roles are equal
						if (messageRole1.equals(messageRole2)) {

							logger.debug("Message Roles = " + messageRole1 + ", " + messageRole2);

							version = verA + "." + verB + "." + verC;

							//If Message Roles are also equal, continue to the comparison of next Message fields
							continue process_k;

						} else {

							// Since the Role of an existing field has been changed. ANALYSE HERE!!!

							//If Message Role is "optional", its an Incremental change
							if (messageRole2.startsWith("optional")) {
								logger.debug(
										"Since the Role of an existing field has been changed to 'optional' . Hence INCREMENTAL CHANGE!!!");
								if (countA == 0 && countB == 0 && countC == 0) {
									tempVer = Integer.parseInt(verC) + 1;
									verC = new Integer(tempVer).toString();
									tempVer = 0;
									countC = countC + 1;
								}
								continue process_k;
							} else {
								//If Message Role is not "optional", its a MINOR change
								logger.debug(
										"Since the Role of an existing field has been changed, NOT to 'optional', but to something else. Hence MINOR CHANGE!!!");
								if (countA == 0 && countB == 0) {
									tempVer = Integer.parseInt(verB) + 1;
									verB = new Integer(tempVer).toString();
									tempVer = 0;
									verC = "0";
									countB = countB + 1;
									continue process_k;
								}
							}
						}
					}
				}
			}
		}

		version = getFullVersion(verA, verB, verC);

		versionList.add(verA);
		versionList.add(verB);
		versionList.add(verC);

		return versionList;

	}

	//This method returns the Version List when the size of the Field lists in Messages(currently compared) are NOT equal
	public static List<String> returnVersionWhenUnEqualNumberOfMessageFields(List<ProtobufMessageField> fieldList1,
			List<ProtobufMessageField> fieldList2, int countA, int countB, int countC, String countMajor,
			String countMinor, String countIncremental) {

		String verA = countMajor;
		String verB = countMinor;
		String verC = countIncremental;

		String version = "";
		int tempVer = 0;

		List<ProtobufMessageField> fieldListTemp1 = new ArrayList<>();
		List<ProtobufMessageField> fieldListTemp2 = new ArrayList<>();

		List<String> fieldNameList1 = new ArrayList<>();
		List<String> fieldNameList2 = new ArrayList<>();

		List<String> versionList = new ArrayList<>();

		logger.debug("No. of Message Fields in Proto Files are NOT equal");

		// Fill a list with Message Field Names 1. This list (fieldNameList1) contain
		// String names of the Message Fields
		for (ProtobufMessageField str1 : fieldList1) {
			fieldNameList1.add(str1.getName());
		}

		// Fill a list with Message Field Names 2. This list (fieldNameList2) contain
		// String names of the Message Fields
		for (ProtobufMessageField str2 : fieldList2) {
			fieldNameList2.add(str2.getName());
		}

		int fieldList1Size = fieldList1.size();
		int fieldList2Size = fieldList2.size();

		// If Message Fields List1 size is greater than Message Fields List2 size
		if (fieldList1Size > fieldList2Size) {

			// It contains Message Fields
			fieldListTemp1 = fieldList1;

			//If current protobuf Message field List2 contains Message fields of the same Name as that in Message field1,
			// add that field object to fieldListTemp2
			for (ProtobufMessageField pmf1 : fieldList1) {
				if (fieldNameList2.contains(pmf1.getName())) {
					fieldListTemp2.add(pmf1);
				}
			}

			//Remove all the common Message field objects from fieldListTemp1.
			//Now fieldListTemp1 will contain Message fields which have been removed in Message Object of Current Protobuf.
			fieldListTemp1.removeAll(fieldListTemp2);

			// If no name is found common, then MAJOR Change
			if (!fieldListTemp1.isEmpty()) {

				if (countA == 0) {
					tempVer = Integer.parseInt(verA) + 1;
					verA = new Integer(tempVer).toString();
					tempVer = 0;
					verB = "0";
					verC = "0";
					countA = countA + 1;
					version = getFullVersion(verA, verB, verC);
				}
			}

			versionList.add(verA);
			versionList.add(verB);
			versionList.add(verC);

		} else {

			// If Message Fields List1 size is less than Message Fields List2.
			//This means, extra fields are added in the Message Object in the Current Protobuf
			if (fieldList1Size < fieldList2Size) {

				// It contains Messages Fields
				fieldListTemp2 = fieldList2;

				//If Last protobuf Message field List1 contains Message fields of the same Name as that in Message field2,
				// add that field object to fieldListTemp1
				for (ProtobufMessageField pmf2 : fieldList2) {
					if (fieldNameList1.contains(pmf2.getName())) {
						fieldListTemp1.add(pmf2);
					}
				}

				// Here, given priority to already present message fields to decide the version.
				versionList = returnVersionWhenEqualNumberOfMessageFields(fieldList1, fieldListTemp1, countA, countB,
						countC, countMajor, countMinor, countIncremental);

				if (versionList.get(0).equals(countMajor) && versionList.get(1).equals(countMinor)
						&& versionList.get(2).equals(countIncremental)) {

					// Clear the version List
					versionList.clear();

					//Remove all the common field objects from fieldListTemp2
					fieldListTemp2.removeAll(fieldListTemp1);

					// If no name is found common, then MAJOR Change
					if (fieldListTemp2.isEmpty()) {

						if (countA == 0) {

							logger.debug("Since no Message field is common. It's a MAJOR CHANGE!!");
							tempVer = Integer.parseInt(verA) + 1;
							verA = new Integer(tempVer).toString();
							tempVer = 0;
							verB = "0";
							verC = "0";
							countA = countA + 1;
							version = getFullVersion(verA, verB, verC);

						}

						versionList.add(verA);
						versionList.add(verB);
						versionList.add(verC);

					} else {

						// Now analyse the uncommon fields

						for (ProtobufMessageField pmfTemp2 : fieldListTemp2) {

							//If role of the uncommon field is "optional", then Incremental Change
							if (pmfTemp2.getRole().equals("optional")) {
								if (countB == 0 && countB == 0 && countC == 0) {
									tempVer = Integer.parseInt(verC) + 1;
									verC = new Integer(tempVer).toString();
									tempVer = 0;
									countC = countC + 1;
									version = getFullVersion(verA, verB, verC);
								}
							} else {
								//If role of the uncommon field is NOT "optional", then MINOR Change
								if (countA == 0 && countB == 0) {
									tempVer = Integer.parseInt(verB) + 1;
									verB = new Integer(tempVer).toString();
									tempVer = 0;
									verC = "0";
									countB = countB + 1;
									version = getFullVersion(verA, verB, verC);
								}
							}
						}
					}
					versionList.add(verA);
					versionList.add(verB);
					versionList.add(verC);
				}
			}
		}

		return versionList;
	}

	//This method returns Version List when Number of Service fields in both Protobuf are equal
	public static List<String> returnVersionWhenEqualNumberOfServiceFields(List<String> serviceFieldList1,
			List<String> serviceFieldList2, int countA, int countB, int countC, String countMajor,
			String countMinor, String countIncremental) {

		String verA = countMajor;
		String verB = countMinor;
		String verC = countIncremental;

		String version = "";
		int tempVer = 0;

		List<String> versionList = new ArrayList<>();

		logger.debug("No. of Fields in Protobuf message are equal");

		if(!serviceFieldList2.containsAll(serviceFieldList1)) {

			if (countA == 0) {
				tempVer = Integer.parseInt(verA) + 1;
				verA = new Integer(tempVer).toString();
				tempVer = 0;
				verB = "0";
				verC = "0";
				countA = countA + 1;
			}

		}

		version = getFullVersion(verA, verB, verC);

		versionList.add(verA);
		versionList.add(verB);
		versionList.add(verC);

		return versionList;

	}

	public static String getFullVersion(String verA, String verB, String verC) {
		return verA +"."+ verB +"."+ verC ;
	}
}
