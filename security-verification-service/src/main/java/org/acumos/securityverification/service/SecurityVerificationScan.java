package org.acumos.securityverification.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.securityverification.exception.AcumosServiceException;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.acumos.securityverification.utils.SecurityVerificationServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class SecurityVerificationScan implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	String solutionId;
	String revisionId;
	Environment env;

	SecurityVerificationScan(String solutionId, String revisionId, Environment env1) {
		this.solutionId = solutionId;
		this.revisionId = revisionId;
		this.env = env1;
	}

	@Override
	public void run() {
		logger.debug("SecurityVerification thread is created");
		UUID uidNumber = UUID.randomUUID();
		String folder = uidNumber.toString();
		try {
			updateVerifiedLicenseStatus(solutionId, "in-progress");
			SecurityVerificationServiceUtils.executeScript(SVServiceConstants.SCRIPTFILE_DUMP_MODEL, solutionId,
					revisionId, folder, env);

			SecurityVerificationServiceUtils.executeScript(SVServiceConstants.SCRIPTFILE_LICENSE_SCAN, solutionId,
					revisionId, folder, env);
			// Upload scanresult.json
			File scanResultJsonFile = SecurityVerificationServiceUtils.readScanOutput(SVServiceConstants.SECURITY_SCAN
					+ SVServiceConstants.FORWARD_SLASH + folder + SVServiceConstants.SCAN_RESULT_JSON);
			logger.debug("scanResultJsonFile: {}", scanResultJsonFile);
			uploadToArtifact(solutionId, revisionId, scanResultJsonFile);
			logger.debug("ScanResult Json uploadToArtifact successfully");
			// Upload scancode.json
			File scanCodeJsonFile = SecurityVerificationServiceUtils
					.readScanOutput(SVServiceConstants.SECURITY_SCAN + SVServiceConstants.FORWARD_SLASH + folder
							+ SVServiceConstants.SCAN_CODE_JSON);
			logger.debug("scanCodeJsonFile: {}", scanCodeJsonFile);
			uploadToArtifact(solutionId, revisionId, scanCodeJsonFile);

			// TODO  VerifiedLicense successful/failed logic is TBD.

		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}

	}

	private void uploadToArtifact(String solutionId, String revisionId, File file)
			throws AcumosServiceException, FileNotFoundException {
		long fileSizeByKB = file.length();
		if (fileSizeByKB > 0) {
			logger.debug("in side if conditoin fileSizeByKB  {}", fileSizeByKB);
			ICommonDataServiceRestClient client = getCcdsClient();
			MLPSolution mlpSolution = client.getSolution(solutionId);
			String userId = mlpSolution.getUserId();
			UploadArtifactSVOutput uploadArtifactSVOutput = new UploadArtifactSVOutput();
			MLPDocument document = uploadArtifactSVOutput.addRevisionDocument(solutionId, revisionId,
					AccessTypeCode.PR.toString(), userId, file);
		}
	}

	private void updateVerifiedLicenseStatus(String solutionId, String verifiedLicense) {

		ICommonDataServiceRestClient client = getCcdsClient();
		List<MLPSolutionRevision> mlpSolutionRevisions = client.getSolutionRevisions(solutionId);
		for (MLPSolutionRevision mlpSolutionRevision : mlpSolutionRevisions) {
			mlpSolutionRevision.setVerifiedLicense(verifiedLicense);
			client.updateSolutionRevision(mlpSolutionRevision);
		}

	}

	private ICommonDataServiceRestClient getCcdsClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(
				env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD), null);
		return client;
	}

	private NexusArtifactClient getNexusClient() {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(env.getProperty(SVServiceConstants.NEXUS_CLIENT_URL));
		repositoryLocation.setUsername(env.getProperty(SVServiceConstants.NEXUS_CLIENT_USER));
		repositoryLocation.setPassword(env.getProperty(SVServiceConstants.NEXUS_CLIENT_PWD));
		NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
		return artifactClient;
	}

}
