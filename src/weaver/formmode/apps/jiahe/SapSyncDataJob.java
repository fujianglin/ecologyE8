package weaver.formmode.apps.jiahe;

import com.alibaba.fastjson.JSONObject;
import com.weaver.formmodel.util.DateHelper;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dom4j.Document;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.file.Prop;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.integration.util.XMLUtil;
import weaver.interfaces.schedule.BaseCronJob;

public class SapSyncDataJob extends BaseCronJob
{
  private BaseBean baseBean = new BaseBean();

  public static void main(String[] args)
  {
  }

  public void sync()
  {
  }

  public Map<String, SapBugetDto> getWbsBudgetData(String wsdl, String username, String password, String years)
  {
    String[] yearArray = years.split(",");
    Map map = new HashMap();
    for (int m = 0; m < yearArray.length; ++m) {
      String year = Util.null2String(yearArray[m]);
      if (year.length() > 0) {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wbs=\"http://www.genor.com/WBSBudget/\"><soapenv:Header/><soapenv:Body><wbs:WBSBudgetRequest><FiscalYear>" + 
          year + "</FiscalYear>" + 
          "</wbs:WBSBudgetRequest>" + 
          "</soapenv:Body>" + 
          "</soapenv:Envelope>";
        this.baseBean.writeLog("WbsBudget参数:" + xml);
        String data = HttpsUtil.httpsRequest(wsdl, "POST", xml, username, password);
        this.baseBean.writeLog("WbsBudget结果：" + data);
        Document doc = XMLUtil.parseXMLStr(data);
        Element Envelope = doc.getRootElement();
        Envelope.addNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        Element Body = Envelope.element("Body");
        List YY1_PROJECT_API_Array = Body.elements("YY1_PROJECT_API");
        for (int i = 0; i < YY1_PROJECT_API_Array.size(); ++i) {
          Element YY1_PROJECT_API = (Element)YY1_PROJECT_API_Array.get(i);
          List YY1_PROJECT_APIType_Array = YY1_PROJECT_API.elements("YY1_PROJECT_APIType");
          for (int j = 0; j < YY1_PROJECT_APIType_Array.size(); ++j) {
            Element YY1_PROJECT_APIType = (Element)YY1_PROJECT_APIType_Array.get(j);
            String FiscalYear = Util.null2String((YY1_PROJECT_APIType.element("FiscalYear") == null) ? "" : YY1_PROJECT_APIType.element("FiscalYear").getTextTrim());
            String WBSElement = Util.null2String((YY1_PROJECT_APIType.element("WBSElement") == null) ? "" : YY1_PROJECT_APIType.element("WBSElement").getTextTrim());
            String WBSDescription = Util.null2String((YY1_PROJECT_APIType.element("WBSDescription") == null) ? "" : YY1_PROJECT_APIType.element("WBSDescription").getTextTrim());
            String GLAccount = Util.null2String((YY1_PROJECT_APIType.element("GLAccount") == null) ? "" : YY1_PROJECT_APIType.element("GLAccount").getTextTrim());

            if ((FiscalYear.length() != 0) && (WBSElement.length() != 0)) { if (GLAccount.length() == 0)
                continue;

              String Key = "W_" + FiscalYear + WBSElement + GLAccount;
              SapBugetDto sapBugetDto = null;
              if (map.containsKey(Key)) {
                sapBugetDto = (SapBugetDto)map.get(Key);
              } else {
                sapBugetDto = new SapBugetDto();
                sapBugetDto.setBudgetyear(FiscalYear);
                sapBugetDto.setWBSElement(WBSElement);
                sapBugetDto.setWBSDescription(WBSDescription);
                sapBugetDto.setGLAccount(GLAccount);
                sapBugetDto.setKey(Key);
                map.put(Key, sapBugetDto);
              }
              double PlanAmountInGlobalCurrency = 0D;
              double ActualAmountInGlobalCurrenc = 0D;
              String PlanningCategory = Util.null2String((YY1_PROJECT_APIType.element("PlanningCategory") == null) ? "" : YY1_PROJECT_APIType.element("PlanningCategory").getTextTrim());
              String Ledger = Util.null2String(YY1_PROJECT_APIType.element("Ledger").getTextTrim());
              String IsCommitment = Util.null2String(YY1_PROJECT_APIType.element("IsCommitment").getTextTrim());
              if ("BUDGET01".equals(PlanningCategory)) {
                PlanAmountInGlobalCurrency = Util.getDoubleValue(YY1_PROJECT_APIType.element("PlanAmountInGlobalCurrency").getTextTrim(), 0D);
                sapBugetDto.setPlanAmountInGlobalCurrency(
                  new BigDecimal(sapBugetDto.getPlanAmountInGlobalCurrency().add(new BigDecimal(PlanAmountInGlobalCurrency)).setScale(2, 4).toPlainString()));
              }

              if (("ACT01".equals(PlanningCategory)) && ("0L".equals(Ledger))) {
                ActualAmountInGlobalCurrenc = Util.getDoubleValue(YY1_PROJECT_APIType.element("ActualAmountInGlobalCurrenc").getTextTrim(), 0D);
                sapBugetDto.setActualAmountInGlobalCurrenc(
                  new BigDecimal(sapBugetDto.getActualAmountInGlobalCurrenc().add(new BigDecimal(ActualAmountInGlobalCurrenc)).setScale(2, 4).toPlainString()));
              }

              if ("TRUE".equalsIgnoreCase(IsCommitment)) {
                ActualAmountInGlobalCurrenc = Util.getDoubleValue(YY1_PROJECT_APIType.element("ActualAmountInGlobalCurrenc").getTextTrim(), 0D);
                sapBugetDto.setDongjie(
                  new BigDecimal(sapBugetDto.getDongjie().add(new BigDecimal(ActualAmountInGlobalCurrenc)).setScale(2, 4).toPlainString()));
              }
            }
          }
        }
      }
    }
    return map;
  }

  public Map<String, SapBugetDto> getCostCenterData(String wsdl, String username, String password, String years) {
    String[] yearArray = years.split(",");
    Map map = new HashMap();
    for (int m = 0; m < yearArray.length; ++m) {
      String year = Util.null2String(yearArray[m]);
      if (year.length() > 0) {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cos=\"http://www.genor.com/CostCenterBudget/\"><soapenv:Header/><soapenv:Body><cos:CostCenterBudgetRequest><FiscalYear>" + 
          year + "</FiscalYear>" + 
          "</cos:CostCenterBudgetRequest>" + 
          "</soapenv:Body>" + 
          "</soapenv:Envelope>";
        this.baseBean.writeLog("CostCenter参数:" + xml);
        String data = HttpsUtil.httpsRequest(wsdl, "POST", xml, username, password);
        this.baseBean.writeLog("CostCenter结果:" + data);
        Document doc = XMLUtil.parseXMLStr(data);
        Element Envelope = doc.getRootElement();
        Envelope.addNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        Element Body = Envelope.element("Body");
        List YY1_CostCenter_API_Array = Body.elements("YY1_CostCenter_API");

        for (int i = 0; i < YY1_CostCenter_API_Array.size(); ++i) {
          Element YY1_CostCenter_API = (Element)YY1_CostCenter_API_Array.get(i);
          List YY1_CostCenter_APIType_Array = YY1_CostCenter_API.elements("YY1_CostCenter_APIType");
          for (int j = 0; j < YY1_CostCenter_APIType_Array.size(); ++j) {
            Element YY1_CostCenter_APIType = (Element)YY1_CostCenter_APIType_Array.get(j);
            String FiscalYear = Util.null2String(YY1_CostCenter_APIType.element("FiscalYear").getTextTrim());
            String CostCenter = Util.null2String(YY1_CostCenter_APIType.element("CostCenter").getTextTrim());
            String GLAccount = Util.null2String(YY1_CostCenter_APIType.element("GLAccount").getTextTrim());

            if ((FiscalYear.length() != 0) && (CostCenter.length() != 0)) { if (GLAccount.length() == 0)
                continue;

              String Key = "C_" + FiscalYear + CostCenter + GLAccount;
              SapBugetDto sapBugetDto = null;
              if (map.containsKey(Key)) {
                sapBugetDto = (SapBugetDto)map.get(Key);
              } else {
                sapBugetDto = new SapBugetDto();
                sapBugetDto.setBudgetyear(FiscalYear);
                sapBugetDto.setCostCenter(CostCenter);
                sapBugetDto.setGLAccount(GLAccount);
                sapBugetDto.setKey(Key);
                map.put(Key, sapBugetDto);
              }
              double PlanAmountInGlobalCurrency = 0D;
              double ActualAmountInGlobalCurrenc = 0D;
              String PlanningCategory = Util.null2String(YY1_CostCenter_APIType.element("PlanningCategory").getTextTrim());
              String Ledger = Util.null2String(YY1_CostCenter_APIType.element("Ledger").getTextTrim());
              String IsCommitment = Util.null2String(YY1_CostCenter_APIType.element("IsCommitment").getTextTrim());
              if ("PLN".equals(PlanningCategory)) {
                PlanAmountInGlobalCurrency = Util.getDoubleValue(YY1_CostCenter_APIType.element("PlanAmountInGlobalCurrency").getTextTrim(), 0D);
                sapBugetDto.setPlanAmountInGlobalCurrency(
                  new BigDecimal(sapBugetDto.getPlanAmountInGlobalCurrency().add(new BigDecimal(PlanAmountInGlobalCurrency)).setScale(2, 4).toPlainString()));
              }

              if (("ACT01".equals(PlanningCategory)) && ("0L".equals(Ledger))) {
                ActualAmountInGlobalCurrenc = Util.getDoubleValue(YY1_CostCenter_APIType.element("ActualAmountInGlobalCurrenc").getTextTrim(), 0D);
                sapBugetDto.setActualAmountInGlobalCurrenc(
                  new BigDecimal(sapBugetDto.getActualAmountInGlobalCurrenc().add(new BigDecimal(ActualAmountInGlobalCurrenc)).setScale(2, 4).toPlainString()));
              }

              if ("TRUE".equalsIgnoreCase(IsCommitment)) {
                ActualAmountInGlobalCurrenc = Util.getDoubleValue(YY1_CostCenter_APIType.element("ActualAmountInGlobalCurrenc").getTextTrim(), 0D);
                sapBugetDto.setDongjie(
                  new BigDecimal(sapBugetDto.getDongjie().add(new BigDecimal(ActualAmountInGlobalCurrenc)).setScale(2, 4).toPlainString()));
              }
            }
          }
        }
      }
    }
    return map;
  }

  public void execute()
  {
	  this.baseBean.writeLog("计划任务开关！");
	  String jhrwl="weaver.formmode.apps.jiahe.SapSyncDataJob";//计划任务类路径
	  RecordSet jhrwRs = new RecordSet();
	  RecordSet selectRs = new RecordSet();
	  String jhrwZtSql="select sfqy from uf_sapJhControl where jhrwl='"+jhrwl+"'";
	  jhrwRs.execute(jhrwZtSql);
	  if(jhrwRs.next()){
	  	String sfqy = jhrwRs.getString("sfqy");//是否启用 0是启用  1否禁用
	  	if("0".equals(sfqy)){//括号里面放置计划任务代码
	  		this.baseBean.writeLog("执行计划任务！");
	  		
	  		String CostCentwsdl = Util.null2String(Prop.getPropValue("jhsapbrowser", "CostCentUrl"));
	  	    String WbsBudgetwsdl = Util.null2String(Prop.getPropValue("jhsapbrowser", "WBSBudgetUrl"));
	  	    String username = Util.null2String(Prop.getPropValue("jhsapbrowser", "username"));
	  	    String password = Util.null2String(Prop.getPropValue("jhsapbrowser", "password"));
	  	    String currentYear = DateHelper.getCurrentYear();
	  	    String years = (Util.getIntValue(currentYear, 0) - 1) + "," + currentYear + "," + (Util.getIntValue(currentYear, 0) + 1);

	  	    CostCentwsdl = "https://l600119-iflmap.hcisbp.cn1.hana.ondemand.com:443/cxf/OAtoS4_ReadCostCenterBudget";
	  	    WbsBudgetwsdl = "https://l600119-iflmap.hcisbp.cn1.hana.ondemand.com:443/cxf/OAtoS4_Read_WBS_Budget";

	  	    this.baseBean.writeLog("CostCentwsdl:" + CostCentwsdl);
	  	    this.baseBean.writeLog("WbsBudgetwsdl:" + WbsBudgetwsdl);
	  	    this.baseBean.writeLog("username:" + username);
	  	    this.baseBean.writeLog("password:" + password);
	  	    this.baseBean.writeLog("years:" + years);
	  	    Map map = getCostCenterData(CostCentwsdl, username, password, years);

	  	    Map wbsmap = getWbsBudgetData(WbsBudgetwsdl, username, password, years);

	  	    map.putAll(wbsmap);
	  	    RecordSet rs = new RecordSet();
	  	    rs.executeUpdate("delete from uf_buget", new Object[0]);
	  	    Iterator it = map.keySet().iterator();
	  	    int modeid = 11;
	  	    int userid = 1;
	  	    int usertype = 0;
	  	    this.baseBean.writeLog("最终数据JSON：" + JSONObject.toJSON(map));

	  	    while (it.hasNext()) {
	  	      String key = Util.null2String((String)it.next());
	  	      SapBugetDto dto = (SapBugetDto)map.get(key);
	  	      //转换成本中心和费用科目，添加成本中心名称和费用科目名称
	  	      String NewCostCenter="";
	  	      String NewGLAccount="";
	  	      String CostCenterText="";
	  	      String GLAccountText="";
	  	      String selectCosterCenterSql = "select id,departmentname from HrmDepartment where id in (select deptid from hrmdepartmentDefined where SAPcbzxx='"+dto.getCostCenter()+"') and canceled=0";
	  	      this.baseBean.writeLog("selectCosterCenterSql=" + selectCosterCenterSql);
	  	      selectRs.execute(selectCosterCenterSql);
	  	      if(selectRs.next()){
	  	    	NewCostCenter=selectRs.getString("id");
	  	    	CostCenterText=selectRs.getString("departmentname");
	  	      }else{
	  	    	CostCenterText=dto.getCostCenter(); 
	  	      }
	  	      String selectGLAccountSql = "select kmdm,kmms from uf_kmlb where kmdm='00"+dto.getGLAccount()+"'";
	  	      this.baseBean.writeLog("selectGLAccountSql=" + selectGLAccountSql);
	  	      selectRs.execute(selectGLAccountSql);
	  	      if(selectRs.next()){
	  	    	NewGLAccount=selectRs.getString("kmdm");
	  	    	GLAccountText=selectRs.getString("kmms");
	  	      }else{
	  	    	GLAccountText=dto.getGLAccount();
	  	      }
	  	      this.baseBean.writeLog("NewCostCenter：" + NewCostCenter+"----NewGLAccount:"+NewGLAccount);
	  	      
	  	      
	  	      String sql = "insert into uf_buget(WBSElement,WBSDescription,PlanAmountInGlobalCurrency,keyong,dongjie,ActualAmountInGlobalCurrenc,budgetyear,CostCenter,GLAccount,formmodeid,modedatacreater,modedatacreatertype,modedatacreatedate,modedatacreatetime,NewCostCenter,NewGLAccount,CostCenterText,GLAccountText)values('" + 
	  	        dto.getWBSElement() + "'," + 
	  	        "'" + dto.getWBSDescription() + "'," + 
	  	        "'" + dto.getPlanAmountInGlobalCurrency().setScale(2, 4).toPlainString() + "'," + 
	  	        "'" + dto.getKeyong().setScale(2, 4).toPlainString() + "'," + 
	  	        "'" + dto.getDongjie().setScale(2, 4).toPlainString() + "'," + 
	  	        "'" + dto.getActualAmountInGlobalCurrenc().setScale(2, 4).toPlainString() + "'," + 
	  	        "'" + dto.getBudgetyear() + "'," + 
	  	        "'" + dto.getCostCenter() + "'," + 
	  	        "'" + dto.getGLAccount() + "'," + 
	  	        "'" + modeid + "'," + 
	  	        "'" + userid + "'," + 
	  	        "'" + usertype + "'," + 
	  	        "'" + DateHelper.getCurrentDate() + "'," + 
	  	        "'" + DateHelper.getCurrentTime() + "'," + 
	  	        "'" + NewCostCenter + "'," +
	  	        "'" + NewGLAccount + "'," +
	  	        "'" + CostCenterText + "'," +
	  	        "'" + GLAccountText + "'" +
	  	        ")";
	  	      this.baseBean.writeLog("插入SQL：" + sql);
	  	      rs.executeSql(sql);
	  	    }

	  	    rs.executeSql("delete from modeDataShare_" + modeid);
	  	    rs.executeSql("delete from modeDataShare_" + modeid + "_set");
	  	    rs.executeQuery("select * from uf_buget", new Object[0]);
	  	    while (rs.next()) {
	  	      int sourceId = rs.getInt("id");
	  	      ModeRightInfo modeRightInfo = new ModeRightInfo();
	  	      modeRightInfo.setNewRight(true);
	  	      modeRightInfo.editModeDataShare(1, 11, sourceId);
	  	    }
	  		
	  	}else{
	  		this.baseBean.writeLog("不执行计划任务！");
	  	}
	  }
	  
    
  }
}