package msdev.jhswyy.sap.action;
import weaver.conn.RecordSet;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

import com.weaver.general.BaseBean;
public class DataCalculation implements Action {
	public String execute(RequestInfo requestInfo) {
		BaseBean bb = new BaseBean(); 
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();

		//RecordSet whileRs = new RecordSet();
		bb.writeLog("嘉和计算单位单价数据！");

		String requestId = requestInfo.getRequestid();// 获取流程的ID
		int formid = -requestInfo.getRequestManager().getFormid();// 获取流程对应的表名id

		String id ="";//主表id
		String mainid="";//明细id

		//先获取主表id
		String maiRrequestInfoSQL = "select * from formtable_main_"+formid+" where requestId ='" + requestId+"'";//查询当前的流程主表信息的SQL
		rs.execute(maiRrequestInfoSQL);
		if(rs.next()){
			id=rs.getString("id");			
		}
		bb.writeLog("执行查询采购批量流程主表信息的SQL:"+maiRrequestInfoSQL+"主表id："+id);	
		String jwlh="";//旧物料编号
		String Material="";//物料编号    
		int fenzi=0;//分子
		int fenmu=0;//分母
		double pricebak=0;//采购单位单价
		double hxmzj=0;//基本单位单价（未税）
		double RequestedQuantity=0;//基本单位数量
        double PurchaseRequisitionPrice=0;//采购总价格（未税）
		try{
			//明细表数据
			String mingXiSQL = "select * from formtable_main_"+formid+"_dt1"+ " where mainid ='"+id+"'";//查询当前明细SQL
			rs1.execute(mingXiSQL);
			bb.writeLog("明细sql:"+mingXiSQL);
			while(rs1.next()){	
				mainid=rs1.getString("id");//明细ID	
				jwlh=rs1.getString("jwlh");//旧物料号
				String MaterialSql="select Product from  uf_wlbh where ProductOldID='"+jwlh+"'";
				rs2.execute(MaterialSql);
				if(rs2.next()){
					Material=rs2.getString("Product");
					
				}
				bb.writeLog("查询新物料号SQL："+MaterialSql+"，根据旧物料号:"+jwlh+"查询到的新物料号："+Material);
				
				
				
				Material=rs1.getString("Material");//物料编号	
				hxmzj=rs1.getDouble("hxmzj");//基本单位单价
				PurchaseRequisitionPrice=hxmzj*RequestedQuantity;//采购总价格（未税）
				String dwSql="select * from uf_ProductUomCds  where Product='"+Material+"' and 	AlternativeUnit=PurchaseOrderQuantityUnit";
				rs.execute(dwSql);
				//	PurchaseOrderQuantityUnit  订单单位
				// 	BaseUnit 基本单位
				if(rs.next()){
					fenzi=rs.getInt("QuantityNumerator");//分子
					fenmu=rs.getInt("QuantityDenominator");//分母
					pricebak = hxmzj*fenzi/fenmu;
				}else{
					pricebak=hxmzj; 	
				}
				bb.writeLog("计算采购单位单价的分子："+fenzi+"计算采购单位单价的分母："+fenmu+"采购单位单价:"+pricebak+"采购总价格（未税）:"+PurchaseRequisitionPrice);
				//将查询到的数据更新到流程中
				String updateSql="update  formtable_main_"+formid+"_dt1"+ " set PurchaseRequisitionPrice='"+PurchaseRequisitionPrice+"',"
						+ "pricebak='"+pricebak+"' where id='"+mainid+"'";
				rs.execute(updateSql);
				bb.writeLog("将批量接口数据导入到明细表中:"+updateSql);
				bb.writeLog("接口执行完毕！");
			}
		}catch(Exception e){
			bb.writeLog(">>>>>>>>>>>>>>>>>请求接口报错："+e,e);
			requestInfo.getRequestManager().setMessagecontent("请求接口报错！");
			return Action.FAILURE_AND_CONTINUE;
		}

		return SUCCESS;//代码执行无异常接口执行成功

	}

}



