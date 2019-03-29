License Manager Client Library

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
mvn javadoc:fix -DdefaultAuthor="est.tech" -DdefaultVersion="0.0.2" 
```

4. Unit test coverage should be above 40%

Check html page under here
```
license-manager-client-library/target/site/jacoco/org.acumos.licensemanager.client.model/index.source.html
```