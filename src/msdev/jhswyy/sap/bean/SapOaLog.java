package msdev.jhswyy.sap.bean;

public class SapOaLog {
	
	private String type = "";//交互类型  0 供应商数据；1 物料数据；2 采购申请

	private String inputdata = "";//输出数据

	private String outputdata = "";//返回数据

	private int request = 0;//相关流程

	private String oplog = "";//操作记录

	private String createdate = "";//创建日期

	private String createtime = "";//创建时间

	private String opstatus = "";//执行状态   0 进行中 1 执行成功 2 执行失败

	private String errlog = "";//失败日志 

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getErrlog() {
		return errlog;
	}

	public void setErrlog(String errlog) {
		this.errlog = errlog;
	}

	public String getInputdata() {
		return inputdata;
	}

	public void setInputdata(String inputdata) {
		this.inputdata = inputdata;
	}

	public String getOplog() {
		return oplog;
	}

	public void setOplog(String oplog) {
		this.oplog = oplog;
	}

	public String getOpstatus() {
		return opstatus;
	}

	public void setOpstatus(String opstatus) {
		this.opstatus = opstatus;
	}

	public String getOutputdata() {
		return outputdata;
	}

	public void setOutputdata(String outputdata) {
		this.outputdata = outputdata;
	}

	public int getRequest() {
		return request;
	}

	public void setRequest(int request) {
		this.request = request;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
