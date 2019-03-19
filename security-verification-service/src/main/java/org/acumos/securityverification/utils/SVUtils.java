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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SVUtils {

	private static Logger logger = LoggerFactory.getLogger(SVUtils.class);

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

	public static byte[] executeScript(String solutionId, String revisionId,String folder) throws Exception {

		logger.debug("executeScript() INSIDE-----");

		byte[] result = null;
		ProcessBuilder processBuilder = null;
		Process process = null;
		BufferedReader reader = null;
		try {
			StringBuilder sb = new StringBuilder();

			String[] cmd = { "/home/cognitaopr/log/security-verification-test/shell-test/default.sh", solutionId, revisionId, folder };
			processBuilder = new ProcessBuilder(cmd);

			if (processBuilder != null) {
				process = processBuilder.start();
				int errCode = process.waitFor();
				logger.debug("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
				String line = null;
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((line = reader.readLine()) != null) {
					logger.debug("executeScript() INSIDE 6666-----");
					sb.append(line + System.getProperty("line.separator"));
				}

				logger.debug("sb.toString();>>>>  " + sb.toString());
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
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	public static File readScanOutput(String path) {
		File file = new File(path); 
		return file;
	}
	/*
	public static InputStream readScript() throws Exception {

		logger.debug("readScript()::: scancode JSON \n\n");
		byte[] result = null;
		File scancode_file = new File("/home/cognitaopr/log/security-verification-test/shell-test/output/test/scancode.json");
		
		InputStream scancodeStream = new DataInputStream(new FileInputStream(scancode_file));
		
		
		FileReader fr = new FileReader(scancode_file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		logger.debug("Reading text file using FileReader");
		while ((line = br.readLine()) != null) {
			logger.debug(line);
		}
		br.close();
		fr.close();
		

		logger.debug("readScript()::: scanresult JSON \n\n");
		File scanresult_file = new File(
				"/home/cognitaopr/log/security-verification-test/shell-test/output/test/scanresult.json");
		FileReader fr1 = new FileReader(scanresult_file);
		BufferedReader br1 = new BufferedReader(fr1);
		String line1;
		logger.debug("Reading text file using FileReader");
		while ((line1 = br1.readLine()) != null) {
			// process the line
			logger.debug(line1);
		}
		br1.close();
		fr1.close();

		return scancodeStream;
	}*/
}
