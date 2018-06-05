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


import org.acumos.onboarding.common.config.DockerClientConfiguration;
import org.acumos.onboarding.component.docker.DockerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

@RunWith(MockitoJUnitRunner.class)
public class DockerClientConfigurationTest {
	
    @InjectMocks
	DockerClientConfiguration dockerCConfig;// = new DockerClientConfiguration();
    
    @Mock
    Environment environment;

	@Test
	 public void dockerConfigurationTest() {
		DockerConfiguration config = new DockerConfiguration();
		Mockito.when(environment.getProperty("docker.config", config.getConfig())).thenReturn("test");
		Mockito.when(environment.getProperty("docker.port", String.valueOf(config.getPort()))).thenReturn("test");
		try{
		dockerCConfig.dockerConfiguration();
		}catch(Exception e){
			
		}
	 }
}
