<%@ page language="java" import="java.util.*" contentType="text/html;charset=UTF-8"%>
<%@ page import="weaver.general.BaseBean" %>
<%@ page import="weaver.general.*" %>
<%@ page import="weaver.general.*,weaver.conn.*" %>
<jsp:useBean id="LltlSapByOrder" class="msdev.jhswyy.sap.action.LltlSapByOrder" scope="page" />
<%
	 String wlbh=Util.null2s(request.getParameter("wlbh"), "-1");
     String caigouslse= Util.null2s(request.getParameter("caigousls"),"-1");//采购单位数量
     double danjia= Double.parseDouble(Util.null2s(request.getParameter("danjia"),"-1"));//采购单位单价
     double jgsldw= Double.parseDouble(Util.null2s(request.getParameter("jgsldw"),"-1"));//价格数量单位
    double caigousls=Double.parseDouble(caigouslse);
	double jichusl =0;//基础单位数量
	RecordSet rs=new RecordSet();
    BaseBean bb =new BaseBean();
    bb.writeLog("进入嘉和!jsp中获取到的值："+wlbh+"采购数量："+caigousls+"单价："+danjia+"价格数量单位："+jgsldw);
   
    String allID="";//单位返回值
    int fenzi=0;//分子
    int fenmu=0;//分母
    double cgdwdj=0;//采购单位单价(备份)
    //根据物料编号去单位转换表中进行数据转换
    String dwSql="select * from uf_ProductUomCds  where Product='"+wlbh+"' and 	AlternativeUnit=PurchaseOrderQuantityUnit";
    rs.execute(dwSql);
    //	PurchaseOrderQuantityUnit  订单单位
    // 	BaseUnit 基本单位
    if(rs.next()){
    	
    	fenzi=rs.getInt("QuantityNumerator");//分子
    	fenmu=rs.getInt("QuantityDenominator");//分母
    	
    	
    	jichusl=caigousls*fenzi/fenmu;
    	cgdwdj = danjia/jgsldw*fenzi/fenmu;
    	bb.writeLog("分子："+fenzi+"分母："+fenmu+"基本单位数量："+jichusl+"采购单位数量："+caigousls+"采购单位单价:"+cgdwdj);
    		
    	allID=String.valueOf(jichusl)+","+String.valueOf(cgdwdj);
    	
    }else{
    	jichusl=caigousls;
    	cgdwdj=danjia/jgsldw;
    	allID=String.valueOf(jichusl)+","+String.valueOf(cgdwdj);
    }
    bb.writeLog("获取到的单位转换返回值是："+allID);

%>

<%=allID%>


