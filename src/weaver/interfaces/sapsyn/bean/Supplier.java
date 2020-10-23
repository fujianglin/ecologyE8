package weaver.interfaces.sapsyn.bean;

import java.util.List;

//供应商信息java bean
public class Supplier {
    private String Supplier;
    private String SupplierName;
    private String SupplierFullName;
    private String BusinessPartner;
    private String BusinessPartnerName;
    private String BusinessPartnerFullName;
    private List<SupplierBanks> banks;

    public String getSupplier() {
        return Supplier;
    }

    public void setSupplier(String supplier) {
        Supplier = supplier;
    }

    public String getSupplierName() {
        return SupplierName;
    }

    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }

    public String getSupplierFullName() {
        return SupplierFullName;
    }

    public void setSupplierFullName(String supplierFullName) {
        SupplierFullName = supplierFullName;
    }

    public String getBusinessPartner() {
        return BusinessPartner;
    }

    public void setBusinessPartner(String businessPartner) {
        BusinessPartner = businessPartner;
    }

    public String getBusinessPartnerName() {
        return BusinessPartnerName;
    }

    public void setBusinessPartnerName(String businessPartnerName) {
        BusinessPartnerName = businessPartnerName;
    }

    public String getBusinessPartnerFullName() {
        return BusinessPartnerFullName;
    }

    public void setBusinessPartnerFullName(String businessPartnerFullName) {
        BusinessPartnerFullName = businessPartnerFullName;
    }

    public List<SupplierBanks> getBanks() {
        return banks;
    }

    public void setBanks(List<SupplierBanks> banks) {
        this.banks = banks;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "Supplier='" + Supplier + '\'' +
                ", SupplierName='" + SupplierName + '\'' +
                ", SupplierFullName='" + SupplierFullName + '\'' +
                ", BusinessPartner='" + BusinessPartner + '\'' +
                ", BusinessPartnerName='" + BusinessPartnerName + '\'' +
                ", BusinessPartnerFullName='" + BusinessPartnerFullName + '\'' +
                ", banks=" + banks +
                '}';
    }
}
