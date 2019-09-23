/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */
package org.acumos.securityverification.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.stream.Collectors;
import org.acumos.securityverification.domain.SecurityVerificationCdump;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityVerificationJsonParserTest {

  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private SecurityVerificationJsonParser securityVerificationJsonParser;
  private String cdumpInputStreamResult;
  private String siteConfigResult;

  @Before
  public void setUp() throws Exception {
    InputStream cdumpInputStream =
        SecurityVerificationJsonParserTest.class.getResourceAsStream(
            "/ACUMOS-CDUMP-BF0478BC-0D3F-4433-80B6-D7A0F6DB2DF1-1.json");
    cdumpInputStreamResult =
        new BufferedReader(new InputStreamReader(cdumpInputStream))
            .lines()
            .parallel()
            .collect(Collectors.joining("\n"));
    InputStream siteConfigInputStream =
        SecurityVerificationJsonParserTest.class.getResourceAsStream("/siteConfig.json");
    siteConfigResult =
        new BufferedReader(new InputStreamReader(siteConfigInputStream))
            .lines()
            .parallel()
            .collect(Collectors.joining("\n"));
    securityVerificationJsonParser = new SecurityVerificationJsonParser();
  }

  @Test
  public void testParseCdumpJsonFile() throws Exception {
    SecurityVerificationCdump securityVerificationCdump =
        securityVerificationJsonParser.parseCdumpJsonFile(cdumpInputStreamResult);
    assertNotNull(securityVerificationCdump.getCid());
    assertNotNull(securityVerificationCdump.getCname());
    assertNotNull(securityVerificationCdump.getNodes());
    assertNotNull(securityVerificationCdump.getSolutionId());
    assertNotNull(securityVerificationCdump.getVersion());
    assertEquals("bf0478bc-0d3f-4433-80b6-d7a0f6db2df1", securityVerificationCdump.getSolutionId());
    assertEquals("1", securityVerificationCdump.getVersion());
    assertEquals("IngestAdder", securityVerificationCdump.getCname());
  }

  @Test
  public void testExternalScanValue() throws Exception {
    JSONObject siteConfigDataJsonObj = new JSONObject();
    siteConfigDataJsonObj = securityVerificationJsonParser.stringToJsonObject(siteConfigResult);
    JSONObject siteConfigVerificationObject =
        (JSONObject) siteConfigDataJsonObj.get(SVConstants.VERIFICATION);
    String externalScanObject =
        securityVerificationJsonParser.externalScanValue(siteConfigVerificationObject);
    assertNotNull(externalScanObject);
    assertFalse(false);
  }

  @Test
  public void testAllowedLicenseMap() throws Exception {
    JSONObject siteConfigDataJsonObj = new JSONObject();
    siteConfigDataJsonObj = securityVerificationJsonParser.stringToJsonObject(siteConfigResult);
    JSONObject siteConfigVerificationObject =
        (JSONObject) siteConfigDataJsonObj.get(SVConstants.VERIFICATION);
    Map<String, String> allowedLicenseObjectMap =
        securityVerificationJsonParser.allowedLicenseMap(siteConfigVerificationObject);
    assertNotNull(allowedLicenseObjectMap);
  }

  @Test
  public void testSiteConfigMap() throws Exception {
    JSONObject siteConfigDataJsonObj = new JSONObject();
    siteConfigDataJsonObj = securityVerificationJsonParser.stringToJsonObject(siteConfigResult);
    JSONObject siteConfigVerificationObject =
        (JSONObject) siteConfigDataJsonObj.get(SVConstants.VERIFICATION);
    Map<String, String> licenseScan =
        securityVerificationJsonParser.siteConfigMap(
            siteConfigDataJsonObj, SVConstants.LICENSESCAN);
    Map<String, String> securityScan =
        securityVerificationJsonParser.siteConfigMap(
            siteConfigDataJsonObj, SVConstants.SECURITYSCAN);
    Map<String, String> licenseVerify =
        securityVerificationJsonParser.siteConfigMap(
            siteConfigDataJsonObj, SVConstants.LICENSEVERIFY);
    Map<String, String> securityVerify =
        securityVerificationJsonParser.siteConfigMap(
            siteConfigDataJsonObj, SVConstants.SECURITYVERIFY);
    assertNotNull(licenseScan);
    assertNotNull(securityScan);
    assertNotNull(licenseVerify);
    assertNotNull(securityVerify);
  }

  @Test
  public void testStringToJsonObject() throws Exception {
    JSONObject siteConfigDataJsonObj = new JSONObject();
    siteConfigDataJsonObj = securityVerificationJsonParser.stringToJsonObject(siteConfigResult);
    assertNotNull(siteConfigDataJsonObj);
  }

  @Test
  public void testScanResultRootLicenseType() throws Exception {
    String scanResultJson =
        "{\"schema\": \"1.0\",\"verifiedLicense\": \"false\",\"reason\": \"no license.txt document found\",\"solutionId\": \"0357ff93-46f4-45df-a6f7-7f2cf5ea779f\",\"revisionId\": \"0ce6b633-0604-4a59-9ab2-bad78952c54a\",\"scanTime\": \"190416-143640\",\"root_license\": {\"name\": \"\",\"type\": \"SPDX\"}}";
    SecurityVerificationJsonParser securityVerificationJsonParser =
        new SecurityVerificationJsonParser();
    String type = securityVerificationJsonParser.scanResultRootLicenseType(scanResultJson);
    assertNotNull(type);
  }
}
