package com.finetra.finecovidalert;

public class ListDepotReport {
    String depotName,posDate,posEndDate,staffName,keyValue;
    public ListDepotReport(){

    }

    public ListDepotReport(String depotName, String posDate, String posEndDate, String staffName,String keyValue) {
        this.depotName = depotName;
        this.posDate = posDate;
        this.posEndDate = posEndDate;
        this.staffName = staffName;
        this.keyValue=keyValue;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public String getPosDate() {
        return posDate;
    }

    public void setPosDate(String posDate) {
        this.posDate = posDate;
    }

    public String getPosEndDate() {
        return posEndDate;
    }

    public void setPosEndDate(String posEndDate) {
        this.posEndDate = posEndDate;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
}
