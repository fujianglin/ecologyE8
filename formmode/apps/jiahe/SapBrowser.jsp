<%@page import="weaver.formmode.apps.jiahe.SapBrowserAction"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@page import="weaver.meeting.MeetingBrowser"%>
<%@ taglib uri="/WEB-INF/weaver.tld" prefix="wea"%>
<%@ include file="/systeminfo/init_wev8.jsp" %>
<%
 String sapBrowserType=Util.null2String(request.getParameter("sapBrowserType")); 
 int langid=user.getLanguage();
 if(sapBrowserType.equals("ProjectBudget")){
	 response.sendRedirect("/formmode/apps/jiahe/SapTreeBrowser.jsp?sapBrowserType="+sapBrowserType);
	 return ;
 }
%>

<HTML><HEAD>
<style type="text/css">
.tab_box {
 height:500px !important;
}
</style>
<LINK href="/css/Weaver_wev8.css" type=text/css rel=STYLESHEET>
<SCRIPT language="javascript" src="/js/weaver_wev8.js"></script>
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
//RCMenu += "{"+SystemEnv.getHtmlLabelName(826,user.getLanguage())+",javascript:doSubmit(),_self} " ;
//RCMenuHeight += RCMenuHeightStep ;

//RCMenu += "{"+SystemEnv.getHtmlLabelName(311,user.getLanguage())+",javascript:onClear(),_self} " ;
//RCMenuHeight += RCMenuHeightStep ;

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
				<td valign="top">
				<wea:layout type="2col" attributes="{'expandAllGroup':'true'}">
					<wea:group context="" attributes="{'groupDisplay':'none'}">
					<wea:item attributes="{'isTableList':'true'}">
					<TABLE CLASS="ListStyle" valign="top" cellspacing=1 style="z-index:99!important;">
					<%if(sapBrowserType.equals("ProjectBudget")){ %>
					      <colgroup>
					      <col width="20%">  
					      <col width="40%">
					      <col width="40%">
<%--					      <col width="15%">                         --%>
<%--					      <col width="15%">--%>
<%--					      <col width="15%">--%>
<%--					      <col width="15%">--%>
					      </colgroup>
					      <thead>
					     <TR class=HeaderForXtalbe>
						  	<th>年度</th>
						  	<th>WBS要素</th>
						  	<th>WBS要素描述</th>
						  	<th style='display:none'>计划预算</th>
						  	<th style='display:none'>实际使用预算</th>
						  	<th style='display:none'>冻结预算</th>
						  	<th style='display:none'>可用预算</th>
						 </TR>
						 </thead>
						 <%}else if(sapBrowserType.equals("PurchaseOrder")){ %>
						 <colgroup>
					      <col width="50%">  
					      <col width="50%">
					      </colgroup>
					      <thead>
					     <TR class=HeaderForXtalbe>
						  	<th>PO单号</th>
						  	<th>订单金额</th>
						 </TR>
						 </thead>
						 <%} %>
						 <tbody>
						 	
						 </tbody>
					</TABLE>
					</wea:item>
					</wea:group>
				</wea:layout>
		 		</td>
			</tr>
		</table>
</FORM>
<div id="zDialog_div_bottom" class="zDialog_div_bottom">
    <wea:layout needImportDefaultJsAndCss="false">
		<wea:group context=""  attributes='{\"groupDisplay\":\"none\"}'>
			<wea:item type="toolbar">
<%--		    	<input type="button" class="zd_btn_submit" accessKey=O  id=btnok value="O-<%=SystemEnv.getHtmlLabelName(826,user.getLanguage())%>" onclick="doSubmit()"></input>--%>
<%--				<input type="button" class="zd_btn_submit" accessKey=2  id=btnclear value="2-<%=SystemEnv.getHtmlLabelName(311,user.getLanguage())%>" onclick="onClear()"></input>--%>
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

function ChkTr(id){
	changeCheckboxStatus($('#checkbox_'+id),!$('#checkbox_'+id).attr("checked"));
}

function jsChkAll(obj){
	$("input[name='rptWeekDay']").each(function(){
		changeCheckboxStatus(this,obj.checked);
	}); 
}

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
	       var tbody = jQuery(".ListStyle tbody");
	       jQuery(tbody).empty();
	       if(result.length>0){
	    	   if(sapBrowserType=="ProjectBudget"){
			       for(var i=0;i<result.length;i++){
			    	   var data = result[i];
			    	   var trstr = "<tr onclick='selectData(this)'><td>"+
			    	   data.year+"</td><td name=wbscode>"+
			    	   data.wbscode+"</td><td>"+
			    	   data.wBSDescription+"</td><td style='display:none' name=planAmountInGlobalCurrency>"+
			    	   data.planAmountInGlobalCurrency+"</td><td style='display:none' name=actualAmountInGlobalCurrenc>"+
			    	   data.actualAmountInGlobalCurrenc+"</td>< style='display:none' td>"+
			    	   data.dongjie+"</td><td style='display:none' name=keyong>"+
			    	   data.keyong+"</td></tr>";
			       		tbody.append(trstr);
			       }
	    	   }else if(sapBrowserType=="PurchaseOrder"){
	    		   for(var i=0;i<result.length;i++){
			    	   var data = result[i];
			    	   var trstr = "<tr onclick='selectData(this)'><td name=PurchaseOrder>"+
			    	   data.PurchaseOrder+"</td><td name=ConditionAmount>"+
			    	   data.ConditionAmount+"</td></tr>";
			       		tbody.append(trstr);
			       }
	    	   }
	       }else{
	    	   if(sapBrowserType=="ProjectBudget"){
		    	   var trstr = "<tr><td style='text-align:center' colspan=4>查询不到数据</td></tr>";
		    	   tbody.append(trstr);
	    	   }else if(sapBrowserType=="PurchaseOrder"){
	    		   var trstr = "<tr><td style='text-align:center' colspan=2>查询不到数据</td></tr>";
		    	   tbody.append(trstr);
	    	   }
	       }
	      } else { 
	       
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

function selectData(trobj){
	var sapBrowserType = jQuery("#sapBrowserType").val();
   if(sapBrowserType=="ProjectBudget"){
	   var wbscode = jQuery(trobj).find("[name=wbscode]").text();
		var planAmountInGlobalCurrency = jQuery(trobj).find("[name=planAmountInGlobalCurrency]").text();
		var actualAmountInGlobalCurrenc = jQuery(trobj).find("[name=actualAmountInGlobalCurrenc]").text();
		var keyong = jQuery(trobj).find("[name=keyong]").text();
		var returnjson = {wbscode:wbscode,planAmountInGlobalCurrency:planAmountInGlobalCurrency,actualAmountInGlobalCurrenc:actualAmountInGlobalCurrenc,keyong:keyong};
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
   }else if(sapBrowserType=="PurchaseOrder"){
	   var PurchaseOrder = jQuery(trobj).find("[name=PurchaseOrder]").text();
		var ConditionAmount = jQuery(trobj).find("[name=ConditionAmount]").text();
		var returnjson = {PurchaseOrder:PurchaseOrder,ConditionAmount:ConditionAmount};
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
	
	
}

function doSubmit()
{	
	checkids="";
	checknames="";
	 
	$("input[name='rptWeekDay']:checked").each(function(){
		id=$(this).val();
		if(checkids==""){
			checkids=id;
			checknames=$('#checkname_'+id).text()
		}else{
			checkids+=","+id;
			checknames+=","+$('#checkname_'+id).text()
		}
		
	}); 
	
	 returnjson = {id:checkids,name:checknames};
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
