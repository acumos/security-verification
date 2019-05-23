.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

=========================================================
Security Verification (SV) Scanning Service Release Notes
=========================================================

--------------------------
Version 0.2.0, 22 May 2019
--------------------------

This release includes improvements and other updates as below, for the merged
commits and related Jira items:

* `4362: SecurityVerificationServiceImpl createSiteConfig <https://gerrit.acumos.org/r/#/c/security-verification/+/4362/>`_

  * `ACUMOS-2865: SecurityVerificationServiceImpl.createSiteConfig <https://jira.acumos.org/browse/ACUMOS-2865>`_


* `ACUMOS-2860: Artifact type cdump not found -- when publishing in portal <https://jira.acumos.org/browse/ACUMOS-2860>`_

  * `4462: Artifact type cdump not found in portal <https://gerrit.acumos.org/r/#/c/security-verification/+/4462/>`_
  * `4449: Artifact type cdump not found in portal <https://gerrit.acumos.org/r/#/c/security-verification/+/4449/>`_
  * `4443: Artifact type cdump not found in portal <https://gerrit.acumos.org/r/#/c/security-verification/+/4443/>`_
  * `4418: Artifact type cdump not found in portal <https://gerrit.acumos.org/r/#/c/security-verification/+/4418/>`_
  * `4408: Artifact type cdump not found in portal <https://gerrit.acumos.org/r/#/c/security-verification/+/4408/>`_
  * `4397: Artifact type cdump not found in portal <https://gerrit.acumos.org/r/#/c/security-verification/+/4397/>`_
  * `4351: Artifact type cdump not found when publishing <https://gerrit.acumos.org/r/#/c/security-verification/+/4351/>`_

* `4338: Updated SV code <https://gerrit.acumos.org/r/#/c/security-verification/+/4338/>`_

  * `ACUMOS-2845: Dependencies should be installed part of the docker image of the component rather than directly in yaml file <https://jira.acumos.org/browse/ACUMOS-2845>`_

* `4262: Sonar 40% code coverage requirement on every repo <https://gerrit.acumos.org/r/#/c/security-verification/+/4262/>`_

  * `ACUMOS-1095: Sonar 40% code coverage requirement on every repo <https://jira.acumos.org/browse/ACUMOS-1095>`_
  * `ACUMOS-2815: Security Verification throwing Unexected Error Message <https://jira.acumos.org/browse/ACUMOS-2815>`_

* `4179: S-V Library workflow permission determination <https://gerrit.acumos.org/r/#/c/security-verification/+/4179/>`_

  * `ACUMOS-2774: Security Verification run containerized process as unprivileged user <https://jira.acumos.org/browse/ACUMOS-2774>`_

* `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

  * `4455: Script updates in testing <https://gerrit.acumos.org/r/#/c/security-verification/+/4455/>`_
  * `4450: Script updates in testing <https://gerrit.acumos.org/r/#/c/security-verification/+/4450/>`_
  * `4409: Script updates in testing <https://gerrit.acumos.org/r/#/c/security-verification/+/4409/>`_
  * `4204: Script updates in testing <https://gerrit.acumos.org/r/#/c/security-verification/+/4204/>`_
  * `4188: Move config to /tmp <https://gerrit.acumos.org/r/#/c/security-verification/+/4188/>`_
  * `4187: Add license type to scanresult.json <https://gerrit.acumos.org/r/#/c/security-verification/+/4187/>`_

* `4156: S-V Library workflow permission determination <https://gerrit.acumos.org/r/#/c/security-verification/+/4156/>`_

  * `ACUMOS-1956:S-V library implementation <https://jira.acumos.org/browse/ACUMOS-1956>`_
  * `ACUMOS-2559: S-V Library workflow permission determination <https://jira.acumos.org/browse/ACUMOS-2559>`_

----------------------------
Version 0.1.0, 12 April 2019
----------------------------

This is the first test release of the SV Scanning Service. Docker-compose and
kubernetes templates are in the
`system-integration <https://github.com/acumos/system-integration>`_ repo
folders AIO/docker/acumos and AIO/kubernetes, respectively. The implementation
includes a combination of:

* A springboot application that serves the "/scan" API, per the
  `design document <https://docs.acumos.org/en/latest/submodules/security-verification/security-verification-service/docs/design.html>`_
* A set of bash scripts as prototype implementations of the following functions,
  built into the generated SV Scanning Service image. These will be migrated to
  Java code as time permits:

  * dump_model.sh: dump all to-be-scanned data for a model revision
  * license_scan.sh: invoke the
    `Scancode Toolkit <https://github.com/nexB/scancode-toolkit>`_ on the dumped
    model data
  * scan_all.sh: test script to scan all revisions in the CDS
  * setup_verification_site_config.sh: test script to initialize the CDS site
    config for the SV Library and Scanning Service

Includes the merged commits and related Jira items:

* `4137: Release 0.1.0 <https://gerrit.acumos.org/r/#/c/4137/>`_

  * `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

* `4135: Add scan_all.sh script, fix license_scan.sh bugs <https://gerrit.acumos.org/r/#/c/4135/>`_

  * `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

* `4098: Updates for testing <https://gerrit.acumos.org/r/#/c/4098/>`_

  * `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

* `4090: Integrate scripts into sv-scanning-service <https://gerrit.acumos.org/r/#/c/4090/>`_

  * `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

* `4069: Add script to populate verification site key <https://gerrit.acumos.org/r/#/c/4069/>`_

  * `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

----------------------------
Version 0.0.1, 04 April 2019
----------------------------

Includes the merged commits and related Jira items:

* `3881: Baseline license scan scripts <https://gerrit.acumos.org/r/#/c/3881/>`_

  * `ACUMOS-1958: S-V License Scan process implementation <https://jira.acumos.org/browse/ACUMOS-1958>`_
