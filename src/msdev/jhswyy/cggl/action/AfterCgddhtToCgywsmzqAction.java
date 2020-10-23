package msdev.jhswyy.cggl.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class AfterCgddhtToCgywsmzqAction implements Action{

	/**
	 * 采购订单合同后Action 修改生命周期表 带出字段：PO号  订单合同流程  订单合同流程发起日期  订单合同流程结束日期
	 */
	public String execute(RequestInfo request) {
		BaseBean bb = new BaseBean();
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		RecordSet updateRs = new RecordSet();
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCgddhtToCgywsmzqAction 开始修改采购业务生命周期表  --- ");
		String requestid=request.getRequestid();//流程requestid
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCgddhtToCgywsmzqAction 修改采购业务生命周期表requestid="+requestid+"  --- ");
		int formid=-request.getRequestManager().getFormid();  //采购订单流程表单id
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCgddhtToCgywsmzqAction 修改采购业务生命周期表formid="+formid+"  --- ");
		String poh="";			//PO号
		String pohxmh="";		//PO行项目号
		String ddhtlc="";		//订单合同流程
		String ddhtlcfqrq="";	//订单合同流程发起日期
		String ddhtlcjsrq="";	//订单合同流程结束日期
		
		ddhtlc = requestid;
		String selectDdhtRequestSql = "select creater,createdate from workflow_requestbase where requestid='"+requestid+"'";
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCg 查询订单合同流程信息Sql="+selectDdhtRequestSql+"  --- ");
		rs.execute(selectDdhtRequestSql);
		if(rs.next()){
			ddhtlcfqrq = rs.getString("createdate");
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ddhtlcjsrq = sdf.format(date);
		
		String selectDdhtSql = "select * from formtable_main_"+formid+"_dt2 where mainid = (select id from formtable_main_"+formid+" where requestid='"+requestid+"')";
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCgddhtToCgywsmzqAction 订单合同信息Sql="+selectDdhtSql+"  --- ");
		rs.execute(selectDdhtSql);
		while(rs.next()){
			String prh = rs.getString("PurchaseRequisition");
			String prhxmh = rs.getString("PurchaseRequisitionItem");
			poh = rs.getString("PurchaseOrder");
			pohxmh = rs.getString("PurchaseOrderItem");
			//这里根据PR号 去生命周期表中寻找 如果没有执行insert
			String selectSql="select * from uf_cgywsmzq where prh='"+prh+"'";
			rs1.execute(selectSql);
			if(rs1.next()){
				String updateCgywsmzqSql = "update uf_cgywsmzq set poh='"+poh+"',pohxmh='"+pohxmh+"',ddhtlc='"+ddhtlc+"',ddhtlcfqrq='"+ddhtlcfqrq+"',ddhtlcjsrq='"+ddhtlcjsrq+"' where prh='"+prh+"' and prhxmh='"+prhxmh+"'";
				bb.writeLog("--- msdev/jhswyy/cggl/AfterCgddhtToCgywsmzqAction 修改生命周期表Sql="+updateCgywsmzqSql+"  --- ");
				updateRs.execute(updateCgywsmzqSql);
			}else{
				String insertSql="insert into uf_cgywsmzq (poh,pohxmh,ddhtlc,ddhtlcfqrq,ddhtlcjsrq) values ('"+poh+"','"+pohxmh+"','"+ddhtlc+"','"+ddhtlcfqrq+"','"+ddhtlcjsrq+"')";
				bb.writeLog("--- msdev/jhswyy/cggl/AfterCgddhtToCgywsmzqAction 插入生命周期表Sql="+insertSql+"  --- ");
				updateRs.execute(insertSql);
			}
			
		}
		
		return null;
	}

}
