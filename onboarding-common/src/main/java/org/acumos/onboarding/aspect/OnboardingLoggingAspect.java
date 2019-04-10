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
package org.acumos.onboarding.aspect;


import org.acumos.onboarding.common.utils.Crediantials;
import org.acumos.onboarding.common.utils.LoggerDelegate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
/**
*
* Aspect  for General Logging.
*/
@Aspect
@Component
public class OnboardingLoggingAspect  {
	
	private static final Logger logger = LoggerFactory.getLogger(OnboardingLoggingAspect.class);
	LoggerDelegate log = new LoggerDelegate(logger);
	@Pointcut("within(@org.springframework.stereotype.Controller *)")
	   public void controller() {
	}
	
	@Pointcut("execution(public * org.acumos.onboarding.services.impl.OnboardingController.dockerizePayload(..))")
	   protected void loggingSignOnOperation() {
	}	
	
	@Before("controller() &&  loggingSignOnOperation()")
	public void logBefore(JoinPoint joinPoint) throws Throwable {

		for (Object signatureArg : joinPoint.getArgs()) {
			if (signatureArg instanceof org.acumos.onboarding.common.utils.JsonRequest) {
				@SuppressWarnings("unchecked")
				org.acumos.onboarding.common.utils.JsonRequest<Crediantials> user = (org.acumos.onboarding.common.utils.JsonRequest<Crediantials>) signatureArg;
				String username = user.getBody().getUsername();
				MDC.put("user", username);
				MDC.put("contextName", "Onboarding");
				log.info("User Logging in");
				MDC.remove("user");
				MDC.remove("contextName");				
			}
		}		
	}
		
	@AfterReturning(pointcut = "controller() &&  loggingSignOnOperation()", returning = "result")
	public void logAfter(JoinPoint joinPoint , Object result) {

		for (Object signatureArg : joinPoint.getArgs()) {
			if (signatureArg instanceof org.acumos.onboarding.common.utils.JsonRequest) {
				@SuppressWarnings("unchecked")
				org.acumos.onboarding.common.utils.JsonRequest<Crediantials> user = (org.acumos.onboarding.common.utils.JsonRequest<Crediantials>) signatureArg;
				String username = user.getBody().getUsername();
				MDC.put("user", username);
				MDC.put("contextName", "Onboarding");												
			}
		}		
		
		MDC.remove("user");
		MDC.remove("contextName");
	}
	
}
