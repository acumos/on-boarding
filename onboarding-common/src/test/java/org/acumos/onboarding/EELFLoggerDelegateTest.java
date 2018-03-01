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

import org.acumos.onboarding.common.utils.EELFLoggerDelegate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EELFLoggerDelegateTest {
	
	EELFLoggerDelegate eelfLoggerDelegate = new EELFLoggerDelegate("EELFLoggerDelegate");
	
	@Test
	public void error(){
	
		eelfLoggerDelegate.error("Failure");	
		
	}
	@Test
	public void errorDiff(){
		Throwable th =null;
		eelfLoggerDelegate.error("Fail", th);;	
		
	}
	
	@Test
	public void init(){
	
		eelfLoggerDelegate.init();	
		
	}
	
	@Test
	public void warn(){
	
		eelfLoggerDelegate.warn("warn");	
		
	}
	@Test
	public void warndiff(){
		Throwable th =null;
		eelfLoggerDelegate.warn("warndiff", th);
		
	}
	@Test
	public void warnthreeParam(){
		Throwable th =null;
		eelfLoggerDelegate.warn(eelfLoggerDelegate, "info", th);
		
	}
	@Test
	public void info(){
	
		eelfLoggerDelegate.warn("info");	
		
	}
	@Test
	public void infodiff(){
		Throwable th =null;
		eelfLoggerDelegate.info("infodiff", th);	
		
	}
	@Test
	public void infoopp(){
		
		eelfLoggerDelegate.info(eelfLoggerDelegate,"tracediff");	
		
	}
	@Test
	public void infothreeParam(){
		Throwable th =null;
		eelfLoggerDelegate.info(eelfLoggerDelegate, "info", th);
		
	}
	@Test
	public void debug(){
	
		eelfLoggerDelegate.debug("debug");	
		
	}
	@Test
	public void debugdiff(){
		Throwable th =null;
		eelfLoggerDelegate.debug("debugdiff", th);
		
	}
	@Test
	public void debugopp(){
		
		eelfLoggerDelegate.debug(eelfLoggerDelegate,"tracediff");	
		
	}
	@Test
	public void debugthreeParam(){
		Throwable th =null;
		eelfLoggerDelegate.debug(eelfLoggerDelegate, "info", th);
		
	}
	@Test
	public void trace(){
	
		eelfLoggerDelegate.trace("trace");	
		
	}
	@Test
	public void tracediff(){
		Throwable th =null;
		eelfLoggerDelegate.trace("tracediff", th);	
		
	}
	@Test
	public void traceopp(){
		
		eelfLoggerDelegate.trace(eelfLoggerDelegate,"tracediff");	
		
	}
	@Test
	public void tracethreeParam(){
		Throwable th =null;
		eelfLoggerDelegate.trace(eelfLoggerDelegate, "info", th);
		
	}
	@Test
	public void getLoggerTest(){
		
		eelfLoggerDelegate.getLogger("Logger");	
		
	}
	@Test
	public void getLoggerTest1(){
		
		try {
			eelfLoggerDelegate.getLogger(Class.forName("EELFLoggerDelegateTest"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
}
