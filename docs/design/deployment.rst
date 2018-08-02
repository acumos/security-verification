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

=============================================
Security-Verification Microservice Deployment
=============================================


The Security-Verification Service will be deployed as an always-running platform
service under docker or kubernetes. The base URL for this API is: http://<verification-service-host>:<port>, where 'verification-service-host' is the routable address of the verification service in the Acumos platform deployment, and port is the assigned port where the servce is listening for API requests. It has the following dependencies, which must be specified in the service template used to create the service:

* environment

  * docker-service API endpoint and credentials (** WHY??**)
  * cms-service API endpoint and credentials (** this is to retrieve related docs stored in CMS instead of nexus**)
  * optional API endpoint of external scanning service to be integrated

* ports: Acumos platform-internal port used (NOTE: this must
  also be mapped to an externally-accessible port so that the service can
  provide the /scanresult API to external scanning services)(note: only NexusIQ server is able to post scan results and only if the web hook has been configured in NexusIQ).

* logs volume: persistent store where the service will save logs. Internal to
  the service, this is mapped to folder /var/acumos/verification, and will
  contain the distinct log files: application.log, debug.log, and error.log.
  NOTE: logging details here need to be aligned with the common logging design
  based upon log delivery to the ELK component.


