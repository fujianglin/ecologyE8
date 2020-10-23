package msdev.jhswyy.cggl.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class AfterDgfkToCgywsmzqAction implements Action{

	/** 
	 * 对公付款后Action 修改生命周期表 带出字段：	对公付款流程  对公付款流程发起日期  对公付款流程结束日期
	 */
	public String execute(RequestInfo request) {
		BaseBean bb = new BaseBean();
		RecordSet rs = new RecordSet();
		RecordSet selectCgywsmzqAboutShyslcRs = new RecordSet();
		RecordSet selectNumrs = new RecordSet();
		RecordSet updateRs = new RecordSet();
		RecordSet shyslchxmRs = new RecordSet();
		bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 开始修改采购业务生命周期表  --- ");
		String requestid=request.getRequestid();//流程requestid
		bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 修改采购业务生命周期表requestid="+requestid+"  --- ");
		int formid=-request.getRequestManager().getFormid();  //对公付款流程表单id
		bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 修改采购业务生命周期表formid="+formid+"  --- ");
		String dgfklc="";			//对公付款流程
		
		dgfklc = requestid;
		String selectDgfkSql = "select * from formtable_main_"+formid+" where requestid='"+requestid+"'";
		bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 对公付款信息Sql="+selectDgfkSql+"  --- ");
		rs.execute(selectDgfkSql);
		if(rs.next()){
			String shyslc = rs.getString("shyslc");
			String shyslchxmSql = "select * from  formtable_main_38_dt1 where mainid =( select id from formtable_main_38 where requestid='"+shyslc+"')";
			bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 对公付款信息关联收货验收流程下的行项目Sql"+shyslchxmSql+"  --- ");
			shyslchxmRs.execute(shyslchxmSql);
			while(shyslchxmRs.next()){
				String poh = shyslchxmRs.getString("poh");
				String pohxmh = shyslchxmRs.getString("pohxmh");
				double cgsl = Util.getDoubleValue(shyslchxmRs.getString("cgsl"), 0.0);//采购数量
				bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 采购数量="+cgsl+"  --- ");
				double dhsl = Util.getDoubleValue(shyslchxmRs.getString("dhsl"), 0.0);//到货数量
				bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 到货数量="+dhsl+"  --- ");
				
				String selectCgywsmzqSql = "select * from uf_cgywsmzq where poh='"+poh+"' and pohxmh='"+pohxmh+"'";
				bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 生命周期下明细SQL="+selectCgywsmzqSql+"  --- ");
				selectCgywsmzqAboutShyslcRs.execute(selectCgywsmzqSql);
				if(selectCgywsmzqAboutShyslcRs.next()){
					double yfje = Util.getDoubleValue(selectCgywsmzqAboutShyslcRs.getString("yfje"));
					//判断是到第几次验收
					String selectDgfkNumSql = "select * from uf_cgywsmzq where poh='"+poh+"' and pohxmh='"+pohxmh+"'";
					bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 判断是到第几次验收Sql="+selectDgfkNumSql+"  --- ");
					selectNumrs.execute(selectDgfkNumSql);
					int num = 0;
					double sfje = 0.0;
					sfje = Util.getDoubleValue(selectCgywsmzqAboutShyslcRs.getString("sfje"), 0.0)+(dhsl/cgsl*yfje);
					bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 上一次实付金额="+Util.getDoubleValue(selectCgywsmzqAboutShyslcRs.getString("sfje"), 0.0)+"  --- ");
					if(selectNumrs.next()){
						if(!"".equals(selectNumrs.getString("dgfklc1"))){
							num=1;
							if(!"".equals(selectNumrs.getString("dgfklc2"))){
								num=2;
								if(!"".equals(selectNumrs.getString("dgfklc3"))){
									num=3;
								}
							}
						}
						
						bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 本次是第几次对公付款="+(num+1)+"  --- ");
						String updateCgywsmzqSql = "update uf_cgywsmzq set dgfklc"+(num+1)+"='"+dgfklc+"',sfje='"+sfje+"' where poh='"+poh+"' and pohxmh='"+pohxmh+"'";
						bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 修改生命周期表Sql="+updateCgywsmzqSql+"  --- ");
						updateRs.execute(updateCgywsmzqSql);	
					}
					else{
						String insertCgywsmzqSql = "insert into uf_cgywsmzq (dgfklc"+(num+1)+",sfje) values('"+dgfklc+"','"+sfje+"')";
						bb.writeLog("--- msdev/jhswyy/cggl/AfterDgfkToCgywsmzqAction 插入生命周期表Sql="+insertCgywsmzqSql+"  --- ");
						updateRs.execute(insertCgywsmzqSql);	
					}
								
				}
				
				
			}

		}
		
		
		return null;
	}

}
