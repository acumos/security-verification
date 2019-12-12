/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
 * ===================================================================================* This Acumos software file is distributed by AT&T and Tech Mahindra
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
package org.acumos.securityverification.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.securityverification.exception.AcumosServiceException;
import org.acumos.securityverification.logging.LogConfig;
import org.acumos.securityverification.service.UploadArtifactSVOutput;
import org.acumos.securityverification.transport.ErrorTransport;
import org.acumos.securityverification.transport.SVResponse;
import org.acumos.securityverification.transport.ScanResult;
import org.acumos.securityverification.transport.SuccessTransport;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@RestController
public class SecurityVerificationServiceController extends AbstractController {

  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired private Environment env;

  public void setEnvironment(Environment env1) {
    env = env1;
  }

  @ApiOperation(value = "Security Verification Service Scan.", response = SuccessTransport.class)
  @RequestMapping(
      value =
          "/scan/"
              + SVServiceConstants.SOLUTIONID
              + "/{solutionId}/"
              + SVServiceConstants.REVISIONID
              + "/{revisionId}/"
              + SVServiceConstants.USERID
              + "/{userId}",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public SVResponse securityVerification(
      @PathVariable("solutionId") String solutionId,
      @PathVariable("revisionId") String revisionId,
      @PathVariable("userId") String userId) {
    try {
      LogConfig.setEnteringMDCs("security-verification-service", "securityVerification");
      logger.info("securityVerification: solutionId {} revisionId {}", solutionId, revisionId);
      ICommonDataServiceRestClient client = getCcdsClient();
      MLPSolutionRevision mlpSolutionRevision = client.getSolutionRevision(solutionId, revisionId);
      mlpSolutionRevision.setVerifiedLicense("IP");
      client.updateSolutionRevision(mlpSolutionRevision);
      startScanJob(solutionId, revisionId, userId);
      LogConfig.clearMDCDetails();
      return new SuccessTransport(HttpServletResponse.SC_OK, null);
    } catch (Exception ex) {
      logger.error("securityVerification failed: {}", ex.toString());
      return new ErrorTransport(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "securityVerification failed", ex);
    }
  }

  private ResponseEntity<SVResponse> startScanJob(
      String solutionId, String revisionId, String userId) {
    logger.debug("startScanJob: ");
    //    HttpHeaders headers =
    //        createAuthHeader(
    //            env.getProperty("jenkins.client.user"),
    //            env.getProperty("jenkins.client.password"));
    RestTemplate restTemplate = new RestTemplate();
    //    HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);
    HttpEntity<String> entity = new HttpEntity<String>("");
    StringBuilder url = new StringBuilder();
    url.append(env.getProperty("jenkins.client.url"));
    url.append("job/");
    url.append(env.getProperty("jenkins.client.scanJob"));
    url.append("/buildWithParameters?");
    url.append("solutionId=");
    url.append(solutionId);
    url.append("&revisionId=");
    url.append(revisionId);
    url.append("&userId=");
    url.append(userId);
    logger.debug("startScanJob: jenkinsJobUrl {}", url.toString());
    ResponseEntity<SVResponse> svResponse =
        restTemplate.exchange(url.toString(), HttpMethod.POST, entity, SVResponse.class);
    logger.debug("startScanJob: API result {}", svResponse.getBody());
    return svResponse;
  }

  private HttpHeaders createAuthHeader(String username, String password) {
    return new HttpHeaders() {
      {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        set("Authorization", authHeader);
      }
    };
  }

  @ApiOperation(
      value = "Security Verification Service ScanResult.",
      response = SuccessTransport.class)
  @RequestMapping(
      value =
          "/scanresult/"
              + SVServiceConstants.SOLUTIONID
              + "/{solutionId}/"
              + SVServiceConstants.REVISIONID
              + "/{revisionId}",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public SVResponse scanResult(
      @PathVariable("solutionId") String solutionId,
      @PathVariable("revisionId") String revisionId,
      @RequestBody String result) {
    try {
      LogConfig.setEnteringMDCs("security-verification-service", "scanResult");
      try {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        ScanResult scanResultObject = objectMapper.readValue(result, ScanResult.class);
        String verifiedLicense = scanResultObject.getVerifiedLicense();
        updateVerifiedLicenseStatus(solutionId, verifiedLicense);
        String reason = scanResultObject.getReason();
        logger.info(
            "scanResult: solutionId {} revisionId {}, verifiedLicense {}, reason {}",
            solutionId,
            revisionId,
            verifiedLicense,
            reason);
        ICommonDataServiceRestClient client = getCcdsClient();
        UUID uidNumber = UUID.randomUUID();
        String uid = uidNumber.toString();
        StringBuilder scanResultJsonFilePath = new StringBuilder();
        scanResultJsonFilePath.append("/maven/scan/");
        scanResultJsonFilePath.append(uid);
        scanResultJsonFilePath.append(SVServiceConstants.FORWARD_SLASH);
        new File(scanResultJsonFilePath.toString()).mkdirs();
        scanResultJsonFilePath.append(SVServiceConstants.SCAN_RESULT_JSON);
        File scanResultJsonFile = new File(scanResultJsonFilePath.toString());
        BufferedWriter writer = new BufferedWriter(new FileWriter(scanResultJsonFile));
        writer.write(result);
        writer.close();
        logger.debug(
            "scanResult: solutionId {} revisionId {}, scanResultJsonFile {}",
            solutionId,
            revisionId,
            scanResultJsonFilePath);
        try {
          uploadToArtifact(solutionId, revisionId, scanResultJsonFile);
          LogConfig.clearMDCDetails();
          return new SuccessTransport(HttpServletResponse.SC_OK, null);
        } catch (Exception ex) {
          logger.error(", failed to upload artifact: {}", ex.toString());
          LogConfig.clearMDCDetails();
          return new ErrorTransport(
              HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "saving scanresult failed, failed to upload artifact",
              ex);
        }
      } catch (Exception ex) {
        logger.error("unable to process request body: {}", ex.toString());
        LogConfig.clearMDCDetails();
        return new ErrorTransport(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "saving scanresult failed, unable to process request body",
            ex);
      }
    } catch (Exception ex) {
      logger.error("saving scanresult failed: {}", ex.toString());
      LogConfig.clearMDCDetails();
      return new ErrorTransport(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "saving scanresult failed", ex);
    }
  }

  private void uploadToArtifact(String solutionId, String revisionId, File file)
      throws AcumosServiceException, FileNotFoundException {
    logger.debug("Inside uploadToArtifact");
    if (file != null) {
      long fileSizeByKB = file.length();
      if (fileSizeByKB > 0) {
        logger.debug("In side if conditoin fileSizeByKB  {}", fileSizeByKB);
        ICommonDataServiceRestClient client =
            new CommonDataServiceRestClientImpl(
                env.getProperty("cdms.client.url"),
                env.getProperty("cdms.client.username"),
                env.getProperty("cdms.client.password"),
                null);
        // client.setRequestId(MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));

        MLPSolution mlpSolution = client.getSolution(solutionId);
        String userId = mlpSolution.getUserId();
        MLPSolutionRevision mlpSolutionRevision =
            client.getSolutionRevision(solutionId, revisionId);
        UploadArtifactSVOutput uploadArtifactSVOutput = new UploadArtifactSVOutput(env);
        uploadArtifactSVOutput.addCreateArtifact(
            solutionId, revisionId, mlpSolutionRevision.getVersion(), userId, file);
      }
    }
  }

  private void updateVerifiedLicenseStatus(String solutionId, String verifiedLicense) {
    logger.debug(
        "Inside updateVerifiedLicenseStatus, solutionId: {} Status: {}",
        solutionId,
        verifiedLicense);
    ICommonDataServiceRestClient client =
        new CommonDataServiceRestClientImpl(
            env.getProperty("cdms.client.url"),
            env.getProperty("cdms.client.username"),
            env.getProperty("cdms.client.password"),
            null);

    if (verifiedLicense.equalsIgnoreCase("true")) {
      verifiedLicense = "SU";
    }
    if (verifiedLicense.equalsIgnoreCase("false")) {
      verifiedLicense = "FA";
    }

    List<MLPSolutionRevision> mlpSolutionRevisions = client.getSolutionRevisions(solutionId);
    for (MLPSolutionRevision mlpSolutionRevision : mlpSolutionRevisions) {
      mlpSolutionRevision.setVerifiedLicense(verifiedLicense);
      client.updateSolutionRevision(mlpSolutionRevision);
    }
  }

  @ApiOperation(value = "Add default SiteConfig Verification.")
  @RequestMapping(
      value = SVServiceConstants.UPDATE_SITE_CONFIG,
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public SVResponse siteConfigVerification() throws Exception {
    logger.debug("Inside siteConfigVerification adding default SiteConfig Verification Json");
    try {
      LogConfig.setEnteringMDCs("security-verification-service", "siteConfigVerification");
      ICommonDataServiceRestClient client = getCcdsClient();
      String siteConfigJson = null;
      if (client != null) siteConfigJson = createSiteConfig(client);
      LogConfig.clearMDCDetails();
      return new SuccessTransport(HttpServletResponse.SC_OK, siteConfigJson);
    } catch (Exception ex) {
      logger.error("createSiteConfig failed: {}", ex.toString());
      return new ErrorTransport(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "createSiteConfig failed", ex);
    }
  }

  private String createSiteConfig(ICommonDataServiceRestClient client) throws Exception {
    logger.debug("Inside createSiteConfig");
    MLPSiteConfig mlpSiteConfig = null;
    try {
      mlpSiteConfig = client.getSiteConfig(SVServiceConstants.SITE_VERIFICATION_KEY);
    } catch (RestClientResponseException ex) {
      logger.error("getSiteConfig failed, server reports: {}", ex.getResponseBodyAsString());
      throw ex;
    }
    if (StringUtils.isEmpty(mlpSiteConfig)) {
      logger.debug("createSiteConfig: no siteConfig verification key in database");
      String siteConfigJsonFromConfiguration = env.getProperty("siteConfig");
      logger.debug("siteConfig.verification env: {} ", siteConfigJsonFromConfiguration);
      MLPSiteConfig config = new MLPSiteConfig();
      config.setConfigKey(SVServiceConstants.SITE_VERIFICATION_KEY);
      config.setConfigValue(siteConfigJsonFromConfiguration);
      try {
        logger.info("createSiteConfig: setting value {}", config.getConfigValue());
        mlpSiteConfig = client.createSiteConfig(config);
        logger.info("createSiteConfig: result {}", mlpSiteConfig.getConfigValue());
        return mlpSiteConfig.getConfigValue();
      } catch (RestClientResponseException ex) {
        logger.error("createSiteConfig failed, server reports: {}", ex.getResponseBodyAsString());
        throw ex;
      }
    } else {
      return mlpSiteConfig.getConfigValue();
    }
  }

  private ICommonDataServiceRestClient getCcdsClient() {
    logger.debug("Inside getCcdsClient");
    ICommonDataServiceRestClient client =
        new CommonDataServiceRestClientImpl(
            env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
            env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
            env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD),
            null);
    return client;
  }
}
