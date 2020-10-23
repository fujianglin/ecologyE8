package msdev.jhswyy.cggl.action;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

/**
 * 流程退回时更改采购池中的数据状态
 * @author MISE
 *
 */
public class UpdateCgcZt implements Action{

	public String execute(RequestInfo request) {
		BaseBean bb = new BaseBean();
		bb.writeLog("开始修改采购池数据状态");
		String requestid=request.getRequestid();//流程requestid
		int formid=-request.getRequestManager().getFormid();  //采购订单流程表单id
		RecordSet rs = new RecordSet();
		RecordSet updateRs = new RecordSet();
		String selectCgcsjidsSql = "select cgcsjid from formtable_main_"+formid+"_dt1  where mainid=(select id from formtable_main_"+formid+" where requestid='"+requestid+"')";
		bb.writeLog("selectCgcsjidsSql="+selectCgcsjidsSql);
		rs.execute(selectCgcsjidsSql);
		while(rs.next()){
			String cgcsjid = rs.getString("cgcsjid");
			String updateZtSql = "update uf_cgc set lczt=0,hbhlc='' where id='"+cgcsjid+"'";
			bb.writeLog("updateZtSql="+updateZtSql);
			updateRs.execute(updateZtSql);
		}
		bb.writeLog("结束修改采购池数据状态");
		return Action.SUCCESS;
	}

}
