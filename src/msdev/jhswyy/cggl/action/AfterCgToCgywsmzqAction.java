package msdev.jhswyy.cggl.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class AfterCgToCgywsmzqAction implements Action {

	/**
	 * 采购流程后Action 创建生命周期，带出字段：请购人  采购流程  采购流程发起日期  采购流程结束日期  PR号 行项目号
	 */
	public String execute(RequestInfo request) {
		BaseBean bb = new BaseBean();
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCgToCgywsmzqAction 开始生成采购业务生命周期表  --- ");
		RecordSet rs = new RecordSet();
		RecordSet insertRs = new RecordSet();
		String requestid=request.getRequestid();//流程requestid
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCgToCgywsmzqAction 生成采购业务生命周期表requestid="+requestid+"  --- ");
		int formid=-request.getRequestManager().getFormid();  //采购流程表单id
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCgToCgywsmzqAction 生成采购业务生命周期表formid="+formid+"  --- ");
		String qgr="";		//请购人
		String cglc="";		//采购流程
		String cglcfqrq="";	//采购流程发起日期
		String cglcjsrq="";	//采购流程结束日期
		String prh="";		//PR号
        String prhxmh="";	//PR行项目号
        
		cglc = requestid;
		String selectCgRequestSql = "select creater,createdate from workflow_requestbase where requestid='"+requestid+"'";
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCgToCgywsmzqAction 查询采购申请流程信息Sql="+selectCgRequestSql+"  --- ");
		rs.execute(selectCgRequestSql);
		if(rs.next()){
			qgr = rs.getString("creater");
			cglcfqrq = rs.getString("createdate");
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		cglcjsrq = sdf.format(date);
		String selectCgSql = "select * from formtable_main_"+formid+"_dt1 where mainid = (select id from formtable_main_"+formid+" where requestid='"+requestid+"')";
		bb.writeLog("--- msdev/jhswyy/cggl/AfterCgToCgywsmzqAction 查询采购申请信息Sql="+selectCgSql+"  --- ");
		rs.execute(selectCgSql);
		int formmodeid = 65;
		while(rs.next()){
			prh = rs.getString("prid");
			prhxmh = rs.getString("hmxh");
			String insertCgywsmzqSql = "insert into uf_cgywsmzq(qgr,cglc,cglcfqrq,cglcjsrq,prh,prhxmh,formmodeid) values('"+qgr+"','"+cglc+"','"+cglcfqrq+"','"+cglcjsrq+"','"+prh+"','"+prhxmh+"','"+formmodeid+"')";
			bb.writeLog("--- msdev/jhswyy/cggl/AfterCgToCgywsmzqAction 添加生命周期表Sql="+insertCgywsmzqSql+"  --- ");
			insertRs.execute(insertCgywsmzqSql);
			//给权限
			int maxid= 0;
			insertRs.executeSql(" select MAX(ID) AS MAXID from uf_cgywsmzq ");
			if(insertRs.next()){
				maxid=insertRs.getInt("MAXID");
			}
			ModeRightInfo moderightinfo = new ModeRightInfo();
			moderightinfo.editModeDataShare(1, formmodeid, maxid);// 新建的时候添加共享
		}
		return null;
	}

}
