package org.acumos.onboarding;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.designstudio.toscagenerator.ToscaGeneratorClient;
import org.acumos.onboarding.common.models.OnboardingNotification;
import org.acumos.onboarding.common.proto.Protobuf;
import org.acumos.onboarding.common.proto.ProtobufMessageField;
import org.acumos.onboarding.common.proto.ProtobufServiceOperation;
import org.acumos.onboarding.common.utils.ProtobufUtil;
import org.acumos.onboarding.services.impl.CommonOnboarding;
import org.acumos.onboarding.services.impl.PortalRestClientImpl;
import org.acumos.onboarding.services.impl.ProtobufRevision;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class ProtobufRevisionTest {

	@Mock
	RestTemplate restTemplate;

	@Mock
    CommonDataServiceRestClientImpl cdsClientImpl;

	@Mock
	PortalRestClientImpl client;

	@Mock
	CommonOnboarding commonOnboarding;

	@Mock
	OnboardingNotification onboardingStatus;

	@Mock
	ToscaGeneratorClient toscaClient ;

	@InjectMocks
	ProtobufRevision protoRevision ;

	List<String> versionList = new ArrayList<>();
	String countMajor = "1";
	String countMinor = "0";
	String countIncremental = "0";
	int countA = 0;
	int countB = 0;
	int countC = 0;

	File protoBuf1 = new File(FilePathTest.filePath()+"model.proto");
	File protoBuf2 = new File(FilePathTest.filePath()+"model2.proto");

	Protobuf protoBuff1 = null;
	Protobuf protoBuff2 = null;

	String lastProtoBuffString = null;
	String currentProtoBuffString = null;

	List<ProtobufMessageField> fieldList1 = new ArrayList<>();
	List<ProtobufMessageField> fieldList2 = new ArrayList<>();

	@Test
	public void getFullVersionTest() {

		try {
			Assert.assertNotNull(ProtobufRevision.getFullVersion("1", "0", "0"));
		} catch (Exception e) {
			Assert.fail("Exception occured while getFullVersionTest(): " + e.getMessage());
		}

	}

	@Test
	public void checkMessageParametersTest() {

		String verA = countMajor;
		String verB = countMinor;
		String verC = countIncremental;

		versionList.add(verA);
		versionList.add(verB);
		versionList.add(verC);

		try {
			if (protoBuf1 != null && protoBuf1.exists()) {
				FileInputStream fisProto;
				fisProto = new FileInputStream(protoBuf1);
				lastProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if (protoBuf2 != null && protoBuf2.exists()) {

				FileInputStream fisProto = new FileInputStream(protoBuf2);
				currentProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if ((lastProtoBuffString != null && !lastProtoBuffString.isEmpty())
					&& (currentProtoBuffString != null && !currentProtoBuffString.isEmpty())) {
				protoBuff1 = ProtobufUtil.parseProtobuf(lastProtoBuffString);
				protoBuff2 = ProtobufUtil.parseProtobuf(currentProtoBuffString);
			}

			Assert.assertNotNull(protoRevision.checkMessageParameters(versionList, protoBuff1, protoBuff2, countA,
					countB, countC, countMajor, countMinor, countIncremental));
		} catch (Exception e) {
			Assert.fail("Exception occured while checkMessageParametersTest(): " + e.getMessage());
		}
	}

	@Test
	public void checkServiceParametersTest() {

		String verA = countMajor;
		String verB = countMinor;
		String verC = countIncremental;

		versionList.add(verA);
		versionList.add(verB);
		versionList.add(verC);

		try {
			if (protoBuf1 != null && protoBuf1.exists()) {
				FileInputStream fisProto;
				fisProto = new FileInputStream(protoBuf1);
				lastProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if (protoBuf2 != null && protoBuf2.exists()) {

				FileInputStream fisProto = new FileInputStream(protoBuf2);
				currentProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if ((lastProtoBuffString != null && !lastProtoBuffString.isEmpty())
					&& (currentProtoBuffString != null && !currentProtoBuffString.isEmpty())) {
				protoBuff1 = ProtobufUtil.parseProtobuf(lastProtoBuffString);
				protoBuff2 = ProtobufUtil.parseProtobuf(currentProtoBuffString);
			}

			Assert.assertNotNull(protoRevision.checkServiceParameters(versionList, protoBuff1, protoBuff2, countA,
					countB, countC, countMajor, countMinor, countIncremental));
		} catch (Exception e) {
			Assert.fail("Exception occured while checkServiceParametersTest(): " + e.getMessage());
		}
	}

	@Test
	public void returnVersionWhenEqualNumberOfMessageFieldsTest() {

		try {

			if (protoBuf1 != null && protoBuf1.exists()) {
				FileInputStream fisProto;
				fisProto = new FileInputStream(protoBuf1);
				lastProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if (protoBuf2 != null && protoBuf2.exists()) {

				FileInputStream fisProto = new FileInputStream(protoBuf2);
				currentProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if ((lastProtoBuffString != null && !lastProtoBuffString.isEmpty())
					&& (currentProtoBuffString != null && !currentProtoBuffString.isEmpty())) {
				protoBuff1 = ProtobufUtil.parseProtobuf(lastProtoBuffString);
				protoBuff2 = ProtobufUtil.parseProtobuf(currentProtoBuffString);
			}

			fieldList1 = protoBuff1.getMessages().get(0).getFields();
			fieldList2 = protoBuff2.getMessages().get(0).getFields();

			Assert.assertNotNull(ProtobufRevision.returnVersionWhenEqualNumberOfMessageFields(fieldList1, fieldList2,
					countA, countB, countC, countMajor, countMinor, countIncremental));
		} catch (Exception e) {
			Assert.fail("Exception occured while returnVersionWhenEqualNumberOfMessageFieldsTest(): " + e.getMessage());
		}
	}

	@Test
	public void returnVersionWhenUnEqualNumberOfMessageFieldsTest() {

		try {

			if (protoBuf1 != null && protoBuf1.exists()) {
				FileInputStream fisProto;
				fisProto = new FileInputStream(protoBuf1);
				lastProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if (protoBuf2 != null && protoBuf2.exists()) {

				FileInputStream fisProto = new FileInputStream(protoBuf2);
				currentProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if ((lastProtoBuffString != null && !lastProtoBuffString.isEmpty())
					&& (currentProtoBuffString != null && !currentProtoBuffString.isEmpty())) {
				protoBuff1 = ProtobufUtil.parseProtobuf(lastProtoBuffString);
				protoBuff2 = ProtobufUtil.parseProtobuf(currentProtoBuffString);
			}

			fieldList1 = protoBuff1.getMessages().get(0).getFields();
			fieldList2 = protoBuff2.getMessages().get(0).getFields();

			Assert.assertNotNull(ProtobufRevision.returnVersionWhenUnEqualNumberOfMessageFields(fieldList1, fieldList2,
					countA, countB, countC, countMajor, countMinor, countIncremental));
		} catch (Exception e) {
			Assert.fail("Exception occured while returnVersionWhenUnEqualNumberOfMessageFieldsTest(): " + e.getMessage());
		}
	}

	@Test
	public void returnVersionWhenEqualNumberOfServiceFieldsTest() {

		List<ProtobufServiceOperation> serviceOperationsList1 = new ArrayList<>();
		List<ProtobufServiceOperation> serviceOperationsList2 = new ArrayList<>();

		List<String> serviceFieldList1 = new ArrayList<>();
		List<String> serviceFieldList2 = new ArrayList<>();

		try {

			if (protoBuf1 != null && protoBuf1.exists()) {
				FileInputStream fisProto;
				fisProto = new FileInputStream(protoBuf1);
				lastProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if (protoBuf2 != null && protoBuf2.exists()) {

				FileInputStream fisProto = new FileInputStream(protoBuf2);
				currentProtoBuffString = IOUtils.toString(fisProto, StandardCharsets.UTF_8);
			}

			if ((lastProtoBuffString != null && !lastProtoBuffString.isEmpty())
					&& (currentProtoBuffString != null && !currentProtoBuffString.isEmpty())) {
				protoBuff1 = ProtobufUtil.parseProtobuf(lastProtoBuffString);
				protoBuff2 = ProtobufUtil.parseProtobuf(currentProtoBuffString);
			}

			serviceOperationsList1 = protoBuff1.getService().getOperations().subList(0,
					protoBuff1.getService().getOperations().size());
			serviceOperationsList2 = protoBuff2.getService().getOperations().subList(0,
					protoBuff2.getService().getOperations().size());

			serviceFieldList1 = serviceOperationsList1.get(0).getInputMessageNames();
			serviceFieldList2 = serviceOperationsList2.get(0).getInputMessageNames();

			Assert.assertNotNull(ProtobufRevision.returnVersionWhenEqualNumberOfServiceFields(serviceFieldList1, serviceFieldList1,
					countA, countB, countC, countMajor, countMinor, countIncremental));
		} catch (Exception e) {
			Assert.fail("Exception occured while returnVersionWhenEqualNumberOfServiceFieldsTest(): " + e.getMessage());
		}
	}
}
