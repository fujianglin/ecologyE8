<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="weaver.conn.RecordSet" %>
<%
//数据
 RecordSet rs1 = new RecordSet();
 RecordSet rs2 = new RecordSet();
 RecordSet rs3 = new RecordSet();
 String query1 = "select id,bdcostcenter,bdcoa,bcdetail,bdcom from uf_budgetdetail ";
 rs1.execute(query1);
 while(rs1.next()){
 	String id = rs1.getString("id");
 	String bdcostcenter = rs1.getString("bdcostcenter");
 	String bdcoa = rs1.getString("bdcoa");
 	String bcdetail = rs1.getString("bcdetail");
 	String bdcom =  rs1.getString("bdcom");
 	String bcdetailName = "";
 	String query2 = "select bcdetail  from uf_feetype where id='"+bcdetail+"'";
 	rs2.executeSql(query2);
 	if(rs2.next()){
 		bcdetailName = rs2.getString("bcdetail");
 	}
    //更新
    String newid = "";
    String query3 = " select id from uf_feetype where bdcostcenter='"+bdcostcenter+"' and bdcoa='"+bdcoa+"' and bcdetail='"+bcdetailName+"' and bdcom ='"+bdcom+"' ";
    rs2.executeSql(query3);
    
    if(rs2.getCounts()>1){
    	out.print("aaa>1 id--"+id+"--");
    }
   if(rs2.getCounts()<1){
    	out.print("aaa<1 id--"+id+"--");
    }
 	if(rs2.next()){
 		newid = rs2.getString("id");
 	}
    String updateSql = "update uf_budgetdetail set bcdetail ='"+newid+"' where id='"+id+"'"; 
    rs3.executeSql(updateSql);
 }
%>