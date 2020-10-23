<%@ page contentType="text/json;charset=UTF-8" language="java" %>
<%@page import="weaver.general.Util"%>
<%@page import="weaver.general.BaseBean"%>
<%@page import="java.util.*"%>
<%@page import="weaver.conn.RecordSet"%>
<%@page import="com.alibaba.fastjson.JSON"%>
<%
  	//采购业务生命周期图表获取收货验收以及对公付款信息
  	BaseBean bb = new BaseBean();
  	RecordSet rs = new RecordSet();
  	bb.writeLog("开始获取收货付款流程信息");
  	String shlcValues = Util.null2String(request.getParameter("shlcValues"));
	String fklcValues = Util.null2String(request.getParameter("fklcValues"));
	String[] shlcArray = shlcValues.split(",");
	String[] fklcArray = fklcValues.split(",");
	bb.writeLog("shlcValues="+shlcValues+",fklcValues="+fklcValues);
	Map<String, String[]> map = new HashMap<String,String[]>();
	for(int i=0;i<4;i++){
		//收货流程
		String[] shlcMsg = new String[2];
		String selectShBhSql = "select bh from formtable_main_38 where requestid='"+shlcArray[i]+"'";
		rs.execute(selectShBhSql);
		if(rs.next()){
			shlcMsg[0] = Util.null2String(rs.getString("bh"));//编号
		}else{
			shlcMsg[0] = "0";
		}
		String selectShKsrqSql = "select createdate from workflow_requestbase where requestid='"+shlcArray[i]+"'";
		rs.execute(selectShKsrqSql);
		if(rs.next()){
			shlcMsg[1] = Util.null2String(rs.getString("createdate"));//开始日期
		}else{
			shlcMsg[1] = "0";
		}
		map.put("shlc"+(i+1),shlcMsg);
		
		//付款流程
		String[] fklcMsg = new String[2];
		String selectFkBhSql = "select bh from formtable_main_53 where requestid='"+fklcArray[i]+"'";
		rs.execute(selectFkBhSql);
		if(rs.next()){
			fklcMsg[0] = Util.null2String(rs.getString("bh"));//编号
		}else{
			fklcMsg[0] = "0";
		}
		String selectFkKsrqSql = "select createdate from workflow_requestbase where requestid='"+fklcArray[i]+"'";
		rs.execute(selectFkKsrqSql);
		if(rs.next()){
			fklcMsg[1] = Util.null2String(rs.getString("createdate"));//开始日期
		}else{
			fklcMsg[1] = "0";
		}
		map.put("fklc"+(i+1),fklcMsg);
	}
	String result = JSON.toJSONString(map);
	bb.writeLog("结束result="+result);
%>
<%=result%>