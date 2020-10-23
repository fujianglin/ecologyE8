package msdev.jhswyy.cggl.action;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class YsFsAction implements Action{
	/**
	 * 预算发生Action
	 */
	public String execute(RequestInfo request) {
		BaseBean bb = new BaseBean();
		bb.writeLog("--- msdev/jhswyy/cggl/YsdjAction 开始发生预算明细  --- ");
		String requestid=request.getRequestid();//流程requestid
		int formid=-request.getRequestManager().getFormid();  //采购订单流程表单id
		RecordSet cglcMXRs = new RecordSet();
		RecordSet ysMXRs = new RecordSet();
		RecordSet updateYsMXRs = new RecordSet();
		String cglcMXSql = "select * from formtable_main_"+formid+"_dt1  where mainid=(select id from formtable_main_"+formid+" where requestid='"+requestid+"')";
		bb.writeLog("--- msdev/jhswyy/cggl/YsdjAction 采购流程明细Sql="+cglcMXSql+"  --- ");
		cglcMXRs.execute(cglcMXSql);
		while(cglcMXRs.next()){
			double cgzjg = Util.getDoubleValue(cglcMXRs.getString("PurchaseRequisitionPrice"));//采购总价格（未税）
			String ysmx = cglcMXRs.getString("ysmx");
			String selectYsmxSql = "select * from uf_budgetdetailinfo where bcdetail='"+ysmx+"'";
			bb.writeLog("--- msdev/jhswyy/cggl/YsdjAction 预算明细Sql="+selectYsmxSql+"  --- ");
			ysMXRs.execute(selectYsmxSql);
			if(ysMXRs.next()){
				double bdavalibalebudget = Util.getDoubleValue(ysMXRs.getString("bdavalibalebudget"));//可用金额
				double bdcostedbudget = Util.getDoubleValue(ysMXRs.getString("bdcostedbudget"));   //已使用金额
				//double bdfreezenbudget = Util.getDoubleValue(ysMXRs.getString("bdfreezenbudget"));  //冻结中金额
				
				double afterKyje = bdavalibalebudget-cgzjg;//更改后可用金额
				double afterYsyje = bdcostedbudget+cgzjg;//更改后已使用金额
				//double afterDjzje = bdfreezenbudget-cgzjg;//更改后冻结中金额
				
				String updateYsmxSql = "update uf_budgetdetailinfo set bdavalibalebudget='"+afterKyje+"',bdcostedbudget='"+afterYsyje+"' where bcdetail='"+ysmx+"'";
				bb.writeLog("--- msdev/jhswyy/cggl/YsdjAction 修改预算明细可用/使用/发生金额Sql="+updateYsmxSql+"  --- ");
				updateYsMXRs.execute(updateYsmxSql);
			}
		}
		bb.writeLog("--- msdev/jhswyy/cggl/YsdjAction 结束发生预算明细  --- ");
		return null;
	}

}
