/*
 *
 * Copyright (c) 2001-2016 泛微软件.
 * 泛微协同商务系统,版权所有.
 * 
 */
package weaver.interfaces.workflow.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import weaver.conn.RecordSet;
import weaver.fna.fnaVoucher.FnaCreateXml;
import weaver.fna.fnaVoucher.financesetting.*;
import weaver.fna.general.RecordSet4Action;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.soa.workflow.request.RequestInfo;
/**
 * 财务凭证接口推送action类
 * @author 无聊的ck
 *
 */
public class WorkflowToFinanceRunXml extends BaseBean implements Action{
	/**
	 * 值等于true时，在U8凭证推送只做数据校验不进行凭证推送
	 */
	String doCheckOnly4U8 = "";
	/**
	 * 值等于true时，在U8凭证推送只做数据校验不进行凭证推送
	 * @return the doCheckOnly4U8
	 */
	public String getDoCheckOnly4U8() {
		return doCheckOnly4U8;
	}
	/**
	 * 值等于true时，在U8凭证推送只做数据校验不进行凭证推送
	 * @param doCheckOnly4U8 the doCheckOnly4U8 to set
	 */
	public void setDoCheckOnly4U8(String doCheckOnly4U8) {
		this.doCheckOnly4U8 = doCheckOnly4U8;
	}
	/**
	 * 前缀
	 */
	String prefix = "";
	/**
	 * 前缀
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * 前缀
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * 凭证id
	 */
	String voucherXmlId = "0";
	/**
	 * 是否打印调试日期
	 */
	String isDebug = "0";
	/**
	 * 是否打印调试日期
	 * @return
	 */
	public String getIsDebug() {
		return isDebug;
	}
	/**
	 * 是否打印调试日期
	 * @param isDebug
	 */
	public void setIsDebug(String isDebug) {
		this.isDebug = isDebug;
	}
	/**
	 * 凭证id
	 * @return
	 */
	public String getVoucherXmlId() {
		return voucherXmlId;
	}
	/**
	 * 凭证id
	 * @param voucherXmlId
	 */
	public void setVoucherXmlId(String voucherXmlId) {
		this.voucherXmlId = voucherXmlId;
	}
	/**
	 * 回执信息写入主表字段字段名
	 */
	private String receiveFieldName = "";
	/**
	 * 回执信息写入主表字段字段名
	 * @return
	 */
	public String getReceiveFieldName() {
		return receiveFieldName;
	}
	/**
	 * 回执信息写入主表字段字段名
	 * @param receiveFieldName
	 */
	public void setReceiveFieldName(String receiveFieldName) {
		this.receiveFieldName = receiveFieldName;
	}
	
	/**
	 * 回执凭证号写入主表字段字段名
	 */
	private String contentFieldName = "";
	
	/**
	 * 回执凭证号写入主表字段字段名
	 * @return
	 */
	public String getContentFieldName() {
		return contentFieldName;
	}
	/**
	 * 回执凭证号写入主表字段字段名
	 * @param contentFieldName
	 */
	public void setContentFieldName(String contentFieldName) {
		this.contentFieldName = contentFieldName;
	}
	/**
	 * 流程action入口函数
	 * @param request 流程RequestInfo对象实例
	 * @return "1":成功；其他：失败；
	 */            
	public String execute(RequestInfo request) {
        this.writeLog("WorkflowToFinanceRunXml","do action on request:"+request.getRequestid());
        String requestid = request.getRequestid();
        
		try {
			RecordSet rs = new RecordSet();
			String workflowid = request.getWorkflowid();
			int currentUid = request.getRequestManager().getUserId();
			String language = "7";
			User user = request.getRequestManager().getUser();
			if(user!=null){
				language = user.getLanguage()+"";
			}
			
			String guid1 = UUID.randomUUID().toString();

			int fnaVoucherXmlId = Util.getIntValue(this.voucherXmlId, 0);
			if(fnaVoucherXmlId <= 0){
				String sql = "select * from fnaVoucherXml where workflowid = "+Util.getIntValue(workflowid)+" order by id";
				rs.executeSql(sql);
				if(rs.next()){
					fnaVoucherXmlId = Util.getIntValue(rs.getString("id"));
				}
			}

			String typename = "";
			String datasourceid = "";
			String interfacesAddress = "";
			String xmlEncoding = "";
			String easpackage = "";
			String sql = "select * from fnaVoucherXml where id = "+fnaVoucherXmlId;

			rs.executeSql(sql);
			if(rs.next()){
				typename = Util.null2String(rs.getString("typename")).trim();
				datasourceid = Util.null2String(rs.getString("datasourceid")).trim();
				interfacesAddress = Util.null2String(rs.getString("interfacesAddress")).trim();
				xmlEncoding = Util.null2String(rs.getString("xmlEncoding")).trim();
				easpackage = Util.null2String(rs.getString("easpackage")).trim();
			}
			
			if(fnaVoucherXmlId > 0){
				String brStr = "";//\n
				FnaCreateXml fnaCreateXml = new FnaCreateXml(guid1, currentUid, language, requestid, request);
				fnaCreateXml.setPrefix(Util.null2String(prefix).trim());
				fnaCreateXml.setPrintDebugLog(Util.getIntValue(this.isDebug, 0)==1);
				String xmlSend = Util.null2String(fnaCreateXml.createXmlStr(fnaVoucherXmlId, false, brStr, "", "WorkflowToFinanceRunXml生成XML模板"));
				
				String errorInfo = "";
				if("K3".equals(typename)){
					K3Handle handle = new K3Handle();
					errorInfo = handle.createVoucher(guid1, datasourceid);
				}else if("NC".equals(typename)){
					RecordSet4Action rsa = new RecordSet4Action(request);
					NCHandle handle = new NCHandle();
					errorInfo = handle.createVoucher(requestid, guid1, xmlSend, interfacesAddress, xmlEncoding, 
							rsa, request.getRequestManager().getFormid(), this.receiveFieldName,this.contentFieldName);
				}else if("EASwebservice".equals(typename)){
					EASWebServiceHandle easWebServiceHandle = new EASWebServiceHandle();
					errorInfo = easWebServiceHandle.createVoucher(requestid,xmlSend,easpackage,user);
				}else if("U8".equals(typename)){
					RecordSet4Action rsa = new RecordSet4Action(request);
					U8Handle handle = new U8Handle();
					errorInfo = handle.createVoucher(requestid, guid1, datasourceid, 
							rsa, request.getRequestManager().getFormid(), this.receiveFieldName,this.contentFieldName, this.doCheckOnly4U8);
				}else if("NC5".equals(typename)){
					RecordSet4Action rsa = new RecordSet4Action(request);
					NC5Handle handle = new NC5Handle();
					errorInfo = handle.createVoucher(requestid, guid1, xmlSend, interfacesAddress, xmlEncoding, 
							rsa, request.getRequestManager().getFormid(), this.receiveFieldName,this.contentFieldName);
				}else if("K3Cloud".equals(typename)){
					RecordSet4Action rsa = new RecordSet4Action(request);
					K3CloudHandle handle = new K3CloudHandle(interfacesAddress);
					errorInfo = handle.createVoucher(requestid, guid1, xmlSend, interfacesAddress, xmlEncoding, 
							rsa, request.getRequestManager().getFormid(), this.receiveFieldName,this.contentFieldName);
				}else if("U8OpenAPI".equals(typename)){
					RecordSet4Action rsa = new RecordSet4Action(request);
					xmlEncoding="utf-8";
					U8OpenAPIHandle handle = new U8OpenAPIHandle();
				
					errorInfo = handle.createVoucher(requestid, guid1, xmlSend, interfacesAddress, xmlEncoding,
							rsa, request.getRequestManager().getFormid(), this.receiveFieldName,this.contentFieldName);
				}else if("SAP".equals(typename)){
					RecordSet4Action rsa = new RecordSet4Action(request);
					SAPHandle3 handle = new SAPHandle3();
					errorInfo = handle.createVoucher(requestid, guid1, xmlSend, interfacesAddress, xmlEncoding,
							rsa, request.getRequestManager().getFormid(), this.receiveFieldName,this.contentFieldName,fnaVoucherXmlId);

				}
				
				if(!"".equals(errorInfo)){
					request.getRequestManager().setMessageid("11111"+requestid+"22222");
					request.getRequestManager().setMessagecontent("requestid："+requestid+"；"+errorInfo);
			        return Action.FAILURE_AND_CONTINUE;
				}
				
			}else{
				request.getRequestManager().setMessageid("11111"+requestid+"22222");
				request.getRequestManager().setMessagecontent("requestid："+requestid+"；action没有配置XML模板");
		        return Action.FAILURE_AND_CONTINUE;
			}
			
		} catch (Exception e) {
			this.writeLog(e);
			request.getRequestManager().setMessageid("11111"+requestid+"22222");
			request.getRequestManager().setMessagecontent("requestid："+requestid+"；action执行异常："+e.getMessage());
	        return Action.FAILURE_AND_CONTINUE;
		} finally {
		}
        return Action.SUCCESS;    
        
    }
	/**
	 * 将InputStream转换成字符串
	 * @param in InputStream
	 * @param encoding2 转换编码
	 * @return 转换后的字符串
	 * @throws Exception
	 */
	public String readInputStream(InputStream in, String encoding2) throws Exception {
		StringBuffer buffer = new StringBuffer();
		
		InputStreamReader isr = null;
		BufferedReader br = null;
		try{
			isr = new InputStreamReader(in, encoding2);
			br = new BufferedReader(isr);
			while (br.ready()) {
				String strLine = br.readLine();
				buffer.append(strLine);
			}
		}finally{
			try {
				if(br!=null){
					br.close();
				}
			} catch (IOException e) {
				new BaseBean().writeLog(e);
			}
			try {
				if(isr!=null){
					isr.close();
				}
			} catch (IOException e) {
				new BaseBean().writeLog(e);
			}
		}
		
		return buffer.toString();
	}

}
