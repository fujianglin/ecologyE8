<%@page import="com.alibaba.fastjson.JSONArray"%>
<%@page import="weaver.formmode.apps.jiahe.SapBrowserAction"%>
<%@page import="weaver.general.Util"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" %>
<%
String saptype = Util.null2String(request.getParameter("saptype"));

SapBrowserAction sapBrowserAction = new SapBrowserAction();
JSONArray jsonArray = new JSONArray();
if(saptype.equals("ProjectBudget")){
	String WBSElement = Util.null2String(request.getParameter("WBSElement"));
	jsonArray =  sapBrowserAction.getProjectBudget(WBSElement);
}else if(saptype.equals("PurchaseOrder")){
	String PurchaseOrder = Util.null2String(request.getParameter("PurchaseOrder"));
	jsonArray =  sapBrowserAction.getPurchaseOrder(PurchaseOrder);
}
out.println(jsonArray);
%>