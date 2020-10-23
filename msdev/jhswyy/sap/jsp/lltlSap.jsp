<%@ page language="java" import="java.util.*" contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="weaver.general.*" %>
<jsp:useBean id="LltlSapByOrder" class="msdev.jhswyy.sap.action.LltlSapByOrder" scope="page" />
<%
	String ddbh=Util.null2s(request.getParameter("ddbh"), "-1");//撤销评价 ids
    String lltlList = LltlSapByOrder.lltlList(ddbh);

%>

<%=lltlList%>


