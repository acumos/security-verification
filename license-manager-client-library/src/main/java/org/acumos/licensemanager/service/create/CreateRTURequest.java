package org.acumos.licensemanager.service.create;

import java.util.Random;
import java.util.UUID;

public class CreateRTURequest {
  final String solutionId;
  final String userId;
  private String[] rtuRefs;
  private boolean siteWide = false;
  private Long rtuId;

  public CreateRTURequest(String solId, String uId) {
    solutionId = solId;
    userId = uId;

  }

  public String getSolutionId() {
    return solutionId;
  }

  public String getUserId() {
    return userId;
  }

  public String[] getRTURefs() {
    if (rtuRefs != null) {
      return rtuRefs;
    }
    // default to UUID generated id
    return new String[]{UUID.randomUUID().toString()};
  }

  public Long getRTUId() {
    if(rtuId == null){
      return new Random().nextLong();
    }
    return rtuId;
  }

  public boolean isSiteWide() {
    return siteWide;
  }

}