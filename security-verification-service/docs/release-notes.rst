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

