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

public class LicenseVerify {

	boolean deploy;
	boolean download;
	boolean share;
	boolean publishCompany;
	boolean publishPublic;

	public boolean isDeploy() {
		return deploy;
	}

	public void setDeploy(boolean deploy) {
		this.deploy = deploy;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	public boolean isShare() {
		return share;
	}

	public void setShare(boolean share) {
		this.share = share;
	}

	public boolean isPublishCompany() {
		return publishCompany;
	}

	public void setPublishCompany(boolean publishCompany) {
		this.publishCompany = publishCompany;
	}

	public boolean isPublishPublic() {
		return publishPublic;
	}

	public void setPublishPublic(boolean publishPublic) {
		this.publishPublic = publishPublic;
	}

}
