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

========
Features
========
............................
Previously Released Features
............................

This is the first release of S-V. The "Validation-Security" component originally
released when the Acumos project was launched is being superseded by S-V.

........................
Current Release Features
........................

The features planned for delivery in the current release ("Athena") are:

* scanning for license/vulnerability issues in all models and related artifacts
* a default set of open source license/vulnerability scan tools, which can be
  replaced in a "plug and play" manner with tools as preferred by Acumos
  platform operators
* a default set of success criteria for license/vulnerability scans, which can
  be easily customized by Acumos platform operators
* integration of scanning at various points in Acumos platform workflows
* integration of scan result checking gates at various points in Acumos
  platform workflows
* Acumos platform admin control of the scanning and gate check points

  * option to invoke scanning in workflows

    * upon completion of model onboarding
    * upon completion of metadata creation/update, e.g. documents, test data,
      source code archives
    * upon completion of artifact generation
    * upon request to deploy a model to a private or public cloud
    * upon request to download model artifacts or metadata
    * upon request to share a model with another user
    * upon request to publish a model to a company or public marketplace
    * upon request to subscribe to a model published in a federated platform

  * option to define workflow gates that must be passed, in order to allow the
    workflow to be executed, including

    * enable checking prior to workflows

      * deploy a model to private cloud
      * deploy a model to public cloud
      * download a model
      * share a model
      * publish to company marketplace
      * publish to public marketplace
      * subscribe to a model from a federated platform

    * what must have been checked, and what are the acceptable results

      * license scan successful: yes, no (default)
      * vulnerability scan successful: yes, no (default)

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
