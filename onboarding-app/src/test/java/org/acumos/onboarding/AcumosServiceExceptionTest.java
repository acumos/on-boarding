/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual
 * 						Property & Tech
 * 						Mahindra. All rights reserved.
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AcumosServiceExceptionTest {
	
	@Mock
	AcumosServiceException acumosServiceException;
	
	
	@Test
	public void AcumosServiceExceptionTest() {
		
		AcumosServiceException acumosServiceException = new AcumosServiceException("Service is not available");
		
	}
	
	
	@Test
	public void AcumosServiceExceptionTest1() {
		
		AcumosServiceException acumosServiceException = new AcumosServiceException("Service is not available",new Throwable());
		
	}
	

	@Test
	public void AcumosServiceExceptionTest2() {
		AcumosServiceException acumosServiceException = new AcumosServiceException(AcumosServiceException.ErrorCode.CONNECTION_ISSUE,"Service is not available");
	}
}
