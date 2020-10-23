<%@ page language="java" import="java.util.*" contentType="text/html;charset=UTF-8"%>
<%@ page import="weaver.general.*" %>
<%@ page import="weaver.general.BaseBean" %>
<jsp:useBean id="MxysxxhzAction2" class="msdev.jhswyy.cggl.action.MxysxxhzAction2" scope="page" />
<%
BaseBean bb=new BaseBean(); 
bb.writeLog("============================mxysxxhz.jsp 开始明细预算信息汇总 ============================");
String returnState = MxysxxhzAction2.execute(); 
bb.writeLog("============================mxysxxhz.jsp 结束明细预算信息汇总============================");
%>

<%=returnState%>