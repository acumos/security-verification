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
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

=================================================
Acumos Security-Verification Design Specification
=================================================

This document describes the design for the Acumos platform Security-Verification
component and related capabilities. This component will be delivered in the
Boreas release.

-----
Scope
-----

The Security-Verification component (referred to here as S-V) addresses the
following goals of the Acumos project, as outlined by the
`Acumos TSC Security Committee <https://wiki.acumos.org/display/SEC>`_:

* models and related metadata that are contributed to an Acumos platform and
  distributed through a federated ecosystem of Acumos platforms must be
  verified to the extent possible, as

  * contributed under clear and compatible open source license(s)
  * free from security vulnerabilities

This in turn is based upon the
`bylaws of the Acumos project <https://www.acumos.org/wp-content/uploads/sites/61/2018/03/charter_acumos_mar2018.pdf>`_
which include the following responsibilities as described in section 2.i.vii

.. code-block:: text

  vii. establishing: (1) a vetting process for maintaining security and integrity of
  new and/or changed code and documentation, including vetting for
  malicious code and spyware and (2) a security issue reporting policy and
  resolution procedure;
..

The above bylaws apply to both the Acumos platform code and the federated
ecosystem of Acumos platform instances and models (referred to also as
"solutions" here) distributed through them. This document addresses the
Acumos project support for the latter goal.

Note that for license scanning, the S-V service is focused on the presence and
appropriateness of model license, as they related to the policies of the
Acumos platform operator. The S-V service is not specifically designed to verify
other potential "licensing" concerns, such as the RTU (right to use) of the user
for a model, as would be governed by a license contract between a model supplier
and model user. Those concerns go beyond the verification of a license as
provided by the model supplier, as a simple expression of the terms under which
that model is made available to users. The two purposes may share common
concepts and controls as provided by the Acumos platform, but this design only
addresses the former concern.

The S-V service will be scoped to address the essential concerns that we have
the resources and technology to address in an open source context. Beyond that,
Acumos community policies and offline practices will need to address the rest.
Following is a short list of some potential concerns, not all of which may be
considered of essential focus for machine-learning models which are collaborated
upon and distributed through a federated ecosystem such as Acumos. Acumos
community discussions will be used to determine which concerns we should put
in scope for implementation.

* Vulnerabilities in solutions and metadata distributed with those solutions
  including real/demo applications of the solutions, test data, documentation,
  etc. Such vulnerabilities can range across the following example set of risk
  categories, which can be viewed as "soft risks" (things which can be used by
  bad actors to increase risks) and "hard risks" (things which represent overt
  malicious risks, when embedded in the solutions and metadata):

  * soft risks, such as bad coding/documentation practices, e.g.

    * incluson of sensitive personal information (SPI) about real individuals,
      assumed to be a risk mostly in test data
    * inclusion of real system addresses or API URLs, along with credentials
      providing access to those systems/APIs
    * unprotected, potentially sensitive APIs
    * code quality issues that can result in unreliable behavior or create
      attack opportunities

  * hard risks, such as

    * malicious or compromised design in software components, either overt or
      inadvertent (e.g. by compromised/malicious code reuse, or importation),
      enabling e.g.

      * host system hacking
      * intrusion behavior, e.g. host/port probing
      * network behavior outside expected norms (e.g. for models, network
        behavior other than serving protobuf interfaces)
      * DDOS/botnets
      * creation of backdoors

    * known threats, e.g. viruses, trojans, etc as embedded in binaries,
      documents, rich media, etc

* Unclear, incomplete, incorrect, or incompatible licenses and/or copyright
  attribution

  * soft risks

    * lack of trust, inhibiting adoption of the solutions
    * perpetuating bad licensing/attribution practices

  * hard risks

    * unclear liabilities, e.g. due to

      * inclusion of undisclosed, GPL-family licensed code or other licenses
        with specific use-limitations or reservations
      * failure to properly license or attribute included solution software, or
        metadata (e.g. source code, documentation, test data, rich media, etc)

......................................
Implementation Approach Considerations
......................................

Given the importance of managing risks such as above, the key question is how
that capability needs to, or can be, implemented. This design assumes that the
goal of the S-V component of the Acumos platform is to provide a comprehensive,
as-much-automated-as-possible, platform-integrated service that fulfills the
goals. Thus the design below attempts to lay out an approach to that. This
design however may not be achievable in a single release or in full, since:

* project resource may be insufficient
* technical solutions to some of the goals may be unavailable, e.g.

  * machine-learning technologies are fairly new, and ability
    to detect malicious design in compiled models (e.g. in pickle files) may be
    limited technically
  * when compared to signatures of well-known or new threats to host systems or
    consumer devices (e.g. PCs) as supported by open source virus/malware scan
    tools, there may be limited experience thus limited libraries of threat
    signatures for compiled ML models

Thus alternate/fallback implementation approaches are described below, so that
as much of the goals as possible can be delivered in the Boreas release, or as
soon as possible afterward. These alternate approaches are based upon the
following assessments of how the bylaws goals related to potential implementation
approaches, such as:

* a comprehensive, as-much-automated-as-possible, platform-integrated service

  * this is the stated default approach, given that resources and technical
    solutions are available

* a hybrid approach of some manual processes, supplementing the automated
  platform capabilities, e.g.

  * manual admin of the platform capabilities, through configuration files that
    are provisioned on the platform hosts, and used by the components in the
    absence of portal-marketplace admin UI support for the same configuration
  * exporting solution packages (artifacts and metadata) for offline scanning,
    in the absence of integrated, automated scanning tools
  * maintaining the status of scans (e.g. unrequested, in-progress, successful,
    failed) as a key input to enabling/blocking workflows for solutions, through
    a manual but API-supported process, in the absence of automated updates of
    status based upon integrated scanning

* a fully manual, open source toolset-supported process that is ensured by
  establishment of community policies and related practices

  * in this case there may be no specific platform-integrated support for
    scanning, verification status management, policy definition or control of
    workflows per those policies, etc
  * open source toolsets and user guides however could be provided to help
    operators/admins to fulfill the requirements of their company and of the
    Acumos ecosystem
  * beyond the above, a priority would be placed on a "trust but verify"
    approach to policy adherence and modeler behaviors that support best
    practices and policies

Depending on how the Acumos community prioritizes the goals of S-V, the
various approaches above, and how successful the S-V team is in resourcing and
addressing technical challenges of the design below, various of these
hybrid/manual approach elements may be implemented in the Athena release.

............................
Previously Released Features
............................

This is the first release of S-V.

........................
Current Release Features
........................

The features planned for delivery in the current release ("Boreas") are:

* scanning for license/vulnerability issues in all models and related data
* a default set of open source license/security scan tools, which can be
  replaced in a "plug and play" manner with tools as preferred by Acumos
  platform operators
* a default set of success criteria for license/security scans, which can
  be easily customized by Acumos platform operators
* integration of scanning at various points in Acumos platform workflows
* integration of scan result checking gates at various points in Acumos
  platform workflows
* Acumos platform admin control of the scanning and gate check points

  * option to use the default internal scan tools, or an offline process for
    scanning
  * option to invoke scanning in workflows

    * upon completion of model onboarding
    * upon completed addition/update of artifacts or "documents" (documents,
      test data, source code archives)
    * upon request to deploy a model
    * upon request to download model artifacts or documents
    * upon request to share a model with another user
    * upon request to publish a model to a company or public marketplace

  * option to define workflow gates that must be passed, in order to allow the
    workflow to be executed, including

    * enable checking prior to workflows

      * deploy a model
      * download a model
      * share a model
      * publish to company marketplace
      * publish to public marketplace

    * what must have been checked, and what are the acceptable results

      * license scan successful: yes, no (default)
      * security scan successful: yes, no (default)

The combination of the two admin options enables the platform to support
customization and optimization of S-V processes for an Acumos instance.
For example:

* scans can be invoked as early or as late as desired, in the lifecycle of a
  model, to accommodate local Acumos platform processes or policies
* since "scans" may include offline processes that take time to complete,
  the admin may allow some workflows to be proceed, while others are blocked.
  For example, if licensing has not been verified/approved, the admin may allow
  deployment to a private cloud to publishing to a company marketplace, but not
  deployment to a public cloud or publishing to a public marketplace.
* the Scanning Service will only execute scans as needed for any new/updated
  artifacts/metadata, since a record of earlier scans will be retained as a
  artifact related to the solution.

------------
Architecture
------------

The following diagram illustrates the integration of S-V into an Acumos platform:

.. image:: images/security-verification-arch.png

.....................
Functional Components
.....................

The S-V service will include two components, and one component microservice:

* Security Verification Library ("S-V Library"): implemented as a Java library
  that Acumos components include in their build processes, this library provides
  an interface that abstracts the status checking and scan invocation processes,
  and determines for the current workflow:

    * whether a scan process needs to be invoked, and invoking it if so
    * whether the workflow should be blocked based upon the S-V requirements
      established by the platform admin, given the current status of S-V for
      the model

  * uses CDS site-config data to determine when to invoke scanning
  * uses CDS site-config data and solution data to determine if workflows are
    allowed
  * runs as a always-on service under docker-ce or kubernetes

* Scanning Service: this is the backend to the S-V service, which

  * provides a scanning API to execute scan operations as needed using scanning
    tools for license and vulnerabilities
  * allows Acumos operators to use a default set of scan tools, or to integrate
    other tools via a plugin-style interface
  * runs as an always-on service under docker, or an on-demand job under
    kubernetes

..........
Interfaces
..........

The S-V service provides the following library functions and APIs.

+++++++++++++++++++++
Security Verification
+++++++++++++++++++++

This Java library is included in the build specification (pom.xml) of calling
components in order to assess the S-V status of components as it affects
Acumos workflows, and to scan invocation as needed.

The S-V library function will take the following parameters:

* solutionId: ID of a solution present in the CDS
* revisionId: ID of a version for a solution present in the CDS
* workflowId: one of

  * created: model has been onboarded
  * updated: model artifacts/metadata have been updated
  * deploy: request to deploy received
  * download: request to download received
  * share: request to share received
  * publishCompany: request to publish to company marketplace received
  * publishPublic: request to publish to public marketplace received

In response, the S-V library will provide the following result parameters:

* workflow allowed: boolean (true|false)

  * true: the S-V service is either not configured to gate the current
    workflow, or the gate conditions have been fulfilled
  * false: the gate conditions for the workflow have not been fulfilled, as
    defined by the Acumos system admin

* reason: text description of the reason for workflow being blocked, for
  presentation to the user, e.g.

  * "security verification incomplete"
  * "internal error" (only applies when an invalid workflow has been indicated,
    or other unexpected conditions such as no matching solution/revision found)

+++++++++++++++
Scan Invocation
+++++++++++++++

This API initiates a S-V scan process as needed, based upon the current status
of the model and any earlier scans in-progress or completed.

The base URL for this API is: http://<scanning-service-host>:<port>, where
'scanning-service-host' is the routable address of the scanning service in the
Acumos platform deployment, and port is the assigned port where the service is
listening for API requests.

* URL resource: /scan/{solutionId}/{revisionId}

  * {solutionId}: ID of a solution present in the CDS
  * {revisionId}: ID of a version for a solution present in the CDS

* Supported HTTP operations

  * GET

    * Response

      * 200 OK

        * meaning: request completed, detailed status in JSON body
        * body: JSON object as below

          * status: "scan completed"

      * 202 ACCEPTED

        * meaning: request accepted, detailed status in JSON body
        * body: JSON object as below

          * status: "scan in progress"

      * 404 NOT FOUND

        * meaning: solution/revision not found, details in JSON body. NOTE: this
          response is only expected in race conditions, e.g. in which a scan
          request was initiated when at the same time, the solution was deleted
          by another user
        * body: JSON object as below

          * status: "invalid solutionId"|"invalid revisionId"

++++++++++++++++++++
External Scan Result
++++++++++++++++++++

The Scanning Service exposes the following API to allow optional external scan
functions/processes to report back on the status of scans. See "External Scans"
below for description of how external scan functions/processes are integrated,
and what happens to the results from them when reported.

The base URL for this API is:
http://<scanning-service-host>:<port>, where 'scanning-service-host' is the
externally routable address of the verification service in the Acumos platform
deployment, and port is the assigned externally accessible port where the
service is listening for API requests.

* URL resource: /result/{solutionId}/{revisionId}

  * {solutionId}: ID of a solution present in the CDS
  * {revisionId}: ID of a version for a solution present in the CDS

* Supported HTTP operations

  * POST

    * Response

      * 200 OK

        * meaning: request completed, detailed status in JSON body
        * body: JSON object as below

          * status: "results posted"

      * 404 NOT FOUND

        * meaning: solution/revision not found, details in JSON body. NOTE: this
          response is expected in race conditions, e.g. in which an external
          scan process was in progress, the solution was deleted from the
          Acumos platform
        * body: JSON object as below

          * status: "invalid solutionId"|"invalid revisionId"

----------------
Component Design
----------------

..............................
Common Data Service Data Model
..............................

The following data model elements are defined/used by the S-V service:

* solution

  * revision

    * artifact: the Scanning Service will retrieve all solution artifacts in the
      process of scanning or verifying status of earlier scans, and create one
      new type SR artifact named scanresult.json, as a record of scan results

    * a new artifact type is needed as below, with a related artifact type

      * Scan Result, attribue type SR, by convention named "scanresult.json"

    * new revision attributes are needed as below, and a new API is needed to
      retrieve and set values for these attributes

      * verifiedLicense: success | failure | in-progress | unrequested (default)
      * verifiedSecurity: success | failure | in-progress | unrequested (default)

.............................
Security Verification Library
.............................

The Security Verification Library ("S-V Library") will be integrated as a
callable library function by Java-based components, through reference in their
pom.xml files. The S-V library has the following dependencies, which must be
specified in the template used to create the calling component:

* environment

  * common-data-svc API endpoint and credentials
  * scanning-service API endpoint

Acumos components will call the S-V library function when they need to check
if a workflow should proceed, based upon the admin requirements for verification
related to that workflow, and the status of verification for a solution/revision.

In addition to checking if the requested workflow should proceed the S-V library
will invoke scan operations as needed, as described below.

The S-V library first:

* validates the requested workflowId, and if invalid returns 
  “workflow allowed”=”false” and reason=”invalid workflowId”
* retrieves the solutionId and revisionId details from the CDS, and if either
  are not found, return “workflow allowed”=”false” and 
  reason=”solution/revision not found” as appropriate
* retrieves and deserializes the verification site-config key via the CDS
  site-config-controller
* retrieves the solutionId details from the CDS
* if the modelTypeCode is PR (predictor) and the toolkitTypeCode is CP
  (composite solution)

  * retrieves the CD (CDUMP) artifact for the revisionId
  * deserializes the CDUMP artifact and for each member of the nodes array
    included in the CDUMP object

    * using the member nodeSolutionId, retrieves the list of revisions for the
      solutionId
    * for the revision that matches the nodeVersion for the member of the nodes
      array, executes the rest of this process, using its revisionId

* otherwise this is a simple model and the process below is applied to the model

If either of the verification securityScan or licenseScan attribute
array members for the workflowId value is "true", the S-V library invokes a
scan as required:

* if the CDS revisionId attributes verifiedSecurity or verifiedLicense are
  "unrequested", invoke the Scan Invocation API with the solutionId and
  revisionId, and continue

The S-V library then will use the following process to determine whether the
requested workflow is allowed:

* as local variables, set "workflow allowed"="true" and "reason"=""
* If the CDS site-config verification.licenseVerify is "true" and the revision
  attribute securityScan is “unrequested”, “in-progress”, or “failure”, set
  “workflow allowed”=”false” and “reason”=”security scan incomplete|security
  scan failure” as appropriate
* If the CDS site-config verification.securityVerify is "true" and the revision
  attribute licenseScan is “unrequested”, “in-progress”, or “failure”, set
  “workflow allowed”=”false” and add ", license scan incomplete|license scan
  failure” as appropriate to “reason”
* Return the values for "workflow allowed" and "reason"

................
Scanning Service
................

The Scanning Service will be deployed as an always-running platform
service under docker or a on-demand job under kubernetes. It has the following
dependencies, which must be specified in the service template used to create the
service:

* environment

  * common-data-svc API endpoint and credentials
  * nexus-service API endpoint and credentials
  * docker-service API endpoint and credentials
  * optional API endpoint of external scanning service to be integrated
  * site-config verification key default

* ports: Acumos platform-internal port used for serving APIs (NOTE: this must
  also be mapped to an externally-accessible port so that the service can
  provide the /scanresult API to external scanning services)

* logs volume: persistent store where the service will save logs. Internal to
  the service, this is mapped to folder /var/acumos/scanning, and will
  contain the distinct log files: application.log, debug.log, and error.log.
  NOTE: logging details here need to be aligned with the common logging design
  based upon log delivery to the ELK component.

The Scanning Service provides a default set of scanning tools and optionally
integrates with an external scanning service. See the `External Scan`_
description below for details on external scanning service integration.

The Scanning Service will record and use the results of scans in a new artifact
type as described in `Scan Result`_, associated with the scanned revision. This
artifact is central to various design goals of the S-V service, e.g.:

* maintaining a semantic, human-readable, and easily exportable record of scans
  related to a revision
* preserving the history of scan results for previous solution revisions, by
  versioning the Scan Result artifacts and creating a new Scan Result artifact
  for each scan.
* making the history of scan results available to those who obtain the solution
  though sharing, downloading, or federated subscription
* optimizing the overhead for scanning by only scanning previously unscanned
  artifacts/metadata

++++++++++++++++
Site-Config Data
++++++++++++++++

The S-V Service leverages a new site-config key "verification", which as
a serialized JSON object defines the related policies for the platform, as a set
of flags that control the four main features of the S-V service per the needs of
the Acumos platform operator:

  * workflow gates, i.e. when evidence of successful license and vulnerability
    scans is required, for a workflow to be allowed
  * at which workflows license and security scans should be invoked
  * whether an internal or external license scanning service should be used
  * which license types are pre-approved for use with solutions

Default values for these options are set through the component configuration data
for the Scanning Service, and can be customized by the platform operator prior
to deployment of the S-V Service. When the S-V Scanning Service starts, if the
verification site-config key is not present, it will be initialized:

* via the CDS site-config-controller API "/config", check for existence of the
  site-config "verification" key, and create it as needed using the
  Spring environment variable "siteConfig.verification"

Additionally, the workflow-related flags can be updated by a platform admin
through Portal UI screens described in `Portal-Marketplace`_. The S-V library
will provide a function via which the Portal-FE can obtain a deserialized
structure for the config key, which is used to present a Site Admin UI screen
where the values can be reviewed and updated.

The key structure is described below:

* verification

  * allowedLicense: array of attributes that can be used to recognize licenses
    that are pre-approved for use with models in the platform

    * type: SPDX | <other type identifiers, e.g. "VendorX">
    * value: a unique string that can be used to identify the license in
      LICENSE.txt files associated with models as a document, or in other files
      (e.g. source code or other documents). Examples include
      `SPDX license identifiers <https://spdx.org/licenses/>`_ and other values
      e.g. identifying a vendor-specific license.

  * externalScan: boolean indicating whether the Scanning Service should use an
    external scan process as described in `External Scan`_. Defaults to "false".

  * licenseScan: license scanning requirements for workflows. See the
    definition of workflowId above for explanation of the workflow names. Each
    workflow is associated with a boolean value, which if "true" indicates
    that a license scan should be invoked at this workflow point.

    * created: true | false (default)
    * updated: true | false (default)
    * deploy: true | false (default)
    * download: true | false (default)
    * share: true | false (default)
    * publishCompany: true | false (default)
    * publishPublic: true | false (default)

  * securityScan: security scanning requirements for workflows. See
    the definition of workflowId above for explanation of the workflow names.
    Each workflow is associated with a boolean value, which if "true" indicates
    that a security scan should be invoked at this workflow point.

    * created: true | false (default)
    * updated: true | false (default)
    * deploy: true | false (default)
    * download: true | false (default)
    * share: true | false (default)
    * publishCompany: true | false (default)
    * publishPublic: true | false (default)

  * licenseVerify: license scanning verification requirements for workflows.
    See the definition of workflowId above for explanation of the workflow
    names. Each workflow is associated with a boolean value, which if "true"
    indicates that a successful license scan must have been completed before
    the workflow begins.

    * deploy: true | false (default)
    * download: true | false (default)
    * share: true | false (default)
    * publishCompany: true | false (default)
    * publishPublic: true | false (default)

  * securityVerify: security scanning verification requirements
    for workflows. See the definition of workflowId above for explanation of
    the workflow names. Each workflow is associated with a boolean value,
    which if "true" indicates that a successful security scan must have
    been completed before the workflow begins.

    * deploy: true | false (default)
    * download: true | false (default)
    * share: true | false (default)
    * publishCompany: true | false (default)
    * publishPublic: true | false (default)

An example serialized value for the site-config verification key is shown below.

.. code-block:: text

  'siteConfig':'{
    "verification": {
      "externalScan":"false",
      "allowedLicense": [
        { "type":"SPDX", "value":"Apache-2.0" },
        { "type":"SPDX", "value":"CC-BY-4.0" },
        { "type":"SPDX", "value":"BSD-3-Clause" },
        { "type":"VendorA", "value":"VendorA-OSS" },
        { "type":"CompanyB", "value":"CompanyB-Proprietary" }
      ]
      "licenseScan": {
        "created":"true",
        "updated":"true",
        "deploy":"false",
        "download":"false",
        "share":"false",
        "publishCompany":"false",
        "publishPublic":"false"
      },
      "securityScan": {
        "created":"true",
        "updated":"true",
        "deploy":"false",
        "download":"false",
        "share":"false",
        "publishCompany":"false",
        "publishPublic":"false"
      },
      "licenseVerify": {
        "deploy":"true",
        "download":"false",
        "share":"false",
        "publishCompany":"true",
        "publishPublic":"true"
      },
      "securityVerify": {
        "deploy":"true",
        "download":"false",
        "share":"false",
        "publishCompany":"true",
        "publishPublic":"true"
      }
    }
  }'
..

+++++++++++
Scan Result
+++++++++++

Revision artifacts of type SR (scanresult.json, referred to here as the
"Scan Result") will record the result of scanning for a revision. For each scan,
a new Scan Result version will be created, so that the history of scanning
is preserved. This or later releases will provide admins with the ability to
limit the number of Scan Result versions maintained for a revision.

The Scan Result will be initialized at the start of a scan, and will look like:

.. code-block:: text

  { "solutionId" : "<solutionId from the API request>",
    "revisionId" : "<revisionId from the API request>",
    "scanTime" : "<epoch time value when the scan was started>"
    "licenseScan" : "in-progress",
    "securityScan" : "in-progress",
      "license" : {
        "type" : "",
        "value" : "",
        "approved" : "",
        "reason" : []
      },
    "description" : {
      "checksum" : "<sha1 checksum of the current description>",
      "licenseScan" : "in-progress",
      "license" : {
        "type" : "",
        "value" : "",
        "approved" : "",
        "reason" : []
      }
    },
    "documents" : [],
    "artifacts" : []
  }
..

where:

* solutionId and revisionId identify the scanned model
* scanTime records the epoch time when the scan was started
* licenseScan hold the result of scanning

  * in-progress: scanning has started
  * success: scanning has completed and succeeded
  * failure: scanning has completed and failed
  * skipped: scan was not performed, e.g. as it is not required for the type
    of object

* securityScan hold the result of scanning (in-progress|success|failure|skipped)
* license is an object that records the details of the identified/derived license

  * type is the type of recognized license identifier, and is either "SPDX" or
    one of the provisioned approvedLicense types from the site-admin
    verification key
  * value is the recognized license identifier string that was found
  * approved (true|false) indicates whether the license meets the admin policy
    and Scanning Service criteria for approving a license, at the time of scan
  * reason is an array of strings which further disclose why the license was
    determined to be approved or not. Examples:

    * "In approved list"
    * "Not in approved list"
    * "artifact <artifactId> license incompatible with revision": used when
      an artifact taints the overall approval for the revision, due to license
      incompatibility
    * "document <documentId> license incompatible with revision": used when
      a document taints the overall approval for the revision, due to license
      incompatibility
    * "file <file path> license not approved": used when a file contained in an
      archive taints the overall approval for the document or artifact, because
      the license for that file is not in the approved list
    * "file <file path> in document <documentId> license incompatible with
       revision": used when a file contained in a document taints the overall
       approval for the revision, due to an incompatible license
    * "file <file path> in artifact <artifactId> license incompatible with
      revision": used when a file contained in an archive taints the overall
      approval for the revision, due to an incompatible license
    * "Warning: model.zip license.txt does not match revision document
      license.txt": used to indicate a potential inconsistency in the license
    * "No recognized license found": used to indicate failure to recognize any
      license

* documents is an array (initially null) to hold the results of document scans
* artifacts is an array (initially null) to hold the results of artifact scans

As document scan records are added to the Scan Result, they will look like:

.. code-block:: text

  { "id" : "<ID attribute of the document>",
    "version" : "<version attribute of the document>",
    "uri" : "<uri attribute of the document>",
    "checksum" : "<checksum attribute of the document>",
    "licenseScan" : "in-progress",
    "securityScan" : "in-progress",
    "license" : {
      "type" : "",
      "value" : "",
      "approved" : "",
      "reason" : []
    }
  }
..

As artifact scan records are added to the Scan Result as array members of the
"artifact" attribute, they will look like:

.. code-block:: text

  { "id" : "<artifactId>",
    "version" : "<artifactVersion>",
    "uri" : "<artifactUri>",
    "checksum" : "<nexusSha1Checksum>",
    "licenseScan" : "in-progress",
    "securityScan" : "in-progress",
    "license" : {
      "type" : "",
      "value" : "",
      "approved" : "",
      "reason" : []
    }
  }
..

++++++++++++++
Scan Execution
++++++++++++++

The S-V library will call the Scan Invocation API when a scan is required per
the admin options for the S-V service.

Two types of scan processes are supported by the S-V service: internal and
external. In both cases, the first step in the scan process is to check whether
a new scan is required. A new scan will not be required if a prior Scan Result
exists and no relevant revision data has changed since the last scan. The
following process is used to check if a scan is required, and to invoke it if
needed:

* retrieve the revision data for use here and later

  * retrieve the array of revisions for the solutionId, find the entry for the
    revisionId being scanned, and save the accessTypeCode value for later use
    (GET /solution/{solutionId}/revision)
  * retrieve the description for the revisionId/accessTypeCode
    (GET /revision/{revisionId}/access/{accessTypeCode}/descr)
  * retrieve the array of documents for the revisionId/accessTypeCode
    (GET /revision/{revisionId}/access/{accessTypeCode}/document)
  * retrieve the array of artifacts for the revisionId
    (GET /revision/{revisionId}/artifact)

* search the artifact array for an artifact type SR (Scan Result)
* if no Scan Result artifact was found, select the type of scan and invoke it
  as described below
* else retrieve the latest Scan Result artifact revision
* if any one of the following tests are true, select the type of scan and invoke
  it as described below

  * the SHA1 checksum of the current description does not match the description
    checksum in the Scan Result
  * for each document in the document array

    * the document ID is not found in the Scan Result documents array, OR
    * the SHA1 checksum of the current document does not match the document
      checksum in the Scan Result

  * for each artifact of type MI (model.zip), DI (docker image), or MD
    (metadata.json), in the document array

    * the artifact ID is not found in the Scan Result artifacts array, OR
    * the SHA1 checksum of the current artifact does not match the artifact
      checksum in the Scan Result

* if a scan is required, and the site-config key "verification.externalScan" is
  "false", invoke an `Internal Scan`_, otherwise invoke an `External Scan`_.

Internal scans will be designed to complete quickly, so that for a specific
solution revision, no subsequent request queuing is required.

From the perspective of the Scanning Service, external scans will take an
unknown amount of time, thus multiple external scans can be invoked for the
same revision.

,,,,,,,,,,,,,
Internal Scan
,,,,,,,,,,,,,

Where noted below, details of the internal scan process are TBD.

The CDS holds three types of data for revisions: a description, a set of
documents, and a set of artifacts. Notes on the approach to scanning these types
of data objects:

* The description is a text object that is used in the UI presentation of the
  model on the Portal. It will only be scanned for license notices.
* Documents may be of any arbitrary type, e.g. media (images, video), archives
  (e.g. training data, source code, documentation), or rich/plain text
  documents. Documents uploaded as a single text file and text files inside
  archive documents will be scanned. Files that are recognized as code will be
  scanned for license and vulnerability, and other text documents will be
  scanned for license only.
* artifacts are the modeler-onboarded or platform-generated files which make up
  the model revision. Of these, only the type MI (model.zip), DI (docker image),
  and MD (metadata.json) will be scanned. As an archive, the model.zip will be
  scanned using the same approach as described for archive documents.

The overall process for scanning these revision data types is:

* initialize a new `Scan Result`_ object with the solutionId and revisionId
  being scanned
* `License Scan`_ the description for the revisionId/accessTypeCode
* for each document of the revisionId/accessTypeCode

  * add a new array member to the Scan Result documents attribute, setting the
    id, version, and uri attributes per the document array member
  * if the document name is "license.txt" (ignoring case), `License Scan`_ it
  * if the document is an archive, `License Scan`_ and `Security Scan`_ it
  * if the document is a recognized source file type, `License Scan`_ and
    `Security Scan`_ it
  * if the document is a plain text file, `License Scan`_ it
  * if the document is a rich text file, `License Scan`_ it if supported by this
    version of S-V

* for each artifact of the revisionId

  * if the artifactTypeCode is LG (log), CD (CDUMP), TG (TGIF), PJ (protobuf
    specification), or SR (scanresult.json), ignore the artifact
  * add a new array member to the Scan Result artifacts attribute, setting the
    id, version, and uri attributes per the artifact array member
  * For artifactTypeCode MI (model zip file)

    * if there is a file named "license.txt" (ignoring case) in the root folder,
      `License Scan`_ it, and

      * if there is no current revisionId document named "license.txt", save the
        file as a new revisionId document named "license.txt"
      * else if the license.txt file from the archive does not match exactly the
        current license.txt document, add "Warning: model.zip license.txt does
        not match revision document license.txt" to the Scan Result
        license.reason array

    * if there are other files in the /scripts/user_provided/ folder or its
      subfolders, `License Scan`_ and `Security Scan`_ each file

  * For artifact type DI (docker image)
  * For artifact type MD (metadata.json)

When all documents and artifacts have been scanned, the Scanning Service:

* updates the revision licenseScan attribute to match the Scan Result
* updates the revision securityScan attribute to match the Scan Result
* adds a new version of type SR artifact "scanresult.json" to the revision

''''''''''''
License Scan
''''''''''''

The "License Scan" process refers to scanning a file for well-known strings
that identify the type of license, using the list of
`SPDX license identifiers <https://spdx.org/licenses/>`_, and the set of
approvedLicense values from the site-config verification key.

The overall process for license scanning for licenses is to assess licenses using a
hierarchical approach in which all objects are expected to have approved
licenses (in some cases derived from child objects), and child object licenses
(where found) are expected to be compatible with their parent object(s) license.
The result of these checks "roll up" to the parent object and ultimately to the
revision, for which any unapproved or incompatible licenses will cause an overall
license check failure result. The process includes these steps:

* assess licenses against a set of well-known types, and their status as approved
  licenses from the site-config verification key
* assess the license that applies to the revisionId overall, which is expected to
  be provided in (and will be derived from) a "license.txt" document associated
  with the revisionId
* assess licenses for other associated documents
* assess the license for the model.zip artifact
* where an item (artifact, document, or file contained in one of them contains
  a recognized license, assess the compatibility of that license with the
  license associated with the parent object
* for the revision overall, verify that

  * at least one of the description, license.txt document (if present),
    documents, or MI (mode.zip) artifact contain an approved license, AND
  * none of the items above contain an unapproved license

The hierarchy of objects is:

* revision: the overall parent object, for which at least one child object must
  have an approved license, and for which no child objects contain an
  unapproved license

  * document named "license.txt"
  * other plain/rich text documents with embedded license
  * archive documents (e.g. a zip archive with source files, or documentation)

    * "license.txt" file in a folder (and hierarchy of subfolders)

      * file in a folder

  * MI (model.zip) type artifact

    * "license.txt" file in a folder (and hierarchy of subfolders)

      * file in a folder

In general, the License Scan process for a specific file will involve the
following steps, with specific variations noted where applicable:

* If a match is found

  * if the result applies to the revision, description, a document, or artifact

    * if the identified license is included as an approvedLicense value,
      update the associated license attribute

      * set license.type to the approvedLicense.type
      * set license.value to the recognized string
      * set license.approved=true
      * add "In approved list" to the license.reason array

    * otherwise if the identified license is included in the list of well-known
      SPDX license identifiers, update the associated license attribute

      * set license.type to "SPDX"
      * set license.value to the recognized string
      * set license.approved=false
      * add "Not in approved list" to the license.reason array

  * else if the result applies to a file contained in a document or the MI
    (model.zip) artifact

    * if the license is not included as an approvedLicense value, update the
      associated license attribute for the containing item

      * set license.approved=false
      * add "file <file path> license not approved" to the license.reason array

    * else if the license is determined to be incompatible (criteria TBD) with
      the license of the parent object, update the license attribute for the
      parent and its parent if any

      * set license.approved=false
      * add a reason string to the license.reason array, as applicable

        * for an artifactId: "artifact <artifactId> license incompatible with
          revision"
        * for a documentId: "document <documentId> license incompatible with
          revision"
        * for a file contained in a document (archive):
          "file <file path> in document <documentId> license incompatible with
          revision"
        * for a file contained in a MI (model.zip) artifact: "file <file path>
          in artifact <artifactId> license incompatible with revision"

* If a match was not found

  * if the result applies to the revision, description, a document, or artifact,
    update the associated license attribute

    * set license.type to "unknown"
    * set license.approved=false
    * add "No recognized license found" to the license.reason array

Note that the lack of licenses in source files, documents, or folders in source
archives is not a critical issue, since parent object licenses will apply to the
child objects. If there are no approved licenses in any parent object, that will
still result in scan failure.

'''''''''''''
Security Scan
'''''''''''''

Goals and methods for security scans are TBD.

'''''''''''''
Code Scanning
'''''''''''''

Beyond simple scanning for internally declared licenses, scanning code involves
special requirements for license and security. In summary any "closely linked"
code must be free from serious security vulnerabilities, and clearly/compatibly
licensed. This is a complex process that requires use of other tools which can
assess the license and security of any code that is directly embedded (e.g.
"imported" in python) in the software as built into an application.

Tools that support such scan requirements are TBD.

,,,,,,,,,,,,,
External Scan
,,,,,,,,,,,,,

External scans will depend upon unspecified tools and processes that are
provided by the Acumos platform operator. The role of the Acumos platform in
this case is only to support:

* the export of all relevant revision data as an archive, that can be
  processed externally
* the importing of a scan results file that will be stored as a
  revision artifact, at some later point

For external scans, the Scanning Service:

* retrieves the revision description, documents per the revision accessTypeCode,
  and revision artifacts of type MI (model.zip), DI (docker image),
  MD (metadata.json), and SR (scanresult.json)
* creates a scanresult.json described in `Scan Result`_ as a template for later
  completion and return by the external scan process
* creates a zip archive named sol-<solutionId-rev-<revisionId>.zip with

  * the description in a file named "description.txt" in the root folders
  * the scanresult.json file in the root folder
  * the documents in a "documents" folder
  * the artifacts in an artifacts folder

* places the archive into an external-user-accessible persistent volume, in a
  folder "/var/acumos/security-verification/external-scan"

At that point, admins or automated systems can access the archive for offline
scan execution.

At some later time, the API described in `External Scan Result`_ will be called
by admins/systems external to the Acumos platform, to report the scan results.
The scan result reports may be partial, or complete.

After receiving a scanresult.json file via the `External Scan Result`_ API, the
Scanning Service:

* checks if the solution/revision referenced in the Scan Result still exists,
  and if not, returns a 404 NOT FOUND response.
* if the solution/revision is found, the Scanning Service

  * updates the revision licenseScan attribute to match the Scan Result
  * updates the revision securityScan attribute to match the Scan Result
  * adds a new version of type SR artifact "scanresult.json" to the revision

----------------------------------
Impacts to other Acumos Components
----------------------------------

...................
Common Data Service
...................

The Common Data Service will implement the new CDS data model elements
described in `Common Data Service Data Model`_, and provide APIs to read/update
that data.

..................
Portal-Marketplace
..................

Calls will be required to the S-V library per the supported workflow scanning
options and workflow verification gates described under `Security Verification`_
section. The specific impacts on the Portal-Marketplace component will be
analyzed and described here.

The Portal-Marketplace UI for users and admins will be impacted in various ways.
The impacts will be described here, and are expected to include at a high level:

* UI elements conveying that workflows are blocked due to required/incomplete
  solution verification, e.g. grayed out workflow options with tooltip hints,
  popup dialogs explaining why a workflow can't be completed at this time, or
  additional notification entries.
* admin of the options for S-V service as described under
  `Current Release Features`_. This could for example take the form of a single
  tab under the Site Admin section, in which the four sub-keys of the
  "verification" key are presented in table format, with the flags of each
  sub-key represented by a checkbox, where unchecked represents "false". For
  example:

  * NOTE: in the following example, "[ ]" represents an unchecked box, and
    "[NA]" represents a greyed-out box

.. csv-table::
    :header: "Workflow", "licenseScan", "SecurityScan", "licenseVerify", "SecurityVerify"
    :widths: 60, 10, 10, 10, 10
    :align: left

    "created", "[ ]", "[ ]", "[NA]", "[NA]"
    "updated", "[ ]", "[ ]", "[NA]", "[NA]"
    "deploy", "[ ]", "[ ]", "[ ]", "[ ]"
    "download", "[ ]", "[ ]", "[ ]", "[ ]"
    "share", "[ ]", "[ ]", "[ ]", "[ ]"
    "publishCompany", "[ ]", "[ ]", "[ ]", "[ ]"
    "publishPublic", "[ ]", "[ ]", "[ ]", "[ ]"
..
