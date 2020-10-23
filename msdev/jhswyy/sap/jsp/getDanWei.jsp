<%@ page language="java" import="java.util.*" contentType="text/html;charset=UTF-8"%>
<%@ page import="weaver.general.BaseBean" %>
<%@ page import="weaver.general.*" %>
<%@ page import="weaver.general.*,weaver.conn.*" %>
<%
	 String wlbh=Util.null2s(request.getParameter("wlbh"), "-1");
	RecordSet rs=new RecordSet();
	RecordSet rs1=new RecordSet();
    BaseBean bb =new BaseBean();
    bb.writeLog("进入嘉和!接收物料编号："+wlbh);
    String PurchaseOrderQuantityUnit=""; //订单单位
    String BaseUnit="";//基本单位
    String allID="";//单位返回值
  
    //根据物料编号去单位转换表中进行数据转换
    String dwSql="select * from uf_ProductUomCds  where Product='"+wlbh+"' and 	AlternativeUnit=PurchaseOrderQuantityUnit";
    rs.execute(dwSql);
    //	PurchaseOrderQuantityUnit  订单单位
    // 	BaseUnit 基本单位
    if(rs.next()){
    	PurchaseOrderQuantityUnit=rs.getString("PurchaseOrderQuantityUnit");//订单单位
    	BaseUnit=rs.getString("BaseUnit");//基本单位
    	//做一步转换
    	String selectBaseUnitSql="select 	jldwwb from 	uf_dwmcdz where dw='"+PurchaseOrderQuantityUnit+"'";
    	rs1.execute(selectBaseUnitSql);
    	  if(rs1.next()){
    		  PurchaseOrderQuantityUnit=rs1.getString("jldwwb");//将采购单位转换为中文
    	  }
	    allID=BaseUnit+","+PurchaseOrderQuantityUnit;
 	
    }else{
    	 String dwSqle="select * from uf_ProductUomCds  where Product='"+wlbh+"' ";
    	 
    	 rs.execute(dwSqle);
         if(rs.next()){
        
    	  BaseUnit=rs.getString("BaseUnit");//基本单位
    	  PurchaseOrderQuantityUnit=BaseUnit;
    	//做一步转换
      	String selectBaseUnitSql="select 	jldwwb from 	uf_dwmcdz where dw='"+PurchaseOrderQuantityUnit+"'";
      	rs1.execute(selectBaseUnitSql);
      	  if(rs1.next()){
      		  PurchaseOrderQuantityUnit=rs1.getString("jldwwb");//将采购单位转换为中文
      	  }
    	  
      }
         allID=BaseUnit+","+PurchaseOrderQuantityUnit;
    }
    bb.writeLog("获取单位返回值是："+allID);

%>

<%=allID%>


