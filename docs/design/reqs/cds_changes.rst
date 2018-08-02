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

===========================
Common Data Service Changes
===========================

CDS: Refactor "Validation"
==========================
Rename "validation" to "verification"


CDS: Security-Verification Type
===============================

The previously defined "Validation Type" CDS entries shall be reused with only a minor change:

.. csv-table::
    :header: "Current (defined for deprecated Validation component)", "New"
    :widths: 50, 50
    :align: left

    SS “Security Scan”,  SS "Security Scan"
    LC “License Check”, LS "License Scan"
    OQ “OSS Quantification”, not used
    TA “Text Analysis”, not used

CodeNameType update:

* org.acumos.cds.CodeNameType.VALIDATION_TYPE to VERIFICATION_TYPE

CDS: Security-Verification Status
=================================

The previously defined "Validation Status" CDS entries shall be reused with only a minor change:

.. csv-table::
    :header: "Current (defined for deprecated Validation component)", "New"
    :widths: 50, 50
    :align: left

    FA “Failed”,  FA “Failed”
    NV “Not Validated”, NR “Not Requested" (this should be the default)
    IP “In Progress”, IP “In Progress”
    PS “Passed”, PS “Passed”
    SB “Submitted”, this status is not used by the new S_V component

These statuses can be retrieved by calling CommonDataServiceImpl.getCodeNamePairs(CodeNameType.VERIFICATION_TYPE).

CDS: CodeNameType
=================
org.acumos.cds.CodeNameType update:

* change VALIDATION_STATUS to VERIFICATION_STATUS

CDS: MLPAbstractRevision Update
===============================

org.acumos.cds.domain.MLPAbstractRevision update:

* change validationStatusCode to verificationLicenseStatusCode; default shall be NR
* add new field: verificationScanStatusCode; default shall be NR
* modify constructor to add parameter for new field

OR do we want a COLLECTION of verificationStatus items? (code, status, workflow, date)

Various types of scan could be triggered multiple times on the same revision. MLPAbstractRevision would have a collection of these objects. This would allow for greater flexibility and granularity in verification types/statuses based on platform operator choice and future security-verification scan types such as keyword scan.


CDS: Artifact Type
==================

Artifact types are defined in the CDS `Artifact Type docs <https://docs.acumos.org/en/latest/submodules/common-dataservice/docs/requirements.html#artifact-type>`_.

CDS shall define a new artifact type "SR" "SCAN-RESULTS" for the scanresults.json file that shall be created/stored in Nexus.


.. _cds-workflow-type:

CDS: Workflow Type
------------------
CDS does not currently have security-verification workflow types defined. A new "workflow type" with the following values shall be created:

.. csv-table::
    :header: "Code", "Text", "Description"
    :widths: 33, 33, 33
    :align: left

    CR, created, model has been onboarded
    UD, updated, model artifacts/metadata have been updated
    PR, deploy-private, request to deploy to private cloud received
    PB, deploy-public, request to deploy to public cloud received
    DL, download, request to download received
    SH, share, request to share received
    PC, publish-company, request to publish to company marketplace received
    PP, publish-public, request to publish to public marketplace received
    SS, subscribe, request to subscribe received

Note: 2-letter code may change at the discretion of the CDS developers

The CDS shall implement functionality to obtain workflow types.
These workflow types are also stored as in the security-verification site config json string. The expectation is that the Portal will obtain the s-v site config and use it to create an s-v configuration section on the Portal admin screen.

CDS: MLPSolutionValidation
==========================
This is the model for a solution validation detail. MLPSolutionValidation has the following attributes: solutionId, revisionId, taskId, validationTypeCode, validationStatusCode, detail, created, modified.

* Rename to MLPSolutionVerification
* Add workflowId to MLPSolutionValidation
* ** QUESTION: do we need to add artifactId? **
* Expand SolutionValidationPK to include workflowId