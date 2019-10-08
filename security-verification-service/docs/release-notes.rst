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

================================================
Security Verification (SV) Library Release Notes
================================================

--------------------------
Version 1.2.0, 10 Oct 2019
--------------------------

* Removal of LMCL dependency - `ACUMOS-3505 <https://jira.acumos.org/browse/ACUMOS-3505>`_
* Changed docker image for SV to follow acumos/ prefix

--------------------------
Version 1.1.0, 01 Oct 2019
--------------------------

* `5317: Security Verification 1.1.0 - jenkins and rtu <https://gerrit.acumos.org/r/#/c/security-verification/+/5317/>`_

  * `ACUMOS-3125: As a model User, when LUM provides denial of action to Acumos, Security Verification will not allow user action and Portal will display notification to user. <https://jira.acumos.org/browse/ACUMOS-3125>`_

----------------------------
Version 1.0.1, 19 Sept 2019
----------------------------

* `ACUMOS-3031: A scan must occur to verify License Profile metadata <https://jira.acumos.org/browse/ACUMOS-3031>`_

  * `4888: Support License ArtifactType <https://gerrit.acumos.org/r/#/c/security-verification/+/4888/>`_

* `4965: Fix SV build on master <https://gerrit.acumos.org/r/#/c/security-verification/+/4965/>`_

---------------------------
Version 0.0.24, 09 Jun 2019
---------------------------

Version update only; changes were to the SV Scanning Service

* `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

  * `4796: Bump sv-client version <https://gerrit.acumos.org/r/#/c/security-verification/+/4796/>`_

---------------------------
Version 0.0.22, 25 Jun 2019
---------------------------

* `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

  * `4603: Wait for CDS, init scancode on startup <https://gerrit.acumos.org/r/#/c/security-verification/+/4754/>`_

---------------------------
Version 0.0.21, 07 Jun 2019
---------------------------

* `4632: Changing API - SV needs to accept logged in userId <https://gerrit.acumos.org/r/#/c/security-verification/+/4632/>`_
* `4616: Rtu Verifier fixes <https://gerrit.acumos.org/r/#/c/security-verification/+/4616/>`_
* `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

  * `4634: Update release notes, scan scripts <https://gerrit.acumos.org/r/#/c/security-verification/+/4634/>`_
  * `4621: Fix workflow check <https://gerrit.acumos.org/r/#/c/security-verification/+/4621/>`_
  * `4603: Support proprietary licences <https://gerrit.acumos.org/r/#/c/security-verification/+/4603/>`_

* Uprev to allow release of License Manager client library

---------------------------
Version 0.0.20, 30 May 2019
---------------------------

* `ACUMOS-2559: S-V Library workflow permission determination <https://jira.acumos.org/browse/ACUMOS-2559>`_

  * `4555: Update versions for release <https://gerrit.acumos.org/r/#/c/security-verification/+/4555/>`_
  * `4554: Upload single copy of scancode.json etc <https://gerrit.acumos.org/r/#/c/security-verification/+/4554/>`_
  * `4545: Update artifact creation logic <https://gerrit.acumos.org/r/#/c/security-verification/+/4545/>`_
  * `4534: Update artifact creation logic <https://gerrit.acumos.org/r/#/c/security-verification/+/4534/>`_

---------------------------
Version 0.0.19, 28 May 2019
---------------------------

* `ACUMOS-2559: S-V Library workflow permission determination <https://jira.acumos.org/browse/ACUMOS-2559>`_

  * `4524: Correct return of failure reason to user <https://gerrit.acumos.org/r/#/c/security-verification/+/4524/>`_
  * `4522: Correct check for getVerifiedLicense result <https://gerrit.acumos.org/r/#/c/security-verification/+/4522/>`_
  * `4518: S-V Library workflow permission determination <https://gerrit.acumos.org/r/#/c/security-verification/+/4518/>`_

---------------------------
Version 0.0.18, 23 May 2019
---------------------------

* Update license-manager-client-library version-0.0.7 in security-verification-client-library (`ACUMOS-2954 <https://jira.acumos.org/browse/ACUMOS-2954>`_)

* `4489: Update design doc with recommended tests <https://gerrit.acumos.org/r/#/c/security-verification/+/4489/>`_

  * `ACUMOS-2358: S-V design documentation <https://jira.acumos.org/browse/ACUMOS-2358>`_

* `4366: Updated release note <https://gerrit.acumos.org/r/#/c/security-verification/+/4366/>`_

  * `ACUMOS-2886: update security verification for cds 2.2.2 <https://jira.acumos.org/browse/ACUMOS-2886>`_

* `4291: Update SV and LM version for LF release <https://gerrit.acumos.org/r/#/c/security-verification/+/4291/>`_

* `ACUMOS-2830: Update license-manager-client-library, security-verification-client and security-verification-service For LF release <https://jira.acumos.org/browse/ACUMOS-2830>`_

* `4262: Sonar 40% code coverage requirement on every repo <https://gerrit.acumos.org/r/#/c/security-verification/+/4262/>`_

  * `ACUMOS-1095: Sonar 40% code coverage requirement on every repo <https://jira.acumos.org/browse/ACUMOS-1095>`_
  * `ACUMOS-2815: Security Verification throwing Unexected Error Message <https://jira.acumos.org/browse/ACUMOS-2815>`_

* 4206: S-V library implementation (`<https://gerrit.acumos.org/r/#/c/security-verification/+/4202/>`_)
* 4202: S-V library implementation (`<https://gerrit.acumos.org/r/#/c/security-verification/+/4202/>`_)
* 4202: S-V library implementation (`<https://gerrit.acumos.org/r/#/c/security-verification/+/4202/>`_)
* 4201: S-V library implementation (`<https://gerrit.acumos.org/r/#/c/security-verification/+/4201/>`_)

-------------------------------
Version 0.0.17, 14 May 2019
-------------------------------

* Artifact type cdump not found -- when publishing in portal (`ACUMOS-2860 <https://jira.acumos.org/browse/ACUMOS-2860>`_)

-------------------------------
Version 0.0.16, 10 May 2019
-------------------------------
* SecurityVerificationServiceImpl.createSiteConfig (`ACUMOS-2865 <https://jira.acumos.org/browse/ACUMOS-2865>`_)

-------------------------------
Version 0.0.15, 10 May 2019
-------------------------------

* SecurityVerificationServiceImpl.createSiteConfig (`ACUMOS-2865 <https://jira.acumos.org/browse/ACUMOS-2865>`_)
* Artifact type cdump not found -- when publishing in portal (`ACUMOS-2860 <https://jira.acumos.org/browse/ACUMOS-2860>`_)
* Dependencies should be installed part of the docker image of the component rather than directly in yaml file (`ACUMOS-2845 <https://jira.acumos.org/browse/ACUMOS-2845>`_)

-------------------------------
Version 0.0.12, 01 May 2019
-------------------------------

* Update license-manager-client-library, security-verification-client and security-verification-service For LF release  (`ACUMOS-2830 <https://jira.acumos.org/browse/ACUMOS-2830>`_)

-------------------------------
Version 0.0.11, 30 April 2019
-------------------------------

* Security Verification throwing Unexected Error Message (`ACUMOS-2815 <https://jira.acumos.org/browse/ACUMOS-2815>`_)

----------------------------
Version 0.1.0, 12 April 2019
----------------------------

* `ACUMOS-2559: S-V Library workflow permission determination <https://jira.acumos.org/browse/ACUMOS-2559>`_

  * `4137: Release 0.1.0 <https://gerrit.acumos.org/r/#/c/4137/>`_
  * `4113: S-V Library workflow permission determination <https://gerrit.acumos.org/r/#/c/4113/>`_
  * `4101: S-V Library workflow permission determination <https://gerrit.acumos.org/r/#/c/4101/>`_
  * `4091: S-V Library workflow permission determination <https://gerrit.acumos.org/r/#/c/4091/>`_

----------------------------
Version 0.0.3, 05 April 2019
----------------------------

* `4085: Updated release note <https://gerrit.acumos.org/r/#/c/4085/>`_

  * `ACUMOS-2555: S-V Library base module <https://jira.acumos.org/browse/ACUMOS-2555>`_

* `4065: S-V Library workflow permission determination <https://gerrit.acumos.org/r/#/c/4065/>`_

  * `ACUMOS-2559: S-V Library workflow permission determination <https://jira.acumos.org/browse/ACUMOS-2559>`_

----------------------------
Version 0.0.1, 04 April 2019
----------------------------

* `3990: S-V library implementation <https://gerrit.acumos.org/r/#/c/3990/>`_

  * `ACUMOS-1956: S-V library implementation <https://jira.acumos.org/browse/ACUMOS-1956>`_
  * `ACUMOS-2546: Reorganize security-verification git repo to support multiple maven projects <https://jira.acumos.org/browse/ACUMOS-2546>`_
  * `ACUMOS-2559: S-V Library workflow permission determination <https://jira.acumos.org/browse/ACUMOS-2559>`_

* `3977: S-V library implementation <https://gerrit.acumos.org/r/#/c/3977/>`_

  * `ACUMOS-1956: S-V library implementation <https://jira.acumos.org/browse/ACUMOS-1956>`_
  * `ACUMOS-2546: Reorganize security-verification git repo to support multiple maven projects <https://jira.acumos.org/browse/ACUMOS-2546>`_
  * `ACUMOS-2559: S-V Library workflow permission determination <https://jira.acumos.org/browse/ACUMOS-2559>`_

* `3948: S-V library implementation <https://gerrit.acumos.org/r/#/c/3948/>`_

  * `ACUMOS-1956: S-V library implementation <https://jira.acumos.org/browse/ACUMOS-1956>`_
  * `ACUMOS-2555: S-V Library base module <https://jira.acumos.org/browse/ACUMOS-2555>`_
  * `ACUMOS-2557: S-V Library solution/revision processing <https://jira.acumos.org/browse/ACUMOS-2557>`_
  * `ACUMOS-2558: S-V Library scan invocation logic <https://jira.acumos.org/browse/ACUMOS-2558>`_
  * `ACUMOS-2546: Reorganize security-verification git repo to support multiple maven projects <https://jira.acumos.org/browse/ACUMOS-2546>`_

* `3914: S-V library implementation <https://gerrit.acumos.org/r/#/c/3914/>`_

  * `ACUMOS-2555: S-V Library base module <https://jira.acumos.org/browse/ACUMOS-2555>`_
  * `ACUMOS-2557: S-V Library solution/revision processing <https://jira.acumos.org/browse/ACUMOS-2557>`_
  * `ACUMOS-2558: S-V Library scan invocation logic <https://jira.acumos.org/browse/ACUMOS-2558>`_

=========================================================
Security Verification (SV) Scanning Service Release Notes
=========================================================

--------------------------
Version 1.1.0, 01 Oct 2019
--------------------------

* `5317: Security Verification 1.1.0 - jenkins and rtu <https://gerrit.acumos.org/r/#/c/security-verification/+/5317/>`_

  * `ACUMOS-3428: Security Verification License Scan migration to Jenkins <https://jira.acumos.org/browse/ACUMOS-3428>`_
  * `ACUMOS-3125: As a model User, when LUM provides denial of action to Acumos, Security Verification will not allow user action and Portal will display notification to user. <https://jira.acumos.org/browse/ACUMOS-3125>`_
  * Add LUM URL env parameter for LMCL
  * Code formatting clean up
  * Licensing RTU check updates
  * Relocate/update scripts for Jenkins.
  * Scan invokes Jenkins job.
  * ScanResult handling from Jenkins.

----------------------------
Version 1.0.1, 19 Sept 2019
----------------------------

* `ACUMOS-3436: Security Verification update to Java 11 <https://jira.acumos.org/browse/ACUMOS-3436>`_

  * `5246: Security Verification - Java 11 <https://gerrit.acumos.org/r/#/c/security-verification/+/5246/>`_

* `ACUMOS-3428: Implement scan job queuing <https://jira.acumos.org/browse/ACUMOS-3428>`_

  * `5292: Release 1.0.1 <https://gerrit.acumos.org/r/#/c/security-verification/+/5292/>`_
  * `5271: Update to CDS 3.0.0 <https://gerrit.acumos.org/r/#/c/security-verification/+/5271/>`_
  * `5241: Ignore license type field for now <https://gerrit.acumos.org/r/#/c/security-verification/+/5241/>`_
  * `5210: Implement scan job queueing <https://gerrit.acumos.org/r/#/c/security-verification/+/5210/>`_

---------------------------
Version 0.0.24, 09 Jun 2019
---------------------------

This release restores the ability to deploy the SV Scanning Service with
full functionality embedded in the docker container image. Updates with external
configuration files (e.g. to update licenses/rules, or the scanning tool/scripts)
is optional, as described by the updated user-guide.

* `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

  * `4800: Handle exception cases and large scan sets <https://gerrit.acumos.org/r/#/c/security-verification/+/4800/>`_
  * `4795: Deployment with config updates optional <https://gerrit.acumos.org/r/#/c/security-verification/+/4795/>`_

* `ACUMOS-2358: S-V design documentation <https://jira.acumos.org/browse/ACUMOS-2358>`_

  * `4789: Update design, add user guide <https://gerrit.acumos.org/r/#/c/security-verification/+/4789/>`_

---------------------------
Version 0.0.22, 25 Jun 2019
---------------------------

* `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

  * `4603: Wait for CDS, init scancode on startup<https://gerrit.acumos.org/r/#/c/security-verification/+/4754/>`_

----------------------------
Version 0.0.21, 07 June 2019
----------------------------

* `ACUMOS-1373: S-V Scanning Service component with spring-based API <https://jira.acumos.org/browse/ACUMOS-1373>`_

  * `4603: Support proprietary licences <https://gerrit.acumos.org/r/#/c/security-verification/+/4603/>`_

---------------------------
Version 0.0.20, 30 May 2019
---------------------------

* `ACUMOS-2559: S-V Library workflow permission determination <https://jira.acumos.org/browse/ACUMOS-2559>`_

  * Update artifact creation logic

---------------------------
Version 0.0.19, 28 May 2019
---------------------------

* `ACUMOS-2559: S-V Library workflow permission determination <https://jira.acumos.org/browse/ACUMOS-2559>`_

  * `4524: Correct return of failure reason to user <https://gerrit.acumos.org/r/#/c/security-verification/+/4524/>`_

    * switch to curl (wget hangs), add logging

---------------------------
Version 0.0.18, 23 May 2019
---------------------------

This release includes improvements and other updates as below, for the merged
commits and related Jira items:

* `4489: Update design doc with recommended tests <https://gerrit.acumos.org/r/#/c/security-verification/+/4489/>`_

  * `ACUMOS-2358: S-V design documentation <https://jira.acumos.org/browse/ACUMOS-2358>`_

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
