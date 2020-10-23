package msdev.jhswyy.sap.action;
import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;



import com.weaver.general.BaseBean;
public class BatchImport implements Action {
	public String execute(RequestInfo requestInfo) {
		BaseBean bb = new BaseBean(); 
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();
		
		//RecordSet whileRs = new RecordSet();
		bb.writeLog("进入嘉和，根据项目明细带出数据！");
		
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
		String ysmx="";//预算明细
		String gkglbm="";//归口管理部门
		String cbzx="";//成本中心
		String CostCenter="";//成本中心编号
		String sqlx ="";//采购类型 （0,1,2）
		String GLAccount="";//费用科目
		String MaterialGroup="";//物料组
	    String cbzxfzr ="";//成本中心负责人
	    String Material="";//物料编号
	    String 	jwlh="";//旧物料编号
	    String ProductName="";//物料名称
	    String  PurchaseOrderQuantityUnit="";  //订单单位
	    String	BaseUnit ="";//基本单位
	    int fenzi=0;//分子
	    int fenmu=0;//分母
	    double caigousls=0;//Double.parseDouble(caigouslse)
	    double RequestedQuantity =0;//基本单位数量
	    try{
				//明细表数据
				String mingXiSQL = "select * from formtable_main_"+formid+"_dt1"+ " where mainid ='"+id+"'";//查询当前明细SQL
				rs1.execute(mingXiSQL);
				bb.writeLog("明细sql:"+mingXiSQL);
				while(rs1.next()){	
					mainid=rs1.getString("id");//明细ID
					jwlh=rs1.getString("jwlh");//旧物料号
					ysmx=rs1.getString("ysmx");//预算明细
					String MaterialSql="select Product from  uf_wlbh where ProductOldID='"+jwlh+"'";
					rs2.execute(MaterialSql);
					if(rs2.next()){
						Material=rs2.getString("Product");
						
					}
					bb.writeLog("查询新物料号SQL："+MaterialSql+"，根据旧物料号:"+jwlh+"查询到的新物料号："+Material);
					
					caigousls=rs1.getDouble("cgsl");//采购单位数量
					
					//根据预算明细去找其他数据
					String selectGkglbmSql="select bdmanagedept from uf_budgetdetailinfo where bcdetail='"+ysmx+"'";//查找归口管理部门
					rs2.execute(selectGkglbmSql);
					
					if(rs2.next()){
						gkglbm=rs2.getString("bdmanagedept");//归口管理部门
						
					}
					//成本中心
					String cbzxSql="select * from uf_feetype where id='"+ysmx+"'";//成本中心
					rs2.execute(cbzxSql);
					if(rs2.next()){
						cbzx=rs2.getString("bdcostcenter");//成本中心
						GLAccount=rs2.getString("bdcoa");//费用科目
						sqlx=rs2.getString("cglx");//采购类型
						
					}
					//物料名称
					String ProductNameSql="select ProductName from 	uf_wlbh where  	Product='"+Material+"'";
					rs2.execute(ProductNameSql);
					if(rs2.next()){
						
						ProductName=rs2.getString("ProductName");
					}
					//根据采购类型去找物料组
					String cglxSql="select MaterialGroup from uf_sgwhb where sqlx='"+sqlx+"'";
					rs2.execute(cglxSql);
					if(rs2.next()){
						MaterialGroup=rs2.getString("MaterialGroup");//物料组
						
					}
					//找成本中心编号
					String CostCenterSql="select SAPcbzxx from HRMDEPARTMENTDEFINED where deptid='"+cbzx+"'";//成本中心编号
					rs2.execute(CostCenterSql);
					if(rs2.next()){
						CostCenter=rs2.getString("SAPcbzxx");//成本中心编号
						
					}
					//成本中心负责人
					String  cbzxfzrSql="select bmfzr from 	Matrixtable_2 where id='"+cbzx+"'";
					rs2.execute(cbzxfzrSql);
					if(rs2.next()){
						cbzxfzr=rs2.getString("bmfzr");//成本中心负责人		
					}
					//根据物料编号 去找采购单位、采购单位数量，基本单位和基本单位数量  
					//根据物料编号去单位转换表中进行数据转换
				    String dwSql="select * from uf_ProductUomCds  where Product='"+Material+"' and 	AlternativeUnit=PurchaseOrderQuantityUnit";
				    rs.execute(dwSql);
				    //	PurchaseOrderQuantityUnit  订单单位
				    // 	BaseUnit 基本单位
				    if(rs.next()){
				    	PurchaseOrderQuantityUnit=rs.getString("PurchaseOrderQuantityUnit");//订单单位
				    	BaseUnit=rs.getString("BaseUnit");//基本单位
				    	fenzi=rs.getInt("QuantityNumerator");//分子
				    	fenmu=rs.getInt("QuantityDenominator");//分母
				    	RequestedQuantity=caigousls*fenzi/fenmu;//基本单位数量
					   
				    }else{
				    	 String dwSqle="select * from uf_ProductUomCds  where Product='"+Material+"' ";
				    	 
				    	 rs.execute(dwSqle);
				         if(rs.next()){
				         
				    	  BaseUnit=rs.getString("BaseUnit");//基本单位
				    	  PurchaseOrderQuantityUnit=BaseUnit;//采购单位
				    	  RequestedQuantity=caigousls;//基本单位数量
				      }
				    }
					bb.writeLog("查看归口部门sql:"+selectGkglbmSql+",成本中心sql："+cbzxSql+",找成本中心编号sql:"+CostCenterSql
							+",成本中心负责人sql:"+cbzxfzrSql);
					bb.writeLog("预算明细："+ysmx+"物料编号："+Material+"成本中心负责人："+cbzxfzr+"物料组:"+MaterialGroup+"成本中心："+cbzx
							+"归口管理部门:"+gkglbm+"成本中心编号:"+CostCenter+"采购类型："+sqlx+"费用科目："+GLAccount+"分子："+fenzi+"分母："+fenmu
							+"基本单位："+BaseUnit+"采购单位："+PurchaseOrderQuantityUnit+"基本单位数量："+RequestedQuantity+"采购数量："+caigousls
							+"物料名称："+ProductName);
					
					//将查询到的数据更新到流程中
					String updateSql="update  formtable_main_"+formid+"_dt1"+ " set cbzxfzr='"+cbzxfzr+"',"
					+ "MaterialGroup='"+MaterialGroup+"',cbzx='"+cbzx+"',gkglbm='"+gkglbm+"',CostCenter='"+CostCenter+"',"
					+ "GLAccount='"+GLAccount+"',sqlx='"+sqlx+"' ,BaseUnit='"+BaseUnit+"',RequestedQuantity='"+RequestedQuantity+"',PurchaseRequisitionItemText='"+ProductName+"' where id='"+mainid+"'";
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


