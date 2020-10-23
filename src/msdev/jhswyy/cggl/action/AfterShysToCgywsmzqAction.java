package msdev.jhswyy.cggl.action;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class AfterShysToCgywsmzqAction implements Action{

	/**
	 * 收货验收后Action 修改生命周期表 带出字段：收货验收流程  收货验收流程发起日期  收货验收流程结束日期
	 */
	public String execute(RequestInfo request) {
		BaseBean bb = new BaseBean();
		RecordSet rs = new RecordSet();
		RecordSet selectNumrs = new RecordSet();
		RecordSet updateRs = new RecordSet();
		RecordSet hxmyfjeRs = new RecordSet();
		bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 开始修改采购业务生命周期表  --- ");
		String requestid=request.getRequestid();//流程requestid
		bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 修改采购业务生命周期表requestid="+requestid+"  --- ");
		int formid=-request.getRequestManager().getFormid();  //收获验收流程表单id
		bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 修改采购业务生命周期表formid="+formid+"  --- ");
		String shyslc="";			//收货验收流程
		
		shyslc = requestid;
		String selectShysSql = "select * from formtable_main_"+formid+"_dt1 where mainid=(select id from formtable_main_"+formid+" where requestid='"+requestid+"')";
		bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 收获验收信息Sql="+selectShysSql+"  --- ");
		rs.execute(selectShysSql);
		while(rs.next()){
			String poh = rs.getString("poh");
			String pohxmh = rs.getString("pohxmh");
			double cgsl = Util.getDoubleValue(rs.getString("cgsl"), 0.0);//采购数量
			bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 采购数量="+cgsl+"  --- ");
			double dhsl = Util.getDoubleValue(rs.getString("dhsl"), 0.0);//到货数量
			bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 到货数量="+dhsl+"  --- ");
			
			//判断是到第几次验收
			String selectShysNumSql = "select * from uf_cgywsmzq where poh='"+poh+"' and pohxmh='"+pohxmh+"'";
			bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 判断是到第几次验收Sql="+selectShysNumSql+"  --- ");
			selectNumrs.execute(selectShysNumSql);
			int num = 0;
			double sssl = 0.0;
			if(selectNumrs.next()){
				sssl = Util.getDoubleValue(selectNumrs.getString("sssl"), 0.0)+dhsl;
				bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 上一次实收数量="+Util.getDoubleValue(selectNumrs.getString("sssl"), 0.0)+"  --- ");
				if(!"".equals(selectNumrs.getString("shyslc1"))){
					num=1;
					if(!"".equals(selectNumrs.getString("shyslc2"))){
						num=2;
						if(!"".equals(selectNumrs.getString("shyslc3"))){
							num=3;
						}
					}
				}
			}
			bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 本次是第几次验收="+(num+1)+"  --- ");
			//查询行项目应付金额
			String hxmyfjeSql = "select *　from　uf_prpotab where purchaseOrder='"+poh+"' and purchaseOrderitem='"+pohxmh+"'";
			bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 行项目应付金额Sql="+hxmyfjeSql+"  --- ");
			hxmyfjeRs.execute(hxmyfjeSql);
			double hxmyfje = 0.0;
			if(hxmyfjeRs.next()){
				double hxmyfjedj = Util.getDoubleValue(hxmyfjeRs.getString("NetPriceAmount"));
				double hxmsl = Util.getDoubleValue(hxmyfjeRs.getString("OrderQuantity"));
				DecimalFormat  def  = new DecimalFormat("#0.00");
				hxmyfje = Double.parseDouble(def.format(hxmyfjedj*hxmsl));
			}
			
			String updateCgywsmzqSql = "update uf_cgywsmzq set shyslc"+(num+1)+"='"+shyslc+"',yssl='"+cgsl+"',sssl='"+sssl+"',yfje='"+hxmyfje+"' where poh='"+poh+"' and pohxmh='"+pohxmh+"'";
			bb.writeLog("--- msdev/jhswyy/cggl/AfterShysToCgywsmzqAction 修改生命周期表Sql="+updateCgywsmzqSql+"  --- ");
			updateRs.execute(updateCgywsmzqSql);
		}
		return null;
	}

}
