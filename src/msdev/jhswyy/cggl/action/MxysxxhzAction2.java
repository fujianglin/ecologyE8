package msdev.jhswyy.cggl.action;

import java.util.HashMap;
import java.util.Map;

import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;


public class MxysxxhzAction2{
	/**
	 * 明细预算信息汇总
	 */
	public String execute() {
		BaseBean bb = new BaseBean();
		bb.writeLog("--- msdev/jhswyy/action/Mxysxxhz 开始汇总预算明细 ---");
		RecordSet ysxxRs = new RecordSet();
		RecordSet selectYsxxhzRs = new RecordSet();
		RecordSet updateYsxxhzRs = new RecordSet();
		RecordSet insertYsxxhzRs = new RecordSet();
		RecordSet selectYsxxYRs = new RecordSet();
		RecordSet fbRs = new RecordSet();
		Map<String,String> months = new HashMap<String, String>();
		months.put("0", "jan");
		months.put("1", "feb");
		months.put("2", "mar");
		months.put("3", "apr");
		months.put("4", "may");
		months.put("5", "jun");
		months.put("6", "jul");
		months.put("7", "aug");
		months.put("8", "sep");
		months.put("9", "oct");
		months.put("10", "nov");
		months.put("11", "dec");
		String selectYsxxSql = "select * from uf_budgetdetail";
		bb.writeLog("--- msdev/jhswyy/action/Mxysxxhz 预算明细所有数据Sql="+selectYsxxSql+" ---");
		selectYsxxYRs.execute(selectYsxxSql);
		String returnstatus = "0";
		while(selectYsxxYRs.next()){
			String billid = selectYsxxYRs.getString("id");
			String ysmxSql = "select * from uf_budgetdetail where id='"+billid+"'";
			bb.writeLog("--- msdev/jhswyy/action/Mxysxxhz 预算明细Sql="+ysmxSql+" ---");
			ysxxRs.execute(ysmxSql);
			if(ysxxRs.next()){
				String bdyear = ysxxRs.getString("bdyear");//年度
				String bdcostcenter = ysxxRs.getString("bdcostcenter");//成本中心
				String bdcoa = ysxxRs.getString("bdcoa");//预算科目
				String bcdetail = ysxxRs.getString("bcdetail");//预算明细
				String bdmanagedept = ysxxRs.getString("bdmanagedept");//归口管理部门
				String bdmonth = ysxxRs.getString("bdmonth");//预计购买期间
				double bdcount = Util.getDoubleValue(ysxxRs.getString("bdcount"), 0.0);//明细金额
				
				String ysxmhzSql = "select * from uf_budgetdetailinfo where bdyear='"+bdyear+"' and bdcostcenter='"+bdcostcenter+"' and bdcoa='"+bdcoa+"' and bcdetail='"+bcdetail+"' and bdmanagedept='"+bdmanagedept+"'";
				bb.writeLog("--- msdev/jhswyy/action/Mxysxxhz 预算明细汇总Sql="+ysxmhzSql+" ---");
				selectYsxxhzRs.execute(ysxmhzSql);
				if(selectYsxxhzRs.next()){
					//如果已存在汇总信息则修改
					String ysxxhzid = selectYsxxhzRs.getString("id");//预算汇总信息id
					double bdcostedbudget = Util.getDoubleValue(selectYsxxhzRs.getString("bdcostedbudget"), 0.0);//已使用金额
					String updateYsxxhzSql = "update uf_budgetdetailinfo set "+months.get(bdmonth)+"='"+bdcount+"' where id='"+ysxxhzid+"'";
					bb.writeLog("--- msdev/jhswyy/action/Mxysxxhz 修改预算明细汇总Sql="+updateYsxxhzSql+" ---");
					updateYsxxhzRs.execute(updateYsxxhzSql);
					String selectAfterUpdateYsxxhzSql = "select * from uf_budgetdetailinfo where id='"+ysxxhzid+"'";
					bb.writeLog("--- msdev/jhswyy/action/Mxysxxhz 查询预算明细汇总Sql="+selectAfterUpdateYsxxhzSql+" ---");
					updateYsxxhzRs.execute(selectAfterUpdateYsxxhzSql);
					double amount = 0.0;//预算汇总预算金额合计 
					if(updateYsxxhzRs.next()){
						double jan = Util.getDoubleValue(updateYsxxhzRs.getString("jan"), 0.0);
						double feb = Util.getDoubleValue(updateYsxxhzRs.getString("feb"), 0.0);
						double mar = Util.getDoubleValue(updateYsxxhzRs.getString("mar"), 0.0);
						double apr = Util.getDoubleValue(updateYsxxhzRs.getString("apr"), 0.0);
						double may = Util.getDoubleValue(updateYsxxhzRs.getString("may"), 0.0);
						double jun = Util.getDoubleValue(updateYsxxhzRs.getString("jun"), 0.0);
						double jul = Util.getDoubleValue(updateYsxxhzRs.getString("jul"), 0.0);
						double aug = Util.getDoubleValue(updateYsxxhzRs.getString("aug"), 0.0);
						double sep = Util.getDoubleValue(updateYsxxhzRs.getString("sep"), 0.0);
						double oct = Util.getDoubleValue(updateYsxxhzRs.getString("oct"), 0.0);
						double nov = Util.getDoubleValue(updateYsxxhzRs.getString("nov"), 0.0);
						double dec = Util.getDoubleValue(updateYsxxhzRs.getString("dec"), 0.0);
						amount = jan+feb+mar+apr+may+jun+jul+aug+sep+oct+nov+dec;
					}
					String updateYsxxhzJeSql = "update uf_budgetdetailinfo set amount='"+amount+"',bdavalibalebudget='"+(amount-bdcostedbudget)+"' where id='"+ysxxhzid+"'";
					bb.writeLog("--- msdev/jhswyy/action/Mxysxxhz 修改预算明细汇总金额Sql="+updateYsxxhzJeSql+" ---");
					updateYsxxhzRs.execute(updateYsxxhzJeSql);
				}else{
					//如果不存在汇总信息则添加
					String fbSql = "select subcompanyid1 from HrmDepartment where id='"+bdcostcenter+"'";
					bb.writeLog("--- msdev/jhswyy/action/Mxysxxhz 成本中心分部Sql="+fbSql+" ---");
					fbRs.execute(fbSql);
					String bdcom = "";//分部
					if(fbRs.next()){
						bdcom =fbRs.getString("subcompanyid1");
					}
					int formmodeid=42;
					String insertYsxxhzSql = "insert into uf_budgetdetailinfo (bdyear,bdcostcenter,bdcoa,bcdetail,"+months.get(bdmonth)+",amount,bdavalibalebudget,bdcom,bdmanagedept,bdfreezenbudget,bdcostedbudget,formmodeid) " +
																		"values('"+bdyear+"','"+bdcostcenter+"','"+bdcoa+"','"+bcdetail+"','"+bdcount+"','"+bdcount+"','"+bdcount+"','"+bdcom+"','"+bdmanagedept+"','0','0','"+formmodeid+"')";
					bb.writeLog("--- msdev/jhswyy/action/Mxysxxhz 新增预算明细汇总Sql="+insertYsxxhzSql+" ---");
					insertYsxxhzRs.execute(insertYsxxhzSql);
					
					//给权限
					int maxid= 0;
					
					insertYsxxhzRs.executeSql(" select MAX(ID) AS MAXID from uf_budgetdetailinfo ");
					if(insertYsxxhzRs.next()){
						maxid=insertYsxxhzRs.getInt("MAXID");
					}
					ModeRightInfo moderightinfo = new ModeRightInfo();
					moderightinfo.editModeDataShare(1, formmodeid, maxid);// 新建的时候添加共享
				}
			}
			returnstatus = "1";
		}
		
		return returnstatus;
	}

}
