package org.acumos.licensemanager.service;

public class LicenseRequest {

  private String[] workflow; // we will need to convert this to rtuID --mapping rtuid to workflow id?
  private String solutionId;
  private String userId;

  public LicenseRequest(String workflow, String solutionId, String userId) {
    this.workflow = new String[1];
    this.workflow[0] = workflow;
    this.solutionId = solutionId;
    this.userId = userId;
  }

  public LicenseRequest(String[] workflow, String solutionId, String userId) {
    this.workflow = workflow;
    this.solutionId = solutionId;
    this.userId = userId;
  }

  /**
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @return the solutionId
   */
  public String getSolutionId() {
    return solutionId;
  }


  /**
   * @return the workflow
   */
  public String[] getWorkflow() {
    return workflow;
  }


}