package weaver.interfaces.sapsyn.bean;

import java.util.ArrayList;

public class PurchaseReqCreateRequest {
    private String PurchaseRequisition;
    private String PurchaseRequisitionType;
    private String PurReqnDescription;
    private ArrayList<PurchaseRequisitionCreateItem> purchaseRequisitionCreateItems;

    public String getPurchaseRequisition() {
        return PurchaseRequisition;
    }

    public void setPurchaseRequisition(String purchaseRequisition) {
        PurchaseRequisition = purchaseRequisition;
    }

    public String getPurchaseRequisitionType() {
        return PurchaseRequisitionType;
    }

    public void setPurchaseRequisitionType(String purchaseRequisitionType) {
        PurchaseRequisitionType = purchaseRequisitionType;
    }

    public String getPurReqnDescription() {
        return PurReqnDescription;
    }

    public void setPurReqnDescription(String purReqnDescription) {
        PurReqnDescription = purReqnDescription;
    }

    public ArrayList<PurchaseRequisitionCreateItem> getPurchaseRequisitionCreateItems() {
        return purchaseRequisitionCreateItems;
    }

    public void setPurchaseRequisitionCreateItems(ArrayList<PurchaseRequisitionCreateItem> purchaseRequisitionCreateItems) {
        this.purchaseRequisitionCreateItems = purchaseRequisitionCreateItems;
    }

    @Override
    public String toString() {
        return "PurchaseReqCreateRequest{" +
                "PurchaseRequisition='" + PurchaseRequisition + '\'' +
                ", PurchaseRequisitionType='" + PurchaseRequisitionType + '\'' +
                ", PurReqnDescription='" + PurReqnDescription + '\'' +
                ", purchaseRequisitionCreateItems=" + purchaseRequisitionCreateItems +
                '}';
    }
}
