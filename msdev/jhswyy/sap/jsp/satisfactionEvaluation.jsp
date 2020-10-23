<%@ page language="java" import="java.util.*" contentType="text/html;charset=UTF-8"%>
<%@ page import="weaver.general.*" %>
<%@ page import="net.sf.json.*" %>
<%@ page import="weaver.conn.RecordSet" %>
<%@ page import="weaver.general.BaseBean" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="weaver.formmode.setup.ModeRightInfo" %>
<%@ page import="weaver.general.Util" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="weaver.proj.Maint.*"%>
<%@ page import="java.math.*" %>
<%@ page import="weaver.hrm.HrmUserVarify" %>
<%@ page import="weaver.hrm.User" %>
<jsp:useBean id="ResourceComInfo" class="weaver.hrm.resource.ResourceComInfo" scope="page"/>
<jsp:useBean id="TriggersProcess" class="msdev.jhswyy.sap.action.TriggersProcess" scope="page" />
<%
RecordSet rs=new RecordSet();
BaseBean bb=new BaseBean(); 

String ids=Util.null2s(request.getParameter("ids"), "-1");//申请单编号
bb.writeLog("ids："+ids);
User user = HrmUserVarify.checkUser (request , response) ;
int idss=user.getUID();
bb.writeLog("获取当前操作者："+idss+"");
bb.writeLog("============================创建评价流程 ids="+ids+"============================");
String returnState2 = TriggersProcess.GeneratingProcess(ids,idss);

String returnState = returnState2;
bb.writeLog("============================返回结果returnState "+returnState+"============================");
%>

<%=returnState%>

