<%@page import="weaver.formmode.apps.jiahe.SapSyncDataJob"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
try{
SapSyncDataJob job = new SapSyncDataJob();
job.execute();
out.println("同步程序执行结束");
}catch(Exception e){
	e.printStackTrace();
	out.println("异常："+e.getMessage());
}
%>