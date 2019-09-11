package org.acumos.securityverification.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.acumos.securityverification.utils.SecurityVerificationServiceUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SecurityVerificationScan.class, SecurityVerificationServiceUtils.class })
public class SecurityVerificationScanTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Mock
	private Environment env;

	@Mock
	private RestTemplate restTemplate = new RestTemplate();

	@InjectMocks
	private SecurityVerificationScan securityVerificationScan;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_URL)).thenReturn("http://cds.com:8001/ccds");
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_USER)).thenReturn("MockedSpringEnvString");
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD)).thenReturn("MockedSpringEnvString");
	}

	@Test
	public void testRun() throws Exception {
		String solutionId = "bf0478bc-0d3f-4433-80b6-d7a0f6db2df2";
		String revisionId = "ba742c3b-039c-4a8e-b0fc-1aee7d5a9d67";
		try {
			ICommonDataServiceRestClient client = mock(CommonDataServiceRestClientImpl.class);
			PowerMockito.whenNew(CommonDataServiceRestClientImpl.class)
					.withArguments(env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
							env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
							env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD), null)
					.thenReturn((CommonDataServiceRestClientImpl) client);
			ResponseEntity responseEntity = mock(ResponseEntity.class);
			when(restTemplate.getForEntity(Mockito.anyString(), any(Class.class))).thenReturn(responseEntity);
			securityVerificationScan = new SecurityVerificationScan(solutionId, revisionId, env, client);
			List<MLPSolutionRevision> mlpSolutionRevisions = new ArrayList<>();
			MLPSolutionRevision mlpSolutionRevision = new MLPSolutionRevision();
			mlpSolutionRevision.setVerifiedLicense("IP");
			mlpSolutionRevisions.add(mlpSolutionRevision);
			when(client.getSolutionRevisions(solutionId)).thenReturn(mlpSolutionRevisions);

			byte[] result = null;
			PowerMockito.mockStatic(SecurityVerificationServiceUtils.class);
			PowerMockito.when(SecurityVerificationServiceUtils.executeScript(SVServiceConstants.SCRIPTFILE_DUMP_MODEL,
					solutionId, revisionId, "test", env)).thenReturn(result);    
		      
			String userId = "ba742c3b-039c-4a8e-b0fc-1aee7d5a9d64";
			String name="scanresult.json";
			String scanResultJson = "{\"schema\": \"1.0\",\"verifiedLicense\": \"false\",\"reason\": \"no license.txt document found\",\"solutionId\": \"0357ff93-46f4-45df-a6f7-7f2cf5ea779f\",\"revisionId\": \"0ce6b633-0604-4a59-9ab2-bad78952c54a\",\"scanTime\": \"190416-143640\",\"root_license\": {\"name\": \"\",\"type\": \"SPDX\"}}";
			
			File file = File.createTempFile("scanresult", ".json");
			FileWriter writer = new FileWriter(file);
			writer.write(scanResultJson);
			writer.close();

			String catalogId = "Mi";
			UploadArtifactSVOutput uploadArtifactSVOutput = mock(UploadArtifactSVOutput.class);
			doNothing().when(uploadArtifactSVOutput).addCreateArtifact(solutionId, revisionId,catalogId, userId, file);
			
			String folder = "fb8f31cc-69d0-46b1-8b68-593a3b55a595";
			String scanResultJsonFilePath = scanOutJsonLocation(folder, SVServiceConstants.SCAN_RESULT_JSON);
			when(SecurityVerificationServiceUtils.readScanOutput(scanResultJsonFilePath)).thenReturn(file);  
			
			securityVerificationScan.run();

		} catch (Exception ex) {
			logger.info("Client failed as expected: {}", ex.toString());
			assertThat(ex.toString(), containsString("The system cannot find the path specified"));
		}

	}

	private String scanOutJsonLocation(String folder,String jsonFlieName) {
		StringBuilder scanJsonOutFliePath = new StringBuilder();
		scanJsonOutFliePath.append(SVServiceConstants.SECURITY_SCAN);
		scanJsonOutFliePath.append(SVServiceConstants.FORWARD_SLASH);
		scanJsonOutFliePath.append(folder);
		scanJsonOutFliePath.append(jsonFlieName);
		return scanJsonOutFliePath.toString();
	}
}
