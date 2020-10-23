package msdev.jhswyy.sap.action;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class CalculateDataAction extends BaseBean implements Action{
	public String execute(RequestInfo request) {
		writeLog("进入嘉和采购流程计算基本单位和数量接口");
		RecordSet rs=new RecordSet();
		RecordSet rs1=new RecordSet();
		RecordSet rs2=new RecordSet();
		RecordSet rs3=new RecordSet();
		 BaseBean bb =new BaseBean();

		int formid=-request.getRequestManager().getFormid();
		 String requestid=request.getRequestid();
		 String Material="";//物料编号
		 double cgsl=0;//采购单位数量
	     double RequestedQuantity =0;//基本单位数量
	     String id="";//明细ID
	     String PurchaseOrderQuantityUnit=""; //订单单位
	     String BaseUnit="";//基本单位
	     int zz=10;  //行号
		//查询所有的明细数据    
		String mingSelectSql =" select dt1.*  " 
				+"   from  formtable_main_"+ formid+"_dt1 dt1 LEFT JOIN formtable_main_"+formid+" main ON  dt1.mainid=main.id "
				+"  where  main.requestid='" + requestid+"' ";
		rs.execute(mingSelectSql);
		bb.writeLog("查看明细sql语句"+mingSelectSql);

		while(rs.next()){			
			Material=rs.getString("Material");//物料编号
			cgsl=Util.getDoubleValue(rs.getString("cgsl"),0);//采购数量
		    id=rs.getString("id");//明细ID
		    int fenzi=0;//分子
		    int fenmu=0;//分母
		    //根据物料编号去单位转换表中进行数据转换
		    String dwSql="select * from uf_ProductUomCds  where Product='"+Material+"' and 	AlternativeUnit=PurchaseOrderQuantityUnit";
		    rs1.execute(dwSql);
		    //	PurchaseOrderQuantityUnit  订单单位
		    // 	BaseUnit 基本单位
		    if(rs1.next()){
		    	fenzi=rs1.getInt("QuantityNumerator");//分子
		    	fenmu=rs1.getInt("QuantityDenominator");//分母
		    	PurchaseOrderQuantityUnit=rs1.getString("PurchaseOrderQuantityUnit");//订单单位
		    	BaseUnit=rs1.getString("BaseUnit");//基本单位
		     	RequestedQuantity=cgsl*fenzi/fenmu;//基本单位数量
		     	//对计算的数据进行保留三位小数
		     	String sl=RequestedQuantity+"";
		     	RequestedQuantity=Util.round2(sl,3);
		    	//采购单位做一步转换，显示中文
		    	String selectBaseUnitSql="select 	jldwwb from 	uf_dwmcdz where dw='"+PurchaseOrderQuantityUnit+"'";
		    	rs2.execute(selectBaseUnitSql);
		    	  if(rs2.next()){
		    		  PurchaseOrderQuantityUnit=rs2.getString("jldwwb");//将订单单位转换为中文
		    	  }
		    	
		    }else{
		    	 RequestedQuantity=cgsl;//基本单位数量等于采购数量
		    	 String dwSqle="select * from uf_ProductUomCds  where Product='"+Material+"' ";
		    	 
		    	 rs2.execute(dwSqle);
		         if(rs2.next()){
		    	  BaseUnit=rs2.getString("BaseUnit");//基本单位
		    	  PurchaseOrderQuantityUnit=BaseUnit;
		    	//采购单位做一步转换，显示中文
		      	String selectBaseUnitSql="select 	jldwwb from 	uf_dwmcdz where dw='"+PurchaseOrderQuantityUnit+"'";
		      	rs3.execute(selectBaseUnitSql);
		      	  if(rs3.next()){
		      		  PurchaseOrderQuantityUnit=rs3.getString("jldwwb");//将采购单位转换为中文
		      	  }
		    	
		       }
		    }
		    bb.writeLog("采购流程接口获取物料编号是："+Material+"采购数量："+cgsl+"基本单位数量："+RequestedQuantity
		    		+"基本单位："+BaseUnit+"采购单位："+PurchaseOrderQuantityUnit);
		    //将查询到的值更新到流程表单中去
		    try {
		    String updateSql="update formtable_main_"+ formid+"_dt1 set RequestedQuantity='"+RequestedQuantity+"',hmxh="+zz+",cgdw='"+PurchaseOrderQuantityUnit+"', BaseUnit='"+BaseUnit+"' where id ="+id+"";
		    rs3.execute(updateSql);
		    writeLog("更新采购明细语句是："+updateSql);
		    }catch (Exception e) {
				writeLog("更新语句报错！");
				
				request.getRequestManager().setMessagecontent("基本单位没有维护，请让管理员维护基本单位表！");		
				return Action.FAILURE_AND_CONTINUE;
			}
		    //如果物料编号为空、采购数量为0
		    if("".equals(Material) || 0==cgsl){ 
		    	request.getRequestManager().setMessagecontent("流程数据不全，请检查流程数据！");		
				return Action.FAILURE_AND_CONTINUE;
            	
            }
		    zz=zz+10;
			
		}	
		return Action.SUCCESS; 		
	}

}
