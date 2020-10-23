package msdev.util;

import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;

public class ModeinfoUtil {
	/**
     * 获取模块的表字段的fileid
     * @param modeID 模块id
     * @param fileName 字段名
     * @return 返回字段fileid
     */
    public static String getFormFileId(String modeID,String fileName){
    	RecordSet rs=new RecordSet();
    	String fileid = "";
    	String sql = "select id from workflow_billfield where billid = (select formid from modeinfo where id = "+modeID+") and fieldname = '"+fileName+"'";
    	rs.execute(sql);
    	if(rs.next()){
    		fileid = rs.getString("id");
    	}
        return fileid;
    }
    
    
    /**
     * 获取模块的表字段的下拉框值
     * @param modeID 模块id
     * @param fileName 字段名
     * @param 选择框的字段值
     * @return 返回下拉框值字符串
     */
    public static String getSelectValName(String modeID,String fileName,String selectVal){
    	RecordSet rs=new RecordSet();
    	String selectname = "_";
    	if(selectVal!=""){
    		String sql = "select selectvalue,selectname from workflow_SelectItem where selectvalue="+selectVal+" and fieldid="+ModeinfoUtil.getFormFileId(modeID, fileName);
        	rs.execute(sql);
        	if(rs.next()){
        		selectname = rs.getString("selectname");
        	}
    	}
    	
        return selectname;
    }
    
    
	/**
	 * 设置建模的数据权限：往建模表里面插入一条数据以后再使用，会设置好这条新插入数据的权限 
	 * @param tablename  表单名
	 * @param creator    创建人ID
	 * @param formmodeId 模块ID
	 */
	public void setModeRight(String tablename,int creator,int formmodeId){
		RecordSet rs = new RecordSet();
		int maxId = 0;
		String queryMaxid = "select MAX(ID) AS MAXID from "+tablename;
		rs.executeSql(queryMaxid);
		if(rs.next()){
			maxId=rs.getInt("MAXID");
		}
		ModeRightInfo moderightinfo = new ModeRightInfo();
		moderightinfo.editModeDataShare(creator, formmodeId, maxId);// 新建的时候添加共享
	}
}
