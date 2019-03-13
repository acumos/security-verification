package org.acumos.securityverification.test;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.securityverification.utils.Configurations;
//import org.acumos.portal.be.util.SanitizeUtils;
import org.acumos.securityverification.utils.SVConstants;
import org.acumos.securityverification.utils.SecurityVerificationJsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CdsConnectionTest {
	
	/*
Sample request:
{
  "configKey": "site_config",
  "configValue": "{\"test1\":\"test2\"}",  
  "userId": "5d27806e-bcec-49cc-aa51-632f17cb31f8"
}
{
  "configKey": "site_config",
  "configValue":"{\"verification\":{\"externalScan\":\"false\",\"allowedLicense\":[{\"type\":\"SPDX\",\"value\":\"Apache-2.0\"},{\"type\":\"SPDX\",\"value\":\"CC-BY-4.0\"},{\"type\":\"SPDX\",\"value\":\"BSD-3-Clause\"},{\"type\":\"VendorA\",\"value\":\"VendorA-OSS\"},{\"type\":\"CompanyB\",\"value\":\"CompanyB-Proprietary\"}],\"licenseScan\":{\"created\":\"true\",\"updated\":\"true\",\"deploy\":\"false\",\"download\":\"false\",\"share\":\"false\",\"publishCompany\":\"false\",\"publishPublic\":\"false\"},\"securityScan\":{\"created\":\"true\",\"updated\":\"true\",\"deploy\":\"false\",\"download\":\"false\",\"share\":\"false\",\"publishCompany\":\"false\",\"publishPublic\":\"false\"},\"licenseVerify\":{\"deploy\":\"true\",\"download\":\"false\",\"share\":\"false\",\"publishCompany\":\"true\",\"publishPublic\":\"true\"},\"securityVerify\":{\"deploy\":\"true\",\"download\":\"false\",\"share\":\"false\",\"publishCompany\":\"true\",\"publishPublic\":\"true\"}}}",
  "userId":"5d27806e-bcec-49cc-aa51-632f17cb31f8"
}
	 * */

	public static void main(String[] args) {
		
		// TODO: Need to configure docker-compose  
		String datasource = "http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8001/ccds";//8000 with 1.18.4
		String userName = "ccds_client";
		String dataPd = "ccds_client";
		
		System.out.println("siteConfig.verification ok fromyaml:--  "+Configurations.getConfig("siteConfig.verification"));
	
		SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
		JSONObject siteConfigDataJsonObj = new JSONObject();
		String strTemp1 = Configurations.getConfig("siteConfig.verification").toString().replaceAll("\\\\\"", "\"");
		String strTemp2 = strTemp1.substring(1,strTemp1.length()-1);
		siteConfigDataJsonObj = parseJSON.stringToJsonObject(strTemp2);
		System.out.println("siteConfigDataJsonObj"+siteConfigDataJsonObj);
		 
		//CDS call
	/*	CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, dataPd,null);
		
		System.out.println("CDS CONNECTED..11");
	    String configKey = "site_config";
				if(client != null) {
					System.out.println("CDS CONNECTED..22");
					MLPSiteConfig mlpSiteConfig = client.getSiteConfig(configKey);
//					System.out.println(String.valueOf(mlpSiteConfig.getConfigKey()));
					System.out.println("CDS CONNECTED..33");
					if(mlpSiteConfig!=null) {
						System.out.println(String.valueOf(mlpSiteConfig.getConfigKey()));
						System.out.println("DB  -->"+mlpSiteConfig.getConfigValue());
						
						SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
						JSONParser parser = new JSONParser();
							try {
								String configValue = "{\"verification\":{\"externalScan\":\"false\",\"allowedLicense\":[{\"type\":\"SPDX\",\"value\":\"Apache-2.0\"},{\"type\":\"SPDX\",\"value\":\"CC-BY-4.0\"},{\"type\":\"SPDX\",\"value\":\"BSD-3-Clause\"},{\"type\":\"VendorA\",\"value\":\"VendorA-OSS\"},{\"type\":\"CompanyB\",\"value\":\"CompanyB-Proprietary\"}],\"licenseScan\":{\"created\":\"true\",\"updated\":\"true\",\"deploy\":\"false\",\"download\":\"false\",\"share\":\"false\",\"publishCompany\":\"false\",\"publishPublic\":\"false\"},\"securityScan\":{\"created\":\"true\",\"updated\":\"true\",\"deploy\":\"false\",\"download\":\"false\",\"share\":\"false\",\"publishCompany\":\"false\",\"publishPublic\":\"false\"},\"licenseVerify\":{\"deploy\":\"true\",\"download\":\"false\",\"share\":\"false\",\"publishCompany\":\"true\",\"publishPublic\":\"true\"},\"securityVerify\":{\"deploy\":\"true\",\"download\":\"false\",\"share\":\"false\",\"publishCompany\":\"true\",\"publishPublic\":\"true\"}}}";
								System.out.println("1111111111----");
								System.out.println("str1:--->"+configValue);
								String str1 = mlpSiteConfig.getConfigValue().toString().replaceAll("\\\\\"", "\"");
								System.out.println("str1:--->"+str1);
								String ss = str1.substring(1,str1.length()-1);
								System.out.println("str3:--->"+ss);								
								
								JSONObject siteConfigJsonObject = (JSONObject) parser.parse(ss);
								parseJSON.parseSiteConfigJson(siteConfigJsonObject);
								
								String licenseScan = parseJSON.parseSiteConfigJson(siteConfigJsonObject,SVConstants.VERIFICATION, SVConstants.LICENSESCAN);
								
								
							} catch (ParseException e) {
								e.printStackTrace();
							}
						
					} else {
						
						System.out.println("No site config infomation found. Default site_config is picked from configuration file.");
						MLPSiteConfig config = new MLPSiteConfig();
						config.setConfigKey(configKey);
						String configValue = "\"{\\\"verification\\\":{\\\"externalScan\\\":\\\"false\\\",\\\"allowedLicense\\\":[{\\\"type\\\":\\\"SPDX\\\",\\\"value\\\":\\\"Apache-2.0\\\"},{\\\"type\\\":\\\"SPDX\\\",\\\"value\\\":\\\"CC-BY-4.0\\\"},{\\\"type\\\":\\\"SPDX\\\",\\\"value\\\":\\\"BSD-3-Clause\\\"},{\\\"type\\\":\\\"VendorA\\\",\\\"value\\\":\\\"VendorA-OSS\\\"},{\\\"type\\\":\\\"CompanyB\\\",\\\"value\\\":\\\"CompanyB-Proprietary\\\"}],\\\"licenseScan\\\":{\\\"created\\\":\\\"true\\\",\\\"updated\\\":\\\"true\\\",\\\"deploy\\\":\\\"false\\\",\\\"download\\\":\\\"false\\\",\\\"share\\\":\\\"false\\\",\\\"publishCompany\\\":\\\"false\\\",\\\"publishPublic\\\":\\\"false\\\"},\\\"securityScan\\\":{\\\"created\\\":\\\"true\\\",\\\"updated\\\":\\\"true\\\",\\\"deploy\\\":\\\"false\\\",\\\"download\\\":\\\"false\\\",\\\"share\\\":\\\"false\\\",\\\"publishCompany\\\":\\\"false\\\",\\\"publishPublic\\\":\\\"false\\\"},\\\"licenseVerify\\\":{\\\"deploy\\\":\\\"true\\\",\\\"download\\\":\\\"false\\\",\\\"share\\\":\\\"false\\\",\\\"publishCompany\\\":\\\"true\\\",\\\"publishPublic\\\":\\\"true\\\"},\\\"securityVerify\\\":{\\\"deploy\\\":\\\"true\\\",\\\"download\\\":\\\"false\\\",\\\"share\\\":\\\"false\\\",\\\"publishCompany\\\":\\\"true\\\",\\\"publishPublic\\\":\\\"true\\\"}}}\"";
						System.out.println("configValue::...>"+configValue);
						config.setConfigValue(Configurations.getConfig("siteConfig.verification"));
						System.out.println("Configurations.getConfig(\"siteConfig.verification\")::  "+Configurations.getConfig("siteConfig.verification"));
						config.setUserId("26fcd4bf-8819-41c1-b46c-87ec2f7a39f8");
						System.out.println("CDS CONNECTED..44");
						Object obj = client.createSiteConfig(config);
						System.out.println("CDS CONNECTED..55 :: "+obj);
					
					}
				
				}*/
		
		
		}

}
