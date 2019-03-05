package org.acumos.licensemanager.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("licenseclient")
public class LicenseClientProperties {

  /**
   * A message for the service.
   */
  private boolean useCDS;
  private boolean defaultAllowed;
  private boolean downloadAllowed;
  private boolean deployAllowed;

  public boolean getCDS() {
    return useCDS;
  }

  /**
   * @return the deployAllowed
   */
  public boolean isDeployAllowed() {
    return deployAllowed;
  }

  /**
   * @param deployAllowed the deployAllowed to set
   */
  public void setDeployAllowed(boolean deployAllowed) {
    this.deployAllowed = deployAllowed;
  }

  /**
   * @return the downloadAllowed
   */
  public boolean isDownloadAllowed() {
    return downloadAllowed;
  }

  /**
   * @param downloadAllowed the downloadAllowed to set
   */
  public void setDownloadAllowed(boolean downloadAllowed) {
    this.downloadAllowed = downloadAllowed;
  }

  /**
   * @return the defaultAllowed
   */
  public boolean isDefaultAllowed() {
    return defaultAllowed;
  }

  /**
   * @param defaultAllowed the defaultAllowed to set
   */
  public void setDefaultAllowed(boolean defaultAllowed) {
    this.defaultAllowed = defaultAllowed;
  }

  public void setUseCDS(boolean useCDS) {
    this.useCDS = useCDS;
  }

}