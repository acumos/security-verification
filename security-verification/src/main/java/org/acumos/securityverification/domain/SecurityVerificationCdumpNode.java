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

package org.acumos.securityverification.domain;

import java.io.Serializable;

public class SecurityVerificationCdumpNode implements Serializable {

	private static final long serialVersionUID = 5437495964588153222L;
	
	private String name;
	private String nodeId;
	private String nodeSolutionId;
	private String nodeVersion;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeSolutionId() {
		return nodeSolutionId;
	}
	public void setNodeSolutionId(String nodeSolutionId) {
		this.nodeSolutionId = nodeSolutionId;
	}
	public String getNodeVersion() {
		return nodeVersion;
	}
	public void setNodeVersion(String nodeVersion) {
		this.nodeVersion = nodeVersion;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
