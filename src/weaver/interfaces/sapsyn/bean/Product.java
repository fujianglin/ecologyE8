package weaver.interfaces.sapsyn.bean;

//物料信息java bean
public class Product {
    private String Product;
    private String ProductDescription;
    private String MaterialGroup;
    private String MaterialGroupText;
    private String UnitOfMeasure;
    private String ProductType;
    private String MaterialTypeName;
    private String CreationDate;
    private String CreatedByUser;
    private String LastChangeDate;
    private String ProductGroup;
    private String BaseUnit;
    private String Division;
    private String Language;
    private String MaterialGroupName;
    private String LastChangedByUser;
    private String YY1_Model_PRD;
    private String YY1_ManufactureNo_PRD;
    private String YY1_Manufacturer_PRD;
    private String QuantityNumerator;
    private String ProductOldID;
    private String IsBatchManagementRequired;
    private String PurchaseOrderQuantityUnit;
    private String IsMarkedForDeletion;
    public String getIsMarkedForDeletion() {
        return IsMarkedForDeletion;
    }

    public void setIsMarkedForDeletion(String isMarkedForDeletion) {
    	IsMarkedForDeletion = isMarkedForDeletion;
    }
  
    public String getPurchaseOrderQuantityUnit() {
        return PurchaseOrderQuantityUnit;
    }

    public void setPurchaseOrderQuantityUnit(String purchaseOrderQuantityUnit) {
    	PurchaseOrderQuantityUnit = purchaseOrderQuantityUnit;
    }
    public String getProductOldID() {
        return ProductOldID;
    }

    public void setProductOldID(String productOldID) {
    	ProductOldID = productOldID;
    }
    
    public String getIsBatchManagementRequired() {
        return IsBatchManagementRequired;
    }

    public void setIsBatchManagementRequired(String isBatchManagementRequired) {
    	IsBatchManagementRequired = isBatchManagementRequired;
    }
    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getMaterialGroup() {
        return MaterialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        MaterialGroup = materialGroup;
    }

    public String getMaterialGroupText() {
        return MaterialGroupText;
    }

    public void setMaterialGroupText(String materialGroupText) {
        MaterialGroupText = materialGroupText;
    }

    public String getUnitOfMeasure() {
        return UnitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        UnitOfMeasure = unitOfMeasure;
    }

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String productType) {
        ProductType = productType;
    }

    public String getMaterialTypeName() {
        return MaterialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        MaterialTypeName = materialTypeName;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    public String getCreatedByUser() {
        return CreatedByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        CreatedByUser = createdByUser;
    }

    public String getLastChangeDate() {
        return LastChangeDate;
    }

    public void setLastChangeDate(String lastChangeDate) {
        LastChangeDate = lastChangeDate;
    }

    public String getProductGroup() {
        return ProductGroup;
    }

    public void setProductGroup(String productGroup) {
        ProductGroup = productGroup;
    }

    public String getBaseUnit() {
        return BaseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        BaseUnit = baseUnit;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String division) {
        Division = division;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getMaterialGroupName() {
        return MaterialGroupName;
    }

    public void setMaterialGroupName(String materialGroupName) {
        MaterialGroupName = materialGroupName;
    }

    public String getLastChangedByUser() {
        return LastChangedByUser;
    }

    public void setLastChangedByUser(String lastChangedByUser) {
        LastChangedByUser = lastChangedByUser;
    }

    public String getYY1_Model_PRD() {
        return YY1_Model_PRD;
    }

    public void setYY1_Model_PRD(String YY1_Model_PRD) {
        this.YY1_Model_PRD = YY1_Model_PRD;
    }

    public String getYY1_ManufactureNo_PRD() {
        return YY1_ManufactureNo_PRD;
    }

    public void setYY1_ManufactureNo_PRD(String YY1_ManufactureNo_PRD) {
        this.YY1_ManufactureNo_PRD = YY1_ManufactureNo_PRD;
    }

    public String getYY1_Manufacturer_PRD() {
        return YY1_Manufacturer_PRD;
    }

    public void setYY1_Manufacturer_PRD(String YY1_Manufacturer_PRD) {
        this.YY1_Manufacturer_PRD = YY1_Manufacturer_PRD;
    }

    public String getQuantityNumerator() {
        return QuantityNumerator;
    }

    public void setQuantityNumerator(String quantityNumerator) {
        QuantityNumerator = quantityNumerator;
    }
  
    @Override
    public String toString() {
        return "Product{" +
                "Product='" + Product + '\'' +
                ", ProductDescription='" + ProductDescription + '\'' +
                ", MaterialGroup='" + MaterialGroup + '\'' +
                ", MaterialGroupText='" + MaterialGroupText + '\'' +
                ", UnitOfMeasure='" + UnitOfMeasure + '\'' +
                ", ProductType='" + ProductType + '\'' +
                ", MaterialTypeName='" + MaterialTypeName + '\'' +
                ", CreationDate='" + CreationDate + '\'' +
                ", CreatedByUser='" + CreatedByUser + '\'' +
                ", LastChangeDate='" + LastChangeDate + '\'' +
                ", ProductGroup='" + ProductGroup + '\'' +
                ", BaseUnit='" + BaseUnit + '\'' +
                ", Division='" + Division + '\'' +
                ", Language='" + Language + '\'' +
                ", MaterialGroupName='" + MaterialGroupName + '\'' +
                ", LastChangedByUser='" + LastChangedByUser + '\'' +
                ", YY1_Model_PRD='" + YY1_Model_PRD + '\'' +
                ", YY1_ManufactureNo_PRD='" + YY1_ManufactureNo_PRD + '\'' +
                ", YY1_Manufacturer_PRD='" + YY1_Manufacturer_PRD + '\'' +
                ", QuantityNumerator='" + QuantityNumerator + '\'' +
                ", ProductOldID='" + ProductOldID + '\'' +
                ", IsBatchManagementRequired='" + IsBatchManagementRequired + '\'' +
                ", PurchaseOrderQuantityUnit='" + PurchaseOrderQuantityUnit + '\'' +
                ", IsMarkedForDeletion='" + IsMarkedForDeletion + '\'' +
                '}'; 
       
    }
}
