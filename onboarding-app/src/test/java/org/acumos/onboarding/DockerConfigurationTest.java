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

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.acumos.onboarding.component.docker.DockerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DockerConfigurationTest {
	
	DockerConfiguration dockerConfiguration = new DockerConfiguration();
	
	@Test 
	public void dockerConfigurationTest1() {
		//#docker.config=/docker_host/.docker
		dockerConfiguration.setConfig("/docker_host/.docker");
		String apiVersion = dockerConfiguration.getApiVersion();
		dockerConfiguration.getHost();
		dockerConfiguration.getImagetagPrefix();
		dockerConfiguration.getPort();
		dockerConfiguration.getRegistryUrl();
		dockerConfiguration.setCertPath("testcert");
		dockerConfiguration.setImagetagPrefix("nx");
		dockerConfiguration.setRegistryEmail("test@email.com");
		dockerConfiguration.setRegistryPassword("password");
		dockerConfiguration.setRegistryUsername("username");
		dockerConfiguration.setCmdExecFactory("com.github.dockerjava.netty.NettyDockerCmdExecFactory");
		dockerConfiguration.setMaxPerRouteConnections(10);
		dockerConfiguration.setTlsVerify(false);
		dockerConfiguration.setRegistryUrl("https://index.docker.io/v1/");
		dockerConfiguration.setPort(8080);
		dockerConfiguration.setRequestTimeout(10);
		dockerConfiguration.setSocket(true);
		boolean issocket = dockerConfiguration.isSocket();
		dockerConfiguration.getMaxTotalConnections();
		dockerConfiguration.setMaxTotalConnections(20);
		dockerConfiguration.getRequestTimeout();
		dockerConfiguration.setRequestTimeout(10);
		dockerConfiguration.getCertPath();
		dockerConfiguration.getImagetagPrefix();
		dockerConfiguration.getRegistryEmail();
		dockerConfiguration.getRegistryPassword();
		dockerConfiguration.getRegistryUsername();
		dockerConfiguration.getCmdExecFactory();
		dockerConfiguration.getMaxPerRouteConnections();
		dockerConfiguration.isTlsVerify();
		assert(true);
	}
	
	@Test
	public void dockerConfigurationTest2() {
		try {
			dockerConfiguration.toUrl();
			assert(true);
			dockerConfiguration.setHost(null);
			dockerConfiguration.toUrl();
			dockerConfiguration.setPort(null);
			dockerConfiguration.toUrl();
			
			
		} catch (AcumosServiceException e) {
			assert(true);
		}
	}
	
	
	
	

}
