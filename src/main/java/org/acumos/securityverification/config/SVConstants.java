/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T 
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

package org.acumos.securityverification.config;

public final class SVConstants {

	public static final String API_PATH_SCAN = "/scan";
	public static final String API_PATH_SCAN_RESULT = "/result";
	public static final String API_PATH_VERIFY = "/verify";
	public static final String API_SOLUTION_ID = "{solutionId}";
	public static final String API_WORKFLOW_ID = "{workflowId}";
	public static final String API_REVISION_ID = "{revisionId}";
	
	public static final String INVALID_SOLUTION_ID = "invalid solutionId";
	public static final String INVALID_REVISION_ID = "invalid revisionId";
	public static final String INVALID_WORKFLOW_ID = "invalid workflowId";
	
	// Sonar wants a private constructor, placed after all fields
	private SVConstants() {}
}
