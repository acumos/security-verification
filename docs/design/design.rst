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
component and related capabilities.







----------------
Component Design
----------------

..............................
Common Data Service Data Model
..............................

The following data model elements are defined/used by the S-V service:

* config: the following new configKey values are defined

  * verification: serialized JSON structure as defined below, initialized by
    the Verification Service upon startup, if not already present in the CDS.
    This element defines all the options for the configuration of the S-V
    service. It is used by the Portal-FE service in presenting options for admin
    users, and updated by the Portal-BE service based upon any changes to the
    options by an admin.

    * license-scan: license scanning requirements for workflows. See the
      definition of workflowId above for explanation of the workflow names. Each
      workflow is associated with a boolean value, which if "true" indicates
      that a license scan should be invoked at this workflow point.

      * created: true | false (default)
      * updated: true | false (default)
      * deploy-private: true | false (default)
      * deploy-public: true | false (default)
      * download: true | false (default)
      * share: true | false (default)
      * publish-company: true | false (default)
      * publish-public: true | false (default)
      * subscribe: true | false (default)

    * vulnerability-scan: vulnerability scanning requirements for workflows. See
      the definition of workflowId above for explanation of the workflow names.
      Each workflow is associated with a boolean value, which if "true" indicates
      that a vulnerability scan should be invoked at this workflow point.

      * created: true | false (default)
      * updated: true | false (default)
      * deploy-private: true | false (default)
      * deploy-public: true | false (default)
      * download: true | false (default)
      * share: true | false (default)
      * publish-company: true | false (default)
      * publish-public: true | false (default)
      * subscribe: true | false (default)

    * license-verify: license scanning verification requirements for workflows.
      See the definition of workflowId above for explanation of the workflow
      names. Each workflow is associated with a boolean value, which if "true"
      indicates that a successful license scan must have been completed before
      the workflow begins.

      * deploy-private: true | false (default)
      * deploy-public: true | false (default)
      * download: true | false (default)
      * share: true | false (default)
      * publish-company: true | false (default)
      * publish-public: true | false (default)
      * subscribe: true | false (default)

    * vulnerability-verify: vulnerability scanning verification requirements
      for workflows. See the definition of workflowId above for explanation of
      the workflow names. Each workflow is associated with a boolean value,
      which if "true" indicates that a successful vulnerability scan must have
      been completed before the workflow begins.

      * deploy-private: true | false (default)
      * deploy-public: true | false (default)
      * download: true | false (default)
      * share: true | false (default)
      * publish-company: true | false (default)
      * publish-public: true | false (default)
      * subscribe: true | false (default)

* solution

  * revision

    * artifact: the Scanning Service will retrieve all solution artifacts in the
      process of scanning or verifying status of earlier scans, and create one
      new artifact named "scanresult.json" as a record of scan results.

    * new revision attributes are needed as below, and a new API is needed to
      PUT updated values for these attributes 

      * verified-license: success | failure | in-progress | unrequested (default)
      * verified-vulnerability: success | failure | in-progress | unrequested (default)

....................
Verification Service
....................

The Verification Service will be deployed as an always-running platform
service under docker or kubernetes. It has the following dependencies, which
must be specified in the service template used to create the service:

* environment

  * common-data-svc API endpoint and credentials
  * scanning-service API endpoint

* ports: Acumos platform-internal port used 

* logs volume: persistent store where the service will save logs. Internal to
  the service, this is mapped to folder /var/acumos/verification, and will
  contain the distinct log files: application.log, debug.log, and error.log.
  NOTE: logging details here need to be aligned with the common logging design
  based upon log delivery to the ELK component.

+++++++++++++++++++
Verification Status
+++++++++++++++++++

Acumos components will call the Verification Status API when they need to check
if a workflow should proceed, based upon the admin requirements for verification
related to that workflow, and the status of verification for a solution/revision.

The Verification Service will use the following process to determine the API
result:

* If the requested workflowId is invalid return 400 BAD REQUEST with status
  "invalid workflowId", and exit.
* If the requested solutionId or revisionId are not found in the CDS return
  404 NOT FOUND with status as appropriate to the error found, and exit.
* If the CDS config license-verify attribute array member for the request
  workflowId value is "false" and the CDS config vulnerability-verify attribute
  array member for the request workflowId value is "false", return 200 OK with
  status "workflow permitted" message "workflow not gated", and exit.
* If the CDS config vulnerability-verify attribute array member for the request
  workflowId value is "true" and the CDS
  solution/{solutionId}/revision/{revisionId} attribute verified-vulnerability
  value is "unrequested, "in-progress", or "failure", add status
  "workflow not permitted" and message "vulnerability scan <value>" per the
  verified-vulnerability value to the response.
* If the CDS config license-verify attribute array member for workflowId
  value is "true" and  and the CDS
  solution/{solutionId}/revision/{revisionId} attribute verified-license
  value is "unrequested, "in-progress", or "failure", add status
  "workflow not permitted" and message "license scan <value>" per the
  verified-license value to the response.
* If after the above steps, the response body status attribute is unset, return
  200 OK with status "workflow permitted" and message
  "all workflow gates cleared".

+++++++++++++++
Scan Invocation
+++++++++++++++

Acumos components will call the Scan Invocation API at the supported workflow
points, to invoke a scan based upon the admin requirements for scanning
related to that workflow.

The Verification Service will use the following process to determine the API
result:

* If the requested workflowId is invalid return 400 BAD REQUEST with status
  "invalid workflowId", and exit.
* If the requested solutionId or revisionId are not found in the CDS return
  404 NOT FOUND with status as appropriate to the error found, and exit.
* If the CDS config license-scan attribute array member for the request
  workflowId value is "false" and the CDS config vulnerability-scan attribute
  array member for the request workflowId value is "false", return 200 OK with
  status "scan not required", and exit.
* If the CDS config license-scan attribute array member for the request
  workflowId value is "true" and the CDS config vulnerability-scan attribute
  array member for the request workflowId value is "in-progress", return 202
  ACCEPTED with status "scan in progress", and exit.
* If after the above steps, the response body status attribute is unset,

  * Invoke the /scan API of the Scanning Service, with solutionId and revisionId
    set per the request.
  * If the  Scanning Service returns any response other than 202 ACCEPTED,
    forward the response body to the requestor with the same response code, and
    exit.
  * If the  Scanning Service returns a 202 ACCEPTED response, return 202
    ACCEPTED to the requestor, with with status "scan in progress", and exit.

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
  * cms-service API endpoint and credentials
  * optional API endpoint of external scanning service to be integrated

* ports: Acumos platform-internal port used for serving APIs (NOTE: this must
  also be mapped to an externally-accessible port so that the service can
  provide the /scanresult API to external scanning services)

* logs volume: persistent store where the service will save logs. Internal to
  the service, this is mapped to folder /var/acumos/scanning, and will
  contain the distinct log files: application.log, debug.log, and error.log.
  NOTE: logging details here need to be aligned with the common logging design
  based upon log delivery to the ELK component.

The Scanning Service encapsulates a default set of scanning tools and optionally
integrates with an external scanning service. See the "External Scan Result"
description below for details on external scanning service integration.

The Scanning Service will record and use the results of scans in a new artifact
"scanresult.json" that is associated with the scanned solution/revision. This
artifact is central to various design goals of the S-V service, e.g.:

* maintaining an easily exportable record of every type of scan executed on
  every artifact or metadata item related to a solution/revision
* preserving the history of scan results for previous solution revisions, by
  copying the earlier revision scan result upon creation of a new solution
  version, and extending it with scan results on the current revision
* making the history of scan results available to those who obtain the solution
  though sharing, downloading, or federated subscription
* optimizing the overhead for scanning by only scanning previously unscanned
  artifacts/metadata

++++++++++++++
Scan Execution
++++++++++++++

The Verification Service will call the Scan Execution API when a scan has been
requested for a scan-enabled workflow by an Acumos component service. The
Scanning Service will use the following process to determine the API result:

* Retrieve (GET) the set of artifact records from the CDS at
  /solution/{solutionId}/revision/{revisionId}/artifact
* If there is no scanresult.json artifact present,

  * If an earlier revision of the solution is found in the CDS at
    GET /solution/{solutionId}/revision, retrieve the set of artifacts for
    that revision, and

    * If there is a scanresult.json artifact in the list, create a new artifact
      in the nexus-service based upon that scanresult.json
    * Else create a new, default scanresult.json artifact in the nexus-service 
      as shown below

.. code-block:: json

  { "solutionId" : "<solutionId>",
    "revisions" : [ 
      { "revisionId" : "<solutionId>",
        "licenseScan" : "in-progress",
        "vulnerabilityScan" : "in-progress",
        "artifacts" : [
          { "id" : "<artifactId>",
            "version" : "<artifactVersion>",
            "uri" : "<artifactUri>",
            "nexusChecksum" : "<nexusSha1Checksum>",
            "lastScanned" : "null",
            "licenseScan" : "unrequested",
            "vulnerabilityScan" : "unrequested"
          }, ...
        ],
        "metadata" : [
          { "name" : "<metadataName>",
            "version" : "<metadataVersion>",
            "checksum" : "<sha1Checksum>",
            "lastScanned" : "null",
            "licenseScan" : "unrequested",
            "vulnerabilityScan" : "unrequested"
          }, ...
        ]
      }
    ]
  }

..


      * where:

        * <solutionId> is the solutionId from the API request
        * <revisionId> is the revisionId from the API request
        * revisions is an array (initially of length 1) to contain information
          about this and all subsequent revisions of the solutionId
        * artifacts is an array of all artifacts for the solutionId/revisionId
          found in the CDS
        * metadata is an array of CMS-based metadata related to the
          solutionId/revisionId, as found in the CMS under

          * content/documents/acumoscms/solution/solution-description/
          * gallery/acumoscms/solution/
          * assets/solutiondocs/solution/

        * <id> is the ID of each artifact of the solutionId/revisionId
        * <artifactVersion> is the version attribute of the artifact
        * <uri> is the uri attribute of the artifact
        * <nexusSha1Checksum> is the sha1 checksum attribute of the artifact
          in the nexus-service
        * <sha1Checksum> is the computed SHA1 checksum of the metadata item
        * lastScanned is the last time the artifact or metadata item was
          scanned (initially null)
        * <metadataName> is the name of the metadata item
        * <metadataVersion> is the version attribute (if any) of the metadata
          item (NOTE: currently the CMS *DOES NOT* track metadata versions...)

     * using the copied or generated scanresults.json file, create a new
       artifact in the nexus-service and associate it with a new artifact entry
       for the solutionId/revisionId in the CDS via POST to
       /solution/{solutionId}/revision/{revisionId}/artifact/{artifactId}

  * Else (no earlier revision exists), create a new scanresults.json artifact
    and save it in the nexus-service and CDS as above

* Else (scanresult.json file is present)

  * For each artifactId found in the CDS for the solutionId/revisionId, if there
    is no corresponding artifact entry in the scanresult.json file for the
    solutionId/revisionId, add an entry at the start of the artifacts for the
    solutionId/revisionId, with intitial attribute values as described above.
  * For each metadata item found in the CMS for the solutionId/revisionId under
    one of the CMS resource paths listed above (under where: ... * metadata is),
    if there is no corresponding metadata entry for the item "name" in the
    scanresult.json file for the solutionId/revisionId, add an entry at the
    start of the metadata for the solutionId/revisionId, with intitial attribute
    values as described above.

* For each artifact entry in the scanresult.json file (pre-existing or as
  created/updated above) for the solutionId/revisionId, if any one of the
  following are true, initiate a scan for the artifact (see "Scan Process"
  below for details), and set the set the scanresult.json licenseScan and
  vulnerabilityScan attributes to "in-progress":

  * lastScanned = null AND licenseScan = "unrequested"
  * lastScanned = null AND vulnerabilityScan = "unrequested"
  * uri != the current uri attribute of the artifact in the CDS
  * nexusChecksum != the current sha1 checksum attribute of the artifact in the
    nexus-service

* For each metadata item in the scanresult.json file (pre-existing or as
  created/updated above) for the solutionId/revisionId, if any one of the
  following are true, initiate a scan for the metadata item (see "Scan Process"
  below for details), and set the set the scanresult.json licenseScan and
  vulnerabilityScan attributes to "in-progress":

  * lastScanned = null AND licenseScan = "unrequested"
  * lastScanned = null AND vulnerabilityScan = "unrequested"
  * checksum != the current sha1 checksum attribute of the corresponding (by
    name) metadata item in the CMS

* update the scanresult.json artifact in the nexus-service, and update the CDS
  (if required) for the new artifact version via POST to 
  /solution/{solutionId}/revision/{revisionId}/artifact/{artifactId}
* if either of the CDS licenseScan and vulnerabilityScan attributes for the
  solution/revision are set to "unrequested", update the attribute to
  "in-progress"

**ALTERNATIVE IMPLEMENTATION**

Upon a scan request, the Scanning Service:

* creates/updates/saves a scanresult.json artifact as above
* updates the CDS licenseScan and vulnerabilityScan attributes for the
  solution/revision as above
* retrieves all artifacts and metadata for the solution revision, for which
  the scanresult.json licenseScan or vulnerabilityScan attribute is
  "in-progress"
* packages the artifacts and metadata in an archive
* places the archive into a host-shared folder named for the solution/revision

At that point, admins can access the archive for offline scan execution.

++++++++++++
Scan Process
++++++++++++

Details of the scan process for license and vulnerabilities is TBD.

During the scan process, as scanning is completed for each artifact or metadata
item in the scanresult.json file, the Scanning Service:

* updates the corresponding scanresult.json licenseScan or vulnerabilityScan
  attribute per the result of the scan, i.e. as "success" or "failure"
* if the result of the scan is "failure", updates the corresponding CDS
  licenseScan or vulnerabilityScan attribute for the solution/revision to
  "failure"
* updates the scanresult.json artifact in the nexus-service, and update the CDS
  (if required) for the new artifact version via POST to 
  /solution/{solutionId}/revision/{revisionId}/artifact/{artifactId}

When scanning is completed for the last item in the scanresult.json file:

* if the current value of the corresponding CDS attribute for licenseScan
  or vulnerabilityScan is "in-progress", the Scanning Service sets the attribute
  to "success"

**ALTERNATIVE IMPLEMENTATION**

Assuming the alternate (offline) implementation of "Scan Execution" above is
chosen, when the offline scan execution is complete, the admin places a
scanresult.json file in the host-shared solution/revision folder. The Scanning
Service, upon detecting the presence of a new or updated scanresult.json file:

* updates the scanresult.json artifact in the nexus-service and CDS as above
* if any of the licenseScan and vulnerabilityScan attributes for the artifacts
  or metadata items in the scanresult.json file are "failure", updates the
  corresponding CDS licenseScan or vulnerabilityScan attribute for the
  solution/revision to "failure"
* when all licenseScan and vulnerabilityScan attributes for the artifacts
  or metadata items in the scanresult.json file have been updated, if the
  current value of either the licenseScan and vulnerabilityScan CDS attributes
  is "in-progress", the Scanning Service sets the attribute to "success"

++++++++++++++++++++
External Scan Result
++++++++++++++++++++

----------------------------------
Impacts to other Acumos Components
----------------------------------

..................
Portal-Marketplace
..................

Existing calls to the Validation-Security service (deprecated) will be removed
and new calls will be required to the Security-Verification service per the
supported workflow scanning options and workflow verification gates described
in the "Verification Status" and "Scan Invocation" sections. The specific
impacts on the Portal-Marketplace component will be analyzed and described here.

The Portal-Marketplace UI for users and admins will be impacted in various ways.
The impacts will be described here, and are expected to include at a high level:

* removal of existing UI elements related to the Validation-Security component
* UI elements conveying that workflows are blocked due to required/incomplete
  solution verification, e.g. grayed out workflow options with tooltip hints,
  popup dialogs explaining why a workflow can't be completed at this time, or
  additional notification entries.
* admin of the options for S-V service as described under "Current Release
  Features"

**ALTERNATIVE IMPLEMENTATION**

The configuration options are provided to the Verification Service through a
JSON/YAML file that is placed/updated by admins on the host that is running the
Verification Service, in a shared folder. The Verification Service monitors that
folder for updates, and when detecting a new config file, saves the options to
the CDS through the same API that the Portal-BE service would use.
