package org.acumos.licensemanager.service;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class LicenseResponse {

  private Map<String, Boolean> allowedToUse;

  public LicenseResponse() {
    if (allowedToUse == null) {
      allowedToUse = new HashMap<String, Boolean>();
    }

  }

  public void addWorkflow(String workflow, boolean allowed) {
    allowedToUse.put(workflow, allowed);
  }

  /**
   * @return the allowedToUse
   */
  public Map<String, Boolean> getAllowedToUse() {
    return ImmutableMap.copyOf(allowedToUse);
  }

}