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
package org.acumos.securityverification.transport;

import java.util.ArrayList;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScanResult extends AbstractResponseObject {
    private String schema;
    private String scanTime;
    private String revisionId;
    private String solutionId;
    private String reason;
    private String verifiedLicense;
    private ArrayList files[];

    public String getSchema() {
     return schema;
    }
    public String getScanTime() {
     return scanTime;
    }
    public String getRevisionId() {
     return revisionId;
    }
    public String getSolutionId() {
     return solutionId;
    }
    public String getReason() {
     return reason;
    }
    public String getVerifiedLicense() {
     return verifiedLicense;
    }
}
