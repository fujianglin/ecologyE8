/*
 *
 * Copyright (c) 2001-2016 ��΢���.
 * ��΢Эͬ����ϵͳ,��Ȩ����.
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
 * ����ƾ֤�ӿ�����action��
 * @author ���ĵ�ck
 *
 */
public class WorkflowToFinanceRunXml extends BaseBean implements Action{
	/**
	 * ֵ����trueʱ����U8ƾ֤����ֻ������У�鲻����ƾ֤����
	 */
	String doCheckOnly4U8 = "";
	/**
	 * ֵ����trueʱ����U8ƾ֤����ֻ������У�鲻����ƾ֤����
	 * @return the doCheckOnly4U8
	 */
	public String getDoCheckOnly4U8() {
		return doCheckOnly4U8;
	}
	/**
	 * ֵ����trueʱ����U8ƾ֤����ֻ������У�鲻����ƾ֤����
	 * @param doCheckOnly4U8 the doCheckOnly4U8 to set
	 */
	public void setDoCheckOnly4U8(String doCheckOnly4U8) {
		this.doCheckOnly4U8 = doCheckOnly4U8;
	}
	/**
	 * ǰ׺
	 */
	String prefix = "";
	/**
	 * ǰ׺
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * ǰ׺
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * ƾ֤id
	 */
	String voucherXmlId = "0";
	/**
	 * �Ƿ��ӡ��������
	 */
	String isDebug = "0";
	/**
	 * �Ƿ��ӡ��������
	 * @return
	 */
	public String getIsDebug() {
		return isDebug;
	}
	/**
	 * �Ƿ��ӡ��������
	 * @param isDebug
	 */
	public void setIsDebug(String isDebug) {
		this.isDebug = isDebug;
	}
	/**
	 * ƾ֤id
	 * @return
	 */
	public String getVoucherXmlId() {
		return voucherXmlId;
	}
	/**
	 * ƾ֤id
	 * @param voucherXmlId
	 */
	public void setVoucherXmlId(String voucherXmlId) {
		this.voucherXmlId = voucherXmlId;
	}
	/**
	 * ��ִ��Ϣд�������ֶ��ֶ���
	 */
	private String receiveFieldName = "";
	/**
	 * ��ִ��Ϣд�������ֶ��ֶ���
	 * @return
	 */
	public String getReceiveFieldName() {
		return receiveFieldName;
	}
	/**
	 * ��ִ��Ϣд�������ֶ��ֶ���
	 * @param receiveFieldName
	 */
	public void setReceiveFieldName(String receiveFieldName) {
		this.receiveFieldName = receiveFieldName;
	}
	
	/**
	 * ��ִƾ֤��д�������ֶ��ֶ���
	 */
	private String contentFieldName = "";
	
	/**
	 * ��ִƾ֤��д�������ֶ��ֶ���
	 * @return
	 */
	public String getContentFieldName() {
		return contentFieldName;
	}
	/**
	 * ��ִƾ֤��д�������ֶ��ֶ���
	 * @param contentFieldName
	 */
	public void setContentFieldName(String contentFieldName) {
		this.contentFieldName = contentFieldName;
	}
	/**
	 * ����action��ں���
	 * @param request ����RequestInfo����ʵ��
	 * @return "1":�ɹ���������ʧ�ܣ�
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
				String xmlSend = Util.null2String(fnaCreateXml.createXmlStr(fnaVoucherXmlId, false, brStr, "", "WorkflowToFinanceRunXml����XMLģ��"));
				
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
					request.getRequestManager().setMessagecontent("requestid��"+requestid+"��"+errorInfo);
			        return Action.FAILURE_AND_CONTINUE;
				}
				
			}else{
				request.getRequestManager().setMessageid("11111"+requestid+"22222");
				request.getRequestManager().setMessagecontent("requestid��"+requestid+"��actionû������XMLģ��");
		        return Action.FAILURE_AND_CONTINUE;
			}
			
		} catch (Exception e) {
			this.writeLog(e);
			request.getRequestManager().setMessageid("11111"+requestid+"22222");
			request.getRequestManager().setMessagecontent("requestid��"+requestid+"��actionִ���쳣��"+e.getMessage());
	        return Action.FAILURE_AND_CONTINUE;
		} finally {
		}
        return Action.SUCCESS;    
        
    }
	/**
	 * ��InputStreamת�����ַ���
	 * @param in InputStream
	 * @param encoding2 ת������
	 * @return ת������ַ���
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
