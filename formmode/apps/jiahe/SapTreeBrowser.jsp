<%@page import="weaver.formmode.apps.jiahe.SapBrowserAction"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@page import="weaver.meeting.MeetingBrowser"%>
<%@ taglib uri="/WEB-INF/weaver.tld" prefix="wea"%>
<%@ include file="/systeminfo/init_wev8.jsp" %>
<%
 String sapBrowserType=Util.null2String(request.getParameter("sapBrowserType")); 
 int langid=user.getLanguage();
%>

<HTML><HEAD>
<style type="text/css">
.tab_box {
 height:500px !important;
}
</style>
<LINK href="/css/Weaver_wev8.css" type=text/css rel=STYLESHEET>
<SCRIPT language="javascript" src="/js/weaver_wev8.js"></script>
<link rel="stylesheet" href="/wui/common/jquery/plugin/zTree/css/demo_wev8.css" type="text/css">
<link rel="stylesheet" href="/wui/common/jquery/plugin/zTree/css/zTreeStyle/zTreeStyle_wev8.css" type="text/css">
<script type="text/javascript" src="/wui/common/jquery/plugin/zTree/js/jquery-1.4.4.min_wev8.js"></script>
<script type="text/javascript" src="/wui/common/jquery/plugin/zTree/js/jquery.ztree.core.min_wev8.js"></script>
</HEAD>
<%
String imagefilename = "/images/hdReport_wev8.gif";
String titlename = "";
if(sapBrowserType.equals("ProjectBudget")){
	titlename = "项目预算";
}else{
	titlename = "PO信息";
}
String needfav ="1";
String needhelp ="";
%>

<BODY>
<%@ include file="/systeminfo/TopTitle_wev8.jsp" %>
<%@ include file="/systeminfo/RightClickMenuConent_wev8.jsp" %>
<%
RCMenu += "{"+SystemEnv.getHtmlLabelName(201,user.getLanguage())+",javascript:btn_cancle(),_self} " ;
RCMenuHeight += RCMenuHeightStep ;
%>
<%@ include file="/systeminfo/RightClickMenu_wev8.jsp" %>

<jsp:include page="/systeminfo/commonTabHead.jsp">
   <jsp:param name="mouldID" value="meeting"/>
   <jsp:param name="navName" value="<%=titlename %>"/>
</jsp:include>
<table id="topTitle" cellpadding="0" cellspacing="0">
	<tr>
		<td>
		</td>
		<td class="rightSearchSpan" style="text-align:right; ">
			<span title="<%=SystemEnv.getHtmlLabelName(23036,user.getLanguage()) %>" class="cornerMenu middle"></span>
		</td>
	</tr>
</table>
<div id="tabDiv" >
	<span id="hoverBtnSpan" class="hoverBtnSpan">
			
	</span>
</div>


<FORM id=weaver name=weaver action="" method=post >
<input type="hidden" name="sapBrowserType" id="sapBrowserType" value="<%=sapBrowserType%>">
		<TABLE class=Shadow>
		<%if(sapBrowserType.equals("ProjectBudget")){ 
		%>
		<tr>
			<td>
			<table>
				<tr>
					<td>WBS要素</td><td><input type="text" name="WBSElement" id="WBSElement"/></td>
					<td><input type="button" value="查询" name="searchbtn" onclick="search()" id="searchbtn"/></td>
					<td>（请输入关键词搜寻，或按查询键显示所有的WBS要素）</td>
				</tr>
			</table>
			</td>
		</tr>
		<%}else if(sapBrowserType.equals("PurchaseOrder")){%>
			<tr>
			<td>
			<table>
				<tr>
					<td>采购订单号</td>
					<td><input type="text" name="PurchaseOrder" id="PurchaseOrder"/></td>
					<td><input type="button" value="查询" name="searchbtn" onclick="search()" id="searchbtn"/></td>
				</tr>
			</table>
			</td>
		</tr>
		<%} %>
			<tr>
				<td valign="top" id="treeDemotd">
					<ul id="treeDemo" class="ztree" style="width: 100%;height: 460px;">请查询...</ul>
		 		</td>
			</tr>
		</table>
</FORM>
<div id="zDialog_div_bottom" class="zDialog_div_bottom">
    <wea:layout needImportDefaultJsAndCss="false">
		<wea:group context=""  attributes='{\"groupDisplay\":\"none\"}'>
			<wea:item type="toolbar">
				<input type="button" class="zd_btn_cancle" accessKey=T  id=btncancel value="T-<%=SystemEnv.getHtmlLabelName(201,user.getLanguage())%>" onclick="onClose()"></input>
			</wea:item>
		</wea:group>
	</wea:layout>
</div>
<jsp:include page="/systeminfo/commonTabFoot.jsp"></jsp:include>   

<script type="text/javascript">
var parentWin = null;
var dialog = null;
try{
	parentWin = parent.parent.getParentWindow(parent);
	dialog = parent.parent.getDialog(parent);
}catch(e){}

function onClose(){
	 if(dialog){
    	dialog.close()
    }else{
	    window.parent.close();
	}
}

var setting = {
	data: {
		simpleData: {
			enable: true
		}
	}
};


function search(){
	var sapBrowserType = jQuery("#sapBrowserType").val();
	var WBSElement = jQuery("#WBSElement").val();
	var PurchaseOrder = jQuery("#PurchaseOrder").val();
	jQuery.ajax({
	  type : "get", //提交方式 
	  url : "/formmode/apps/jiahe/getSapData.jsp",//路径 
	  data : { 
	      "saptype" : sapBrowserType,
	      "WBSElement":WBSElement,
	      "PurchaseOrder":PurchaseOrder
	  },//数据，这里使用的是Json格式进行传输 
	  datatype:"json",
	  success : function(result) {//返回数据根据结果进行相应的处理 
	      if (result) { 
		       if(result.length>0){
		    	   var zNodes = result;
		    	   $("#treeDemotd").html("<ul id=\"treeDemo\" class=\"ztree\" style=\"width: 100%;height: 460px;\"></ul>");
		    	   $("#treeDemo").html("");
		    	   $.fn.zTree.init($("#treeDemo"), setting, zNodes);
		       }else{
		    	   $("#treeDemo").html("查询不到数据");
		       }
	      } else { 
	    	  $("#treeDemo").html("系统异常请联系管理员");
	      } 
     }
    });
}

function btn_cancle(){
	onClose();
}

function onClear()
{
	returnjson = {id:"",name:""};
   if(dialog){
   	try{
   	 dialog.callback(returnjson);
   	}catch(e){}
   	
   	 try{
   	 dialog.close(returnjson)
   	 }catch(e){}
	}else{ 
  	  window.parent.returnValue  = returnjson;
  	  window.parent.close();
 	}
}

function selectData(str){
	var dataStr = str.split("_");
    var wbscode = dataStr[0];
	var planAmountInGlobalCurrency = dataStr[1];
	var actualAmountInGlobalCurrenc = dataStr[2];
	var keyong = dataStr[4];
	var WBSDescription = dataStr[5];
	var returnjson = {wbscode:wbscode,planAmountInGlobalCurrency:planAmountInGlobalCurrency,actualAmountInGlobalCurrenc:actualAmountInGlobalCurrenc,keyong:keyong,WBSDescription:WBSDescription};
	if(dialog){
   	 	try{
   	 		dialog.callback(returnjson);
   		}catch(e){}
   	
   		 try{
   			 dialog.close(returnjson)
   		 }catch(e){}
	}else{ 
  	  window.parent.returnValue  = returnjson;
  	  window.parent.close();
 	}
}
</script>
</HTML>
