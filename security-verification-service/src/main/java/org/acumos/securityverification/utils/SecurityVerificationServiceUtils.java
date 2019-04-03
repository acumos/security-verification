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
package org.acumos.securityverification.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityVerificationServiceUtils {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static boolean isEmptyOrNullString(String input) {
		boolean isEmpty = false;
		if (null == input || 0 == input.trim().length()) {
			isEmpty = true;
		}
		return isEmpty;
	}
	
	public static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

	public static byte[] executeScript(String scriptFile,String solutionId, String revisionId,String folder) throws Exception {
		logger.debug("Inside executeScript.");

		byte[] result = null;
		ProcessBuilder processBuilder = null;
		Process process = null;
		BufferedReader reader = null;
		try {
			StringBuilder sb = new StringBuilder();
			
			String[] cmd1 = { "chmod", "777", scriptFile };
			processBuilder = new ProcessBuilder(cmd1);
			if (processBuilder != null) {
				process = processBuilder.start();
				int errCode = process.waitFor();
				logger.debug("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
				String line = null;
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((line = reader.readLine()) != null) {
					sb.append(line + System.getProperty("line.separator"));
				}

				logger.debug("cmd1 >>  {}", sb.toString());
			}
			
			String[] cmd2 = { "mkdir", SVServiceConstants.SCAN_SCRIPT_LOCATION+folder };
			processBuilder = new ProcessBuilder(cmd2);
			if (processBuilder != null) {
				process = processBuilder.start();
				int errCode = process.waitFor();
				logger.debug("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
				String line = null;
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((line = reader.readLine()) != null) {
					sb.append(line + System.getProperty("line.separator"));
				}
				logger.debug("cmd2>>  {}", sb.toString());
			}
			logger.debug("Before call script shell");
			String[] cmd3 = { "bash", scriptFile, solutionId, revisionId, SVServiceConstants.SCAN_SCRIPT_LOCATION+folder };
			processBuilder = new ProcessBuilder(cmd3);
			logger.debug("After call script shell");
			if (processBuilder != null) {
				process = processBuilder.start();
				int errCode = process.waitFor();
				logger.debug("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
				String line = null;
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((line = reader.readLine()) != null) {
					sb.append(line + System.getProperty("line.separator"));
				}

				logger.debug("cmd3>>  {}", sb.toString());
			}
		} finally {
			if (null != process) {
				process.waitFor(10, TimeUnit.SECONDS);
				process.destroy();
			}
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("executeScript failed {}", e);
					throw e;
				}
			}
		}

		return result;
	}
	
	public static File readScanOutput(String path) {
		File file = new File(path); 
		return file;
	}
	
	public static InputStream readScript(String folder, String jsonFile) throws Exception {

		logger.debug("readScript()::: read JSON \n\n");
		File scancode_file = new File(SVServiceConstants.SCAN_SCRIPT_LOCATION+folder+jsonFile);
		InputStream scancodeStream;
		FileReader fr=null;
	    BufferedReader br=null;
		try {
			scancodeStream = new DataInputStream(new FileInputStream(scancode_file));
			fr = new FileReader(scancode_file);
			br = new BufferedReader(fr);
			String line;
			logger.debug("Reading text file using FileReader");
			while ((line = br.readLine()) != null) {
				logger.debug(line);
			}
		} catch (IOException e) {
			logger.error("readScript failed {}", e);
			throw e;
		}finally {
			if (null != br) {
				br.close();
			}
			if (null != fr) {
				fr.close();
			}

		}
		

		return scancodeStream;
	}
}
