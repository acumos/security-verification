/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

public class SVConstants {

  // Path components
  public static final String APPLICATION_JSON = "application/json";
  public static final String SECURITY_SCAN = "/scan/solution/{solutionId}/revision/{revisionId}";
  public static final String SITE_CONFIG_UPDATE = "/update/siteConfig/verification";

  // Site config Json tag
  public static final String SITE_VERIFICATION_KEY = "verification";
  public static final String VERIFICATION = "verification";
  public static final String LICENSESCAN = "licenseScan";
  public static final String CREATED = "created";
  public static final String UPDATED = "updated";
  public static final String RTUCHECK = "rtuCheck";

  public static final String DEPLOY = "deploy";
  public static final String DOWNLOAD = "download";
  public static final String SHARE = "share";
  public static final String PUBLISHCOMPANY = "publishCompany";
  public static final String PUBLISHPUBLIC = "publishPublic";

  public static final String SECURITYSCAN = "securityScan";
  public static final String LICENSEVERIFY = "licenseVerify";
  public static final String SECURITYVERIFY = "securityVerify";
  public static final String EXTERNALSCAN = "externalScan";
  public static final String ALLOWEDLICENSE = "allowedLicense";
  public static final String TYPE = "type";
  public static final String VALUE = "value";

  // CDUMP Json tag
  public static final String ARTIFACT_TYPE_CDUMP = "CD";
  public static final String ARTIFACT_TYPE_SCANRESULT = "SR";
  public static final String JSON_FILE_NAME = "ACUMOS-CDUMP.json";
  public static final String MODEL_TYPE_CODE = "PR";
  public static final String TOOL_KIT_TYPE_CODE = "CP";

  public static final String NODES = "nodes";
  public static final String DEPENDS_ON = "depends_on";
  public static final String CONTAINER_NAME = "container_name";
  public static final String IMAGE = "image";

  public static final String REASON_NOT_FOUND = "solution/revision not found";
  public static final String LICENSE_SCAN_INCOMPLETE = "license scan incomplete";

  public static final String SOLUTIONID = "solutionId";
  public static final String REVISIONID = "revisionId";
  public static final String USERID = "userId";
  public static final String FORWARD_SLASH = "/";
  public static final String TRUE = "true";
}
