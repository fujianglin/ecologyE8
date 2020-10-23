<%@ page language="java" import="java.util.*" contentType="text/html;charset=UTF-8"%>
<%@ page import="weaver.general.BaseBean" %>
<%@ page import="weaver.general.*" %>
<%@ page import="weaver.general.*,weaver.conn.*" %>
<%
	 String wlbh=Util.null2s(request.getParameter("wlbh"), "-1");
     String caigouslse= Util.null2s(request.getParameter("caigousls"),"-1");//采购单位数量
     double caigousls=Double.parseDouble(caigouslse);
     double jichusl =0;//基础单位数量
	RecordSet rs=new RecordSet();
    BaseBean bb =new BaseBean();
    bb.writeLog("进入嘉和!jsp中获取到的值："+wlbh+"采购数量："+caigousls);
    
    String allID="";//单位返回值
    int fenzi=0;//分子
    int fenmu=0;//分母
    //根据物料编号去单位转换表中进行数据转换
    String dwSql="select * from uf_ProductUomCds  where Product='"+wlbh+"' and 	AlternativeUnit=PurchaseOrderQuantityUnit";
    rs.execute(dwSql);
    //	PurchaseOrderQuantityUnit  订单单位
    // 	BaseUnit 基本单位
    if(rs.next()){
    	
    	fenzi=rs.getInt("QuantityNumerator");//分子
    	fenmu=rs.getInt("QuantityDenominator");//分母
    	jichusl=caigousls*fenzi/fenmu;
    	
    	
	    allID=String.valueOf(jichusl);
 	
    }else{

        	 jichusl=caigousls;
 
         allID=String.valueOf(jichusl);
    }
    bb.writeLog("获取单位返回值是："+allID);

%>

<%=allID%>


