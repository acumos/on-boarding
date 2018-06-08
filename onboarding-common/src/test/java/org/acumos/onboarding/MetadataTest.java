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

package org.acumos.onboarding;

import java.util.ArrayList;
import org.acumos.onboarding.component.docker.preparation.Metadata;
import org.acumos.onboarding.component.docker.preparation.Requirement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetadataTest{
	
	@Test
	public void metaDataTest() {
		
		Requirement requirement = new Requirement();
		System.out.println(requirement.toString());
		Metadata  metaData = new Metadata();
		metaData.setModelName("javageneric");
		metaData.setOwnerId("root");
		ArrayList<Requirement> list = new ArrayList<Requirement>();
		list.add(requirement);
		metaData.setRequirements(list);
		metaData.setRevisionId("1v");
		metaData.setRuntimeName("testrun");
		metaData.setRuntimeVersion("version1.0");
		metaData.setSolutionId("2-crc-edf");
		metaData.setSolutionName("solutionid124");
		metaData.setToolkit("tk1");
		metaData.setVersion("v1.0");
		
		
		metaData.getModelName();
		metaData.getOwnerId();
		metaData.getRequirements();
		metaData.getRevisionId();
		metaData.getRuntimeName();
		metaData.getRuntimeVersion();
		metaData.getSolutionId();
		metaData.getSolutionName();
		metaData.getToolkit();
		metaData.getVersion();
		Assert.assertNotNull(metaData);
	}

}
