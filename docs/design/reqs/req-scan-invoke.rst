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

.. _req-scan-invoke:

===============
Scan Invocation
===============

Acumos components will call the :ref:`api-scan-invocation` at the supported workflow
points to invoke a scan based upon the admin requirements for scanning
related to that workflow. The admin requirements are stored in security-verification site config defined in :ref:`req-sv-service-starts`


* /scan/{solutionId}/{revisionId}/{workflowId}

* request parameters are required and must be valid

    * The workflowId must be valid workflowId type - validate against CDS data

        * If invalid workflowId, return 400 BAD REQUEST with status "invalid workflowId"
        * If the workflowId is valid:

            * If the CDS config license-scan attribute array member for the request workflowId value is "false" and the CDS config vulnerability-scan attribute array member for the request workflowId value is "false", return 200 OK with status "scan not required"
            * If the CDS config license-scan attribute array member for the request workflowId value is "false" and the CDS config vulnerability-scan attribute array member for the request workflowId value is "false", return 200 OK with status "scan not required"
            * If the CDS config license-scan attribute array member for the request workflowId value is "true" and the CDS config vulnerability-scan attribute array member for the request workflowId value is "in-progress", return 202 ACCEPTED with status "scan in progress"

    * The solutionId/revisionId must be valid (fetch MLPSolutionRevision)

        * if not found, return 404 NOT FOUND with status "invalid solutionId/revisionId" with the values


* If all the parameters are valid, perform scan



Perform Scan
============


** CONCERN: THIS APPROACH IS PROBLEMATIC AND MOST LIKELY NOT POSSIBLE GIVEN THE THIRD PARTY TOOLS AVAILABLE. ADDITIONALLY, THIRD PARTY SCAN TOOLS WOULD HAVE TO HAVE WEB HOOKS FUNCTIONALITY, OR THE S-V SERVICE WOULD HAVE TO POLL FOR A SCANRESULTS FILE. EACH TOOL WILL HAVE RESULTS IN A DIFFERENT FORMAT, SO THE TOOL WRAPPERS WOULD HAVE TO PARSE THE SCAN DATA **

* artifacts for a solution/revision in nexus as well as artifacts stored in the content management system shall be scanned

* artifact types are defined in the CDS `docs <https://docs.acumos.org/en/latest/submodules/common-dataservice/docs/requirements.html#artifact-type>`_


* scanresults.json is stored in nexus under solution/revision
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
      { "revisionId" : "<revisionId>",
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

** QUESTION: CAN NEW ARTIFACTS BE ADDED TO AN EXISTING REVISION?** YES; 


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
          in the nexus-service ** does this exist and if so, how is it retrieved? There is no CDS artifact type for this **
        * <sha1Checksum> is the computed SHA1 checksum of the metadata item
        * lastScanned is the last time the artifact or metadata item was
          scanned (initially null)
        * <metadataName> is the name of the metadata item
        * <metadataVersion> is the version attribute (if any) of the metadata
          item (NOTE: currently the CMS *DOES NOT* track metadata versions...)

     * using the copied or generated scanresults.json file, create a new
       artifact in the nexus-service and associate it with a new artifact entry
       for the solutionId/revisionId

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
    start of the metadata for the solutionId/revisionId, with initial attribute
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
  following are true, initiate a scan for the metadata item, and set the set the scanresult.json licenseScan and
  vulnerabilityScan attributes to "in-progress": ** why are all these attributes being updated in a flat file instead of the database? **

  * lastScanned = null AND licenseScan = "unrequested"
  * lastScanned = null AND vulnerabilityScan = "unrequested"
  * checksum != the current sha1 checksum attribute of the corresponding (by
    name) metadata item in the CMS

* update the scanresult.json artifact in the nexus-service, and update the CDS
  (if required) for the new artifact version
* if either of the CDS licenseScan and vulnerabilityScan attributes for the
  solution/revision are set to "unrequested", update the attribute to
  "in-progress"



**ALTERNATIVE IMPLEMENTATION**


* creates/updates/saves a scanresult.json artifact as above ** again, why is this not all in the database **
* updates the CDS licenseScan and vulnerabilityScan attributes for the
  solution/revision as above ** this should update the MLPValidationStatus object **
* retrieves all artifacts and metadata for the solution revision, for which
  the scanresult.json licenseScan or vulnerabilityScan attribute is
  "in-progress"
* packages the artifacts and metadata in an archive
* places the archive into a host-shared folder named for the solution/revision

At that point, admins can access the archive for offline scan execution.
** NOTE: there appears to be no support for this approach - 'TOO EXPENSIVE' TO MANUALLY SELECT MODEL FOR SCANNING, USE EXTERNAL TOOL TO SCAN, VERIFY RESULTS, APPROVE, FILL OUT JSON FILE AND WAIT FOR S-V TOOL TO PICK UP CHANGES **


