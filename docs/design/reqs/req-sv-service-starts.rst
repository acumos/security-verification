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

.. _req-sv-service-starts:

=========================================
Security-Verification Service Initializes
=========================================

Upon startup, the S-V service shall create and store an `MLPSiteConfig <https://javadocs.acumos.org/org.acumos.common-dataservice/master/org/acumos/cds/domain/MLPSiteConfig.html>`_ object if not already present in the CDS. This site config element shall define all the options for the configuration of the S-V service. It shall be used by the Portal-FE service in presenting options for admin users and updated by the Portal-BE service based upon any changes to the options by an admin.


MLPSiteConfig.configKey: security-verification

MLPSiteConfig.configValue: JSON structure stored as a String

Workflow Types: See :ref:`cds-workflow-type` for explanation of the workflow names.

* security-verification:

    * license-scan: license scanning requirements for workflows.  Each workflow is associated with a boolean value, which if "true" indicates
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

** QUESTION: WHAT IS THE DIFFERENCE BETWEEN 'license-scan' and 'license-verify' and BETWEEN vulnerablity-scan and vulnerability-verify ???  **

security-scan: kick off a scan
security-verify: check the status to determine whether to continue the workflow

license-can: kick off the scan
license-verify: check the status to determine whether to continue the workflow


Design questions:


* how to update the s-v site config if needed with new values? the portal updates the values. how to incorporate parameter that tells S-V to update site config already stored in CDS?








