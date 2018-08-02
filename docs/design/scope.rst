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


=====
Scope
=====

The Security-Verification component (referred to here as S-V) addresses the
following goals of the Acumos project, as outlined by the
`Acumos TSC Security Committee <https://wiki.acumos.org/display/SEC>`_:

* models and related metadata that are contributed to an Acumos platform and
  distributed through a federated ecosystem of Acumos platforms must be
  verified to the extent possible, as

  * contributed under clear and compatible open source license(s)
  * free from security vulnerabilities

This in turn is based upon the `bylaws of the Acumos project <https://www.acumos.org/wp-content/uploads/sites/61/2018/03/charter_acumos_mar2018.pdf>`_ which include the following responsibilities as
described in section 2.i.vii:

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
  malicous risks, when embedded in the solutions and metadata):

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
      * network behavior outside expection norms (e.g. for models, network
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
