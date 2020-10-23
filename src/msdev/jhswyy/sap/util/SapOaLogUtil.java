package msdev.jhswyy.sap.util;


import msdev.jhswyy.sap.bean.SapOaLog;
import msdev.util.ModeinfoUtil;
import weaver.conn.RecordSet;
import weaver.general.TimeUtil;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

/**
 * @author joshua.cheng
 * 日志记录Util
 */

public class SapOaLogUtil {
    //日志打印对象
    private static Logger logger = LoggerFactory.getLogger(SapOaLogUtil.class);
    
    
    
    public boolean insertLog(SapOaLog sapOaLog){
    	boolean returnVal = false;
    	
		int formmodeId = 1037;
		String modeTable = "uf_sapoalog"; //OASAP交互记录表
		String khrq = TimeUtil.getCurrentDateString();
		String timeStr = TimeUtil.getOnlyCurrentTimeString().substring(0, 5);
		RecordSet rs = new RecordSet();
		String type = sapOaLog.getType();//交互类型  0 供应商数据；1 物料数据；2 采购申请
		String inputdata = "";//输出数据
		String outputdata = "";//返回数据
		int request = 0;//相关流程
		String oplog = "";//操作记录
		String createdate = "";//创建日期
		String createtime = "";//创建时间
		String opstatus = "";//执行状态
		String errlog = "";//失败日志 0 进行中 1 执行成功 2 执行失败
		int creator = 1;//管理员 创建人
		
		String insertSql = " insert into "+modeTable+" (formmodeid,"
		+" modedatacreater,modedatacreatedate,modedatacreatetime,type,inputdata,outputdata,request,oplog,createdate,createtime,opstatus,errlog)"
		+" values ("+formmodeId+",'"+creator+"','"+khrq+"','"+timeStr +"','"
		+type+"','"+inputdata+"','"+outputdata+"','"+request+"','"+oplog+"','"+createdate+"','"+createtime+"','"+opstatus+"','"+errlog+"')";
		rs.executeSql(insertSql);
    	//数据设置权限
		ModeinfoUtil modeinfoUtil = new ModeinfoUtil();
		modeinfoUtil.setModeRight(modeTable, creator, formmodeId);
    	
    	return returnVal;
    }
    
    
    
    
    
    
    
    
}
