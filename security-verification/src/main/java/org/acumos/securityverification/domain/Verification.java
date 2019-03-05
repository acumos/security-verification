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

import java.util.List;

public class Verification {

	private boolean externalScan;
	private List<AllowedLicense> allowedLicense;
	private SecurityScan securityScan;
	private SecurityVerify securityVerify;
	private LicenseScan licenseScan;
	private LicenseVerify licenseVerify;

	public boolean isExternalScan() {
		return externalScan;
	}

	public void setExternalScan(boolean externalScan) {
		this.externalScan = externalScan;
	}

	public List<AllowedLicense> getAllowedLicense() {
		return allowedLicense;
	}

	public void setAllowedLicense(List<AllowedLicense> allowedLicense) {
		this.allowedLicense = allowedLicense;
	}

	public SecurityScan getSecurityScan() {
		return securityScan;
	}

	public void setSecurityScan(SecurityScan securityScan) {
		this.securityScan = securityScan;
	}

	public SecurityVerify getSecurityVerify() {
		return securityVerify;
	}

	public void setSecurityVerify(SecurityVerify securityVerify) {
		this.securityVerify = securityVerify;
	}

	public LicenseScan getLicenseScan() {
		return licenseScan;
	}

	public void setLicenseScan(LicenseScan licenseScan) {
		this.licenseScan = licenseScan;
	}

	public LicenseVerify getLicenseVerify() {
		return licenseVerify;
	}

	public void setLicenseVerify(LicenseVerify licenseVerify) {
		this.licenseVerify = licenseVerify;
	}

}
