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
package org.acumos.onboarding.component.docker.cmd;

import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.acumos.onboarding.common.utils.UtilityFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;

@Component
public class ApplicationStartupPullImage 
		implements ApplicationListener<ApplicationReadyEvent> {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ApplicationStartupPullImage.class);

	@Value("${docker.host}")
	protected String host;

	@Value("${docker.port}")
	protected String port;

	@Value("${base_image.rimage}")
	protected String rimageName;

	@Value("${base_image.dockerusername}")
	protected String dockerusername;

	@Value("${base_image.dockerpassword}")
	protected String dockerpassword;

	/**
	 * This event is executed as late as conceivably possible to indicate that
	 * the application is ready to service requests.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		try {
			//Download the rbase image at the start of the container. if there is an issue it will go ahead with starting springboot container.
			String hostname = host + ":" + port;
			logger.debug(EELFLoggerDelegate.debugLogger, rimageName + " Pull Started hostname: " + hostname);
			DockerClient dockerClient = UtilityFunction.createDockerClient("tcp://" + hostname);
			logger.debug(EELFLoggerDelegate.debugLogger, "ApplicationStartupPullImage -> Docker client created");
			AuthConfig authConfig = new AuthConfig().withUsername(dockerusername).withPassword(dockerpassword);
			dockerClient.pullImageCmd(rimageName).withAuthConfig(authConfig).exec(new PullImageResultCallback())
					.awaitSuccess();
			logger.debug(EELFLoggerDelegate.debugLogger, rimageName + " Image pulled Successfully");

		} catch (Exception e) {
			logger.debug(EELFLoggerDelegate.debugLogger, "Failed to pull image " + e.getMessage());
			logger.error(EELFLoggerDelegate.errorLogger, "Failed to pull image " + e.getMessage());
		}
	}

}