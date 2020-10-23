<%@page import="weaver.hrm.HrmUserVarify"%>
<%@page import="weaver.hrm.User"%>
<%@page import="weaver.conn.RecordSet"%>
<%@page import="weaver.general.Util"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%
User user = HrmUserVarify.getUser (request , response) ;
if(user==null){
	response.sendRedirect("/notice/noright.jsp") ;
	return ;
}

RecordSet rs = new RecordSet();

int requestid = Util.getIntValue(request.getParameter("requestid"),0);
int workflowid = Util.getIntValue(request.getParameter("workflowid"),0);

%>
<script language="javascript">
//预算年份
var d_bugetyearFieldId = "7568";
//成本中心
var d_cbzxFieldId = "6714";
//费用科目
var d_GLAccountFieldId = "7260";
//预算明细
var d_ysmxFieldId = "8976";
//成本中心可用预算	
var d_cbzxkyysFieldId = "8953";
//预算明细可用预算	
var d_ysmxkyysFieldId = "8973";

jQuery(document).ready(function(){
		//明细1处理
      jQuery("#indexnum0").bindPropertyChange(function () {
        bindfee(1);
      });
      bindfee(2);

});

function bindfee(value){
    var indexnum0 = 0;
    if(document.getElementById("indexnum0")){
		indexnum0 = document.getElementById("indexnum0").value * 1.0 - 1;
    }
    if(indexnum0>=0){
		if(value==1){
			//预算科目绑定事件
			
			  //初始化到达日期 到达日期=6159 开始日期=6156 6157=开始时间 6160=结束时间 
		    jQuery("#field"+d_GLAccountFieldId+"_"+indexnum0).bind("propertychange",function(){
				//显示成本中心预算
		    	showCbzxFna(indexnum0);
		    	
		    });
		}else if(value==2){
		    for(var i=0;i<=indexnum0;i++){
				  //初始化到达日期 到达日期=6159 开始日期=6156 6157=开始时间 6160=结束时间 
		        jQuery("#field"+d_GLAccountFieldId+"_"+i).attr("indexno",i);
				    jQuery("#field"+d_GLAccountFieldId+"_"+i).bind("propertychange",function(){
				    
						//显示成本中心预算
				    	showCbzxFna(jQuery("#field"+d_fymxField+"_"+i).attr("indexno"));
				    });
			  	}	
		  	}	
		}
	}
}

//显示成本中心可用预算	
function showCbzxFna(insertindex) {
	alert(insertindex);
}
//显示预算明细可用预算		
function showYsmxFna(insertindex) {
	alert(insertindex);
}


</script>