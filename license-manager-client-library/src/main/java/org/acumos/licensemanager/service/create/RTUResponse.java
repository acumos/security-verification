package org.acumos.licensemanager.service.create;

import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.domain.MLPRightToUse;

public class RTUResponse {

  final CreateRTURequest request;
  boolean created;
  boolean updated;
  List<MLPRightToUse> rtu;

  public RTUResponse(CreateRTURequest request) {
    this.request = request;
  }

  /**
   * @return the rtu
   */
  public List<MLPRightToUse> getRtu() {
    return rtu;
  }

  /**
   * @return the request
   */
  public CreateRTURequest getRequest() {
    return request;
  }

  /**
   * @return if rtu was created
   */
  public boolean isCreated() {
    return created;
  }

  /**
   * @param created indicate that rtu was created
   */
  public void setCreated(boolean created) {
    this.created = created;
  }

  /**
   * @return if rtu was updated instead of created
   */
  public boolean isUpdated() {
    return updated;
  }

  /**
   * @param updated indicate that rtu was updated
   */
  public void setUpdated(boolean updated) {
    this.updated = updated;
  }

  public void addRtu(MLPRightToUse rightToUse) {
    if(rtu == null){
      rtu = new ArrayList<MLPRightToUse>();
    }
    rtu.add(rightToUse);
  }

  public List<MLPRightToUse> getRtus() {
    return rtu;
  }
}