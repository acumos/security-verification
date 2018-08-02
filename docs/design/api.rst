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

.. _api:

====
APIs
====

.. _api-scan-invocation:

Scan Invocation
===============

This API enables Acumos components to invoke scanning as needed based upon
site-config settings that enable scan invocation points in workflows.

The base URL for this API is: http://<verification-service-host>:<port>, where
'verification-service-host' is the routable address of the verification service
in the Acumos platform deployment, and port is the assigned port where the
servce is listening for API requests.

* URL resource: /scan/{solutionId}/{revisionId}/{workflowId}

  * {solutionId}: ID of a solution present in the CDS
  * {revisionId}: ID of a version for a solution present in the CDS
  * {workflowId}: one of

    * created: model has been onboarded
    * updated: model artifacts/metadata have been updated
    * deploy-private: request to deploy to private cloud received
    * deploy-public: request to deploy to public cloud received
    * download: request to download recieved
    * share: request to share received
    * publish-company: request to publish to company marketplace received
    * publish-public: request to publish to public marketplace received
    * subscribe: request to subscribe received

* Supported HTTP operations

  * GET

    * Response

      * 202 ACCEPTED

        * meaning: request accepted, detailed status in JSON body
        * body: JSON object as below

          * status: "scan in progress"|"scan not required"

      * 404 NOT FOUND

        * meaning: solution/revision not found, details in JSON body. NOTE: this
          response is only expected in race conditions, e.g. in which a scan
          request was initiated when at the same time, the solution was deleted
          by another user
        * body: JSON object as below

          * status: "invalid solutionId"|"invalid revisionId"

      * 400 BAD REQUEST

        * meaning: request was malformed, details in JSON body
        * body: JSON object as below

          * status: "invalid workflowId"


.. _scan-result:

Process External Scan Result
============================

The service exposes the following API to allow optional external scan
functions/processes to report back on the status of scans. Tools that have Web Hooks configured can call this API with results from a scan.

The base URL for this API is:
http://<service-host>:<port>, where 'service-host' is the
externally routable address of the verification service in the Acumos platform
deployment, and port is the assigned externally accessible port where the
service is listening for API requests.

* URL resource: /result

  * if possible, these should be present in the message body:

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

.. _api-verification-status:

Verification Status
===================

** this isn't needed if status is determined when processing scan result and then stored in the database **

This API enables Acumos components to check if scan requirements of a workflow
have been met, based upon site-config settings that require specific
verification criteria, as well as the actual record of scanning as recorded in a
scan-results solution artifact.

The base URL for this API is: http://<verification-service-host>:<port>, where
'verification-service-host' is the routable address of the verification service
in the Acumos platform deployment, and port is the assigned port where the
servce is listening for API requests.

* URL resource: /verify/{solutionId}/{revisionId}/{workflowId}

  * {solutionId}: ID of a solution present in the CDS
  * {revisionId}: ID of a version for a solution present in the CDS
  * {workflowId}: one of

    * deploy-private: request to deploy to private cloud received
    * deploy-public: request to deploy to public cloud received
    * download: request to download received
    * share: request to share received
    * publish-company: request to publish to company marketplace received
    * publish-public: request to publish to public marketplace received
    * subscribe: request to subscribe received
    * **why are created and updated workflows omitted here?**


* Supported HTTP operations

  * GET

    * Response

      * 200 OK

        * meaning: request completed, detailed status in JSON body
        * body: JSON object as below

          * status: "workflow permitted"|"workflow not permitted"
          * messages: array containing one or more strings

            * for status "workflow permitted"

              * "workflow not gated"
              * "all workflow gates cleared"

            * for status "workflow not permitted"

              * "license scan unrequested"
              * "security scan unrequested"
              * "license scan in progress"
              * "security scan in progress"
              * "license scan failure"
              * "security scan failure"

      * 404 NOT FOUND

        * meaning: solution/revision not found, details in JSON body. NOTE: this
          response is only expected in race conditions, e.g. in which a scan
          request was initiated when at the same time, the solution was deleted
          by another user
        * body: JSON object as below

          * status: "invalid solutionId"|"invalid revisionId"

      * 400 BAD REQUEST

        * meaning: request was malformed, details in JSON body
        * body: JSON object as below

          * status: "invalid workflowId"