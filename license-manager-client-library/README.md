
<!---
.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2019 Nordix Foundation.
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
-->
License Manager Client Library

Uses of this library:
- This library is for use by security verification service to verify that users have the rights to use a solution for commercial models.
- This library is for use by portal back end to create new rights to use a solution for a user or for entire site.

Developer notes:

1. To build run 
```
mvn clean install
```
This will require settings setup for nexus for acumos.

2. To run license check and update headers

see https://www.mojohaus.org/license-maven-plugin/

Quick goals reference:
```
mvn license:check-file-header
```
To update license headers:

```
mvn license:update-file-header
```

3. To check java docs are working

```
mvn javadoc:javadoc
```

You can view javadocs in the path provided in console

Fixing java docs 

```
mvn javadoc:fix -DfixTags="version,param,return,throws,link" -DdefaultVersion="0.0.3" 
```

4. Unit test coverage should be above 40%

Check html page under here
```
license-manager-client-library/target/site/jacoco/org.acumos.licensemanager.client.model/index.source.html
```

5. Check style 

```
mvn checkstyle:check
```
