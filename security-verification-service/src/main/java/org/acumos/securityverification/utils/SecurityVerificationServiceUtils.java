/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class SecurityVerificationServiceUtils {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static boolean isEmptyOrNullString(String input) {
		logger.debug("Inside isEmptyOrNullString");
		boolean isEmpty = false;
		if (null == input || 0 == input.trim().length()) {
			isEmpty = true;
		}
		return isEmpty;
	}

	public static byte[] executeScript(String scriptFile, String solutionId, String revisionId, String folder,
			Environment env) throws Exception {
		logger.debug("Inside executeScript scriptFile:{} solutionId:{} revisionId:{}", scriptFile, solutionId,
				revisionId);

		String scriptFileName = SVServiceConstants.FORWARD_SLASH + SVServiceConstants.MAVEN
				+ SVServiceConstants.FORWARD_SLASH + SVServiceConstants.SECURITY_SCAN + SVServiceConstants.FORWARD_SLASH
				+ scriptFile;
		String folderName = SVServiceConstants.FORWARD_SLASH + SVServiceConstants.MAVEN
				+ SVServiceConstants.FORWARD_SLASH + SVServiceConstants.SECURITY_SCAN + SVServiceConstants.FORWARD_SLASH + folder;
		byte[] result = null;
		ProcessBuilder processBuilder = null;
		Process process = null;
		BufferedReader reader = null;
		try {
			StringBuilder sb = new StringBuilder();
			String createFolder = SVServiceConstants.MAVEN + SVServiceConstants.FORWARD_SLASH + SVServiceConstants.SECURITY_SCAN + SVServiceConstants.FORWARD_SLASH + folder;
			logger.debug("Execute Command: mkdir {}", createFolder);
			String[] cmd1 = { "mkdir", createFolder };
			processBuilder = new ProcessBuilder(cmd1);
			if (processBuilder != null) {
				process = processBuilder.start();
				int errCode = process.waitFor();
				logger.debug("Cmd1 mkdir folder, Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
				String line = null;
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((line = reader.readLine()) != null) {
					sb.append(line + System.getProperty("line.separator"));
				}
				logger.debug("Cmd1>>  {}", sb.toString());
			}

			if (scriptFile.equalsIgnoreCase(SVServiceConstants.SCRIPTFILE_DUMP_MODEL)) {
				logger.debug("Execute Command: bash {} {} {} {}", scriptFileName, solutionId, revisionId,folderName);

				String[] dump_model_cmd = { "bash", scriptFileName, solutionId, revisionId,folderName };
				processBuilder = new ProcessBuilder(dump_model_cmd);
				logger.debug("After call script shell");
				if (processBuilder != null) {
					process = processBuilder.start();
					int errCode = process.waitFor();
					logger.debug("dump_model_cmd script, Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
					String line = null;
					reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					while ((line = reader.readLine()) != null) {
						sb.append(line + System.getProperty("line.separator"));
					}
					logger.debug("dump_model_cmd>>  {}", sb.toString());
				}
				logger.debug("Scan result location {}{}{}", SVServiceConstants.SECURITY_SCAN,
						SVServiceConstants.FORWARD_SLASH, folder);
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
		logger.debug("Inside readScanOutput");
		File file = new File(path);
		return file;
	}
}