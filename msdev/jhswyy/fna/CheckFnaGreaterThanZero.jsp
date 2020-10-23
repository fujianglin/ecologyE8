<%@page import="weaver.general.Util"%>
<%@page import="java.util.*"%>
<%@page import="weaver.conn.RecordSet"%>
<%@page import="weaver.general.BaseBean"%>
<%
BaseBean bb = new BaseBean();
RecordSet rs = new RecordSet();
//提交控制的数据 格式： 预算年份,成本中心,费用科目,预算明细,采购总价格（未税）|预算年份,成本中心,费用科目,预算明细,采购总价格（未税）
String dataCheckStr = Util.null2String(request.getParameter("dataCheckStr"));
bb.writeLog("dataCheckStr:"+dataCheckStr);
String[] fnaData = dataCheckStr.split("\\|");
//成本中心预算
Map cbzxDataMap = new HashMap();
//预算明细预算
Map ysmxDataMap = new HashMap();
boolean ifCbzxOut = false;
boolean ifYsmxOut = false;
//取数据，进行合并
for(int i=0;i<fnaData.length;i++){
	String fnaDataDetail = fnaData[i];
	bb.writeLog("fnaDataDetail:"+fnaDataDetail);
	String[] detail = fnaDataDetail.split(",");
	String bugetyear = detail[0];//预算年份
	String cbzx = detail[1];     //成本中心
	String gLAccount = detail[2];//费用科目,
	String ysmx = detail[3];     //预算明细
	double cgzjg = Util.getDoubleValue(detail[4],0);    //采购总价格（未税）
	
	String cbzxDataKey = bugetyear+"_"+cbzx+"_"+gLAccount;
	String ysmxDataKey = bugetyear+"_"+cbzx+"_"+ysmx;
	double cbzxDataValue = 0;
	if(cbzxDataMap.get(cbzxDataKey)!=null){
		cbzxDataValue = (Double)cbzxDataMap.get(cbzxDataKey) ;
	}
	cbzxDataValue += cgzjg;
	cbzxDataMap.put(cbzxDataKey,cbzxDataValue);
	double ysmxDataValue = 0;
	if(ysmxDataMap.get(ysmxDataKey)!=null){
		ysmxDataValue = (Double)ysmxDataMap.get(ysmxDataKey) ;
	}
	ysmxDataValue += cgzjg;
	ysmxDataMap.put(ysmxDataKey,ysmxDataValue);
}
//判断数据 成本中心预算
Iterator iterCbzx = cbzxDataMap.keySet().iterator();
while (iterCbzx.hasNext()) {
	String key1 = (String)iterCbzx.next();
	double val1 = (Double)cbzxDataMap.get(key1) ;//申请金额
	bb.writeLog("val11111111111:"+val1);
	String[] key1Str = key1.split("_");
	String bugetyear = key1Str[0];//预算年份
	String cbzx = key1Str[1];     //成本中心
	String gLAccount = key1Str[2];//费用科目
	String wxzc="0016008000";//无形资产科目代码
	String gdzc="0000000001";//固定资产科目代码
	bb.writeLog("gLAccount:"+gLAccount);
	if(!wxzc.equals(gLAccount) && !gdzc.equals(gLAccount)){
		String queryData = "select keyong from uf_buget where budgetyear='"+bugetyear+"' and CostCenter=(select sapcbzxx from dept_view where id='"
					 + cbzx+"') and GLAccount=substring('"+gLAccount+"',3,333)";
		bb.writeLog("queryData1111:"+queryData);
		rs.executeSql(queryData);
		double ky1 = 0;//可用金额
		if(rs.next()){
			ky1 = rs.getDouble("keyong");
		}
		if(ky1<=0){
			ifCbzxOut = true;
		}
	}
}


//判断数据 预算明细预算
Iterator iterYsmx = ysmxDataMap.keySet().iterator();
while (iterYsmx.hasNext()) {
	String key2 = (String)iterYsmx.next();
	double val2 = (Double)ysmxDataMap.get(key2) ;//申请金额
	bb.writeLog("val2222222:"+val2);
	String[] key1Str = key2.split("_");
	String bugetyear = key1Str[0];//预算年份
	String cbzx = key1Str[1];     //成本中心
	String ysmx = key1Str[2];     //预算明细
	
	String queryData = "select bdavalibalebudget from uf_budgetdetailinfo where bdyear='"+bugetyear+"' and  bdcostcenter='"
					 + cbzx+"' and  bcdetail='"+ysmx+"'";
	rs.executeSql(queryData);
	bb.writeLog("queryData2222222:"+queryData);
	double ky2 = 0;//可用金额
	if(rs.next()){
		ky2 = rs.getDouble("bdavalibalebudget");
	}
	if(ky2<=0){
		ifYsmxOut = true;
	}
}
String returnVal = "";
if(ifCbzxOut){
	returnVal = "1";
	if(ifYsmxOut){
		returnVal = "3";
	}
}else{
	if(ifYsmxOut){
		returnVal = "2";
	}
}
%>
<%=returnVal%>