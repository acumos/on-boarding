/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 - 2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * /*-
 *  * This Acumos software file is distributed by AT&T and Tech Mahindra
 *  * under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *  
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *  
 *  * This file is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 * ===============LICENSE_END=========================================================
 */

package org.acumos.onboarding.component.docker.cmd;

import org.apache.commons.lang.StringUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.exception.DockerException;

public class DeleteImageCommand extends DockerCommand {

	private final String imageName;

	private final String tag;

	private final String registry;

	public DeleteImageCommand(String image, String tag, String registry) {

		this.imageName = image;
		this.tag = tag;
		this.registry = registry;
	}

	public String getImageName() {
		return imageName;
	}

	public String getTag() {
		return tag;
	}

	public String getRegistry() {
		return registry;
	}

	@Override
	public void execute() throws DockerException {
		if (!StringUtils.isNotBlank(imageName)) {
			throw new IllegalArgumentException("Image name must be provided");
		}
		// Don't include tag in the image name. Docker daemon can't handle it.
		// put tag in query string parameter.
		String imageFullName = CommandUtils.imageFullNameFrom(registry, imageName, tag);
		final DockerClient client = getClient();
		RemoveImageCmd removeImageCmd = client.removeImageCmd(imageFullName);
		removeImageCmd.exec();
	}

	@Override
	public String getDisplayName() {
		return "Push image";
	}

}
