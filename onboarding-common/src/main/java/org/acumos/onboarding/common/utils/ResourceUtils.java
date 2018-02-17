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

package org.acumos.onboarding.common.utils;

import java.io.IOException;

import org.acumos.onboarding.common.exception.AcumosServiceException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

public class ResourceUtils {
	private ResourceLoader resourceLoader;

	public ResourceUtils(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Resource getResource(String path) throws AcumosServiceException {
		Resource resource = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
				.getResource("classpath:" + path);
		if (resource == null)
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					path + " not found in classpath");
		return resource;
	}

	public boolean isResourceExists(String path) throws AcumosServiceException {
		return ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResource("classpath:" + path)
				.exists();
	}

	public Resource[] loadResources(String pattern) throws AcumosServiceException {
		try {
			return ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern);
		} catch (IOException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Fail to read resources form classpath cause:" + e.getMessage(), e);
		}
	}
}
