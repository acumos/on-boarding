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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AcumosServiceExceptionTest {
	
	@InjectMocks
	AcumosServiceException acumosServiceException;
	
	@Test
	public void AcumosServiceExceptionTest(){
		acumosServiceException=new AcumosServiceException("message");
		acumosServiceException.setErrorCode("errorCode");
		Assert.assertNotNull(acumosServiceException.getErrorCode());
		Assert.assertNotNull(acumosServiceException);
	}
	
	@Test
	public void AcumosServiceExTest(){
		acumosServiceException=new AcumosServiceException("errorCode", "message");
		Assert.assertNotNull(acumosServiceException);
	}
	
	@Test
	public void AcumosExceptionTest(){
		acumosServiceException=new AcumosServiceException("errorCode", "message", new Throwable());
		Assert.assertNotNull(acumosServiceException);
	}
	
	@Test
	public void AcumosExceptTest(){
		acumosServiceException=new AcumosServiceException("message", new Throwable());
		Assert.assertNotNull(acumosServiceException);
	}
}
