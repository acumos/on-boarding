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

public class FilePathTest {
	
	public static boolean isWindowsSys() {
		String osName = System.getProperty("os.name");
		String osNameMatch = osName.toLowerCase();
		if (osNameMatch.contains("windows"))
			return true;
		return false;
	}
	
	
	public static String filePath(){
		
		boolean windowsFlag = isWindowsSys();
		
		String filePath =  System.getProperty("user.dir");
		
		if(windowsFlag){
			  filePath = filePath+"\\src\\test\\resources\\";
		  } else {
			  filePath = filePath+"/src/test/resources/";
		  }
		
		return filePath;
		
		
		
	}

}
