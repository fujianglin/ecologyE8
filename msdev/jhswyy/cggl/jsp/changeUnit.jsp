<%@ page contentType="text/json;charset=UTF-8" language="java" %>
<%@page import="weaver.general.Util"%>
<%@page import="weaver.general.BaseBean"%>
<%@page import="java.util.*"%>
<%@page import="weaver.conn.RecordSet"%>
<%@page import="com.alibaba.fastjson.JSON"%>
<%
	BaseBean bb = new BaseBean();
  	RecordSet rs = new RecordSet();
  	RecordSet rs2 = new RecordSet();
  	RecordSet updateRs = new RecordSet();
  	String querySql = "select * from uf_jldw";
  	rs.execute(querySql);
  	while(rs.next()){
  		String queryChineseSql = "select jldwwb from uf_dwmcdz where dw='"+rs.getString("dwmc")+"'";
  		bb.writeLog(queryChineseSql);
  		rs2.execute(queryChineseSql);
  		String id = Util.null2String(rs.getString("id"));
  		if(rs2.next()){
  			String jldwwb = Util.null2String(rs2.getString("jldwwb"));
  			bb.writeLog(jldwwb);

			String updateSql = "update uf_jldw set xsmc='"+jldwwb+"' where id='"+id+"'";
			bb.writeLog("updateSql1="+updateSql);
			updateRs.execute(updateSql);
  		}else{
  			String updateSql = "update uf_jldw set xsmc='"+rs.getString("dwmc")+"' where id='"+id+"'";
  			bb.writeLog("updateSql2="+updateSql);
  			updateRs.execute(updateSql);
  		}
  	}
%>
