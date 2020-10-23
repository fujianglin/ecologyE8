package weaver.interfaces.sapsyn.bean;
//项目信息java bean
public class Project {
    private String ProjectUUID;
    private String ProjectInternalID;
    private String Project;
    private String ProjectDescription;
    private String EnterpriseProjectType;
    private String PriorityCode;
    private String ProjectStartDate;
    private String ProjectEndDate;
    private String ProcessingStatus;
    private String ResponsibleCostCenter;
    private String ProfitCenter;
    private String ProjectManagerUUID;
    private String ProjectProfileCode;
    private String FunctionalArea;
    private String CompanyCode;
    private String ControllingArea;
    private String LastChangeDateTime;

    public String getProjectUUID() {
        return ProjectUUID;
    }

    public void setProjectUUID(String projectUUID) {
        ProjectUUID = projectUUID;
    }

    public String getProjectInternalID() {
        return ProjectInternalID;
    }

    public void setProjectInternalID(String projectInternalID) {
        ProjectInternalID = projectInternalID;
    }

    public String getProject() {
        return Project;
    }

    public void setProject(String project) {
        Project = project;
    }

    public String getProjectDescription() {
        return ProjectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        ProjectDescription = projectDescription;
    }

    public String getEnterpriseProjectType() {
        return EnterpriseProjectType;
    }

    public void setEnterpriseProjectType(String enterpriseProjectType) {
        EnterpriseProjectType = enterpriseProjectType;
    }

    public String getPriorityCode() {
        return PriorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        PriorityCode = priorityCode;
    }

    public String getProjectStartDate() {
        return ProjectStartDate;
    }

    public void setProjectStartDate(String projectStartDate) {
        ProjectStartDate = projectStartDate;
    }

    public String getProjectEndDate() {
        return ProjectEndDate;
    }

    public void setProjectEndDate(String projectEndDate) {
        ProjectEndDate = projectEndDate;
    }

    public String getProcessingStatus() {
        return ProcessingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        ProcessingStatus = processingStatus;
    }

    public String getResponsibleCostCenter() {
        return ResponsibleCostCenter;
    }

    public void setResponsibleCostCenter(String responsibleCostCenter) {
        ResponsibleCostCenter = responsibleCostCenter;
    }

    public String getProfitCenter() {
        return ProfitCenter;
    }

    public void setProfitCenter(String profitCenter) {
        ProfitCenter = profitCenter;
    }

    public String getProjectManagerUUID() {
        return ProjectManagerUUID;
    }

    public void setProjectManagerUUID(String projectManagerUUID) {
        ProjectManagerUUID = projectManagerUUID;
    }

    public String getProjectProfileCode() {
        return ProjectProfileCode;
    }

    public void setProjectProfileCode(String projectProfileCode) {
        ProjectProfileCode = projectProfileCode;
    }

    public String getFunctionalArea() {
        return FunctionalArea;
    }

    public void setFunctionalArea(String functionalArea) {
        FunctionalArea = functionalArea;
    }

    public String getCompanyCode() {
        return CompanyCode;
    }

    public void setCompanyCode(String companyCode) {
        CompanyCode = companyCode;
    }

    public String getControllingArea() {
        return ControllingArea;
    }

    public void setControllingArea(String controllingArea) {
        ControllingArea = controllingArea;
    }

    public String getLastChangeDateTime() {
        return LastChangeDateTime;
    }

    public void setLastChangeDateTime(String lastChangeDateTime) {
        LastChangeDateTime = lastChangeDateTime;
    }

    @Override
    public String toString() {
        return "Project{" +
                "ProjectUUID='" + ProjectUUID + '\'' +
                ", ProjectInternalID='" + ProjectInternalID + '\'' +
                ", Project='" + Project + '\'' +
                ", ProjectDescription='" + ProjectDescription + '\'' +
                ", EnterpriseProjectType='" + EnterpriseProjectType + '\'' +
                ", PriorityCode='" + PriorityCode + '\'' +
                ", ProjectStartDate='" + ProjectStartDate + '\'' +
                ", ProjectEndDate='" + ProjectEndDate + '\'' +
                ", ProcessingStatus='" + ProcessingStatus + '\'' +
                ", ResponsibleCostCenter='" + ResponsibleCostCenter + '\'' +
                ", ProfitCenter='" + ProfitCenter + '\'' +
                ", ProjectManagerUUID='" + ProjectManagerUUID + '\'' +
                ", ProjectProfileCode='" + ProjectProfileCode + '\'' +
                ", FunctionalArea='" + FunctionalArea + '\'' +
                ", CompanyCode='" + CompanyCode + '\'' +
                ", ControllingArea='" + ControllingArea + '\'' +
                ", LastChangeDateTime='" + LastChangeDateTime + '\'' +
                '}';
    }
}
