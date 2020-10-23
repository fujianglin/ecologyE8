package msdev.jhswyy.sap.action;

import java.util.HashMap;
import java.util.Map;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.general.DateUtil;
import weaver.hrm.resource.ResourceComInfo;
import weaver.workflow.webservices.WorkflowBaseInfo;
import weaver.workflow.webservices.WorkflowDetailTableInfo;
import weaver.workflow.webservices.WorkflowMainTableInfo;
import weaver.workflow.webservices.WorkflowRequestInfo;
import weaver.workflow.webservices.WorkflowRequestTableField;
import weaver.workflow.webservices.WorkflowRequestTableRecord;
import weaver.workflow.webservices.WorkflowServiceImpl;
import weaver.workflow.workflow.WorkflowComInfo;

import com.weaver.general.BaseBean;

public class TriggersProcess extends BaseBean{
	public String GeneratingProcess(String ids,int idss){
		RecordSet rs=new RecordSet();
		BaseBean bb=new BaseBean(); 
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();
		RecordSet rs3 = new RecordSet();
		bb.writeLog("采购池合并生成流程");
		//主表数据
		String  sqr="";//申请人
		String 	xqrgs="";//申请公司
		String 	lcbh="";//流程编号
	
		//明细表数据
		String 	line="";//行项目号
		
		String 	cost="";//成本中心
		String 	charge="";//费用明细
		String 	projectid="";//项目编号
		String 	year="";//预算年份
		String 	totalprice="";//单价
		String 	materialtext="";//物料短文本
		String  quantity="";//申请数量
		String 	basicunit="";//基本计量单位
		String 	prid="";//PR号
		String 	deliverydate="";//交货日期
		String 	comments="";//备注
		String  sgdh="";//申购单号
		
		String  cglx="";//申购类型
		
		String 	cgse="";//采购税额
		String 	bjfj="";//报价附件
		String 	gdzcbh="";//固定资产编号
		String 	FixedSupplier="";//最终供应商	
		String WBSElement="";//	WBS
		
		String ysmx="";//预算明细
		String cbzxkymx="";//成本中心可用预算
		String ysmxkyys="";//预算明细可用预算
		String wlmc="";//物料名称
		String gg="";//规格
		String cjhh="";//厂家货号
		String cjmc="";//厂家名称
		String wlbm="";//物料编码				
		String PurchaseRequisitionPrice="";//采购总价格（未税）
		String cksl="";//参考税率
		String danjia="";//单价
		String bz="";//询价信息
		String kcsl="";//存货数量
		String jyrq="";//期望到货期日
	
		String GLAccount="";//费用科目
		String[] idStrArray=ids.split(",");//项目合同清单列表ids
		//项目合同对应项目的编号  先对主表数据进行校验
		String appcompanyss="";//申请公司   
		String cbzxfzr="";//成本中心负责人
		String CostCenter="";//成本中心编号	
		String PurReqnItemCurrency="";//币种
		String jhts ="";//交货天数
		String Plant ="";//工厂
		String CompanyCode ="";//公司代码
		String MaterialGroup="";//物料组
		String FixedAsset="";//固定资产编号
		//2019年12月14日15:34:26 增加
		String gkglbm="";//归口管理部门
		String cgdw="";//采购单位
		String cgsl="";//采购数量
        String 	cgb="";//创建人的部门（采购部）
        String pricebak="";//采购单位单价（未税）
        int fenzi=0;//分子
	    int fenmu=0;//分母
	    double cgdwdj=0;//采购单位单价(未税)
	    double danjia1=0;//基本单位单价转换
	    
		String returnState="0";//返回值
		//2020年1月17日 增加字段 “价格数量单位”；
		String PurReqnPriceQuantity="";//价格数量单位
		double PurReqnPriceQuantity1=0;//价格数量单位转换
		//2020年2月6日增加字段 增加 明细表中申请人 ,流程编号，采购池模板中的数据ID
		String mxsqr=""; //明细申请人
		String cgcsjid="";//采购池中的数据id
		//2020年2月20日17 增加 amount 采购总金额（未税） 	cgjezj 采购总金额大写（未税）的计算
		String amount="";//采购总价格
		double amounts =0;//获得采购总价格 总值
		
	    for (int j = 0; j < idStrArray.length; j++) {
			
			bb.writeLog("获取表单id"+idStrArray[j]);

			  
			String xmhtSql="select * from uf_cgc where id='"+idStrArray[j]+"'";
			bb.writeLog("查看当前id的数据sql："+xmhtSql);
			rs.execute(xmhtSql);
			if(rs.next()){
				sqr=String.valueOf(idss);//申请人
				xqrgs=rs.getString("xqrgs");//申请公司
				lcbh=rs.getString("lcbh");//流程编号
				materialtext=rs.getString("materialtext");//物料短文本
				quantity=rs.getString("quantity");//申请数量	
				basicunit=rs.getString("basicunit");//	基本计量单位
				FixedSupplier=rs.getString("FixedSupplier");//最终供应商
				wlbm=rs.getString("wlbm");//物料编码
				double amount1 =Util.getDoubleValue(rs.getString("PurchaseRequisitionPrice"),0);//获得采购总价格
				amounts+=amount1;//循环累加
			}
			
		     if(j==0){
		    	 appcompanyss=xqrgs;//根据公司进行判断
		     }
			
			if(!xqrgs.equals(appcompanyss)){		
				returnState="0";
				 return returnState;
			}
			//urgencydegree  紧急程度 是紧急跳过循环
			bb.writeLog("判断数据：物料短文本："+materialtext+"申请数量:"+quantity+"基本单位："+basicunit+"申请人公司:"+xqrgs+"物料编码："+wlbm+"最终供应商："+FixedSupplier);
			if("".equals(materialtext) || "".equals(quantity) ||"".equals(basicunit) ||"".equals(xqrgs)||"".equals(wlbm)||"".equals(FixedSupplier)){
				
				returnState="2";
				 return returnState;
			}
			
			
		}
	    //根据流程创建人去找其部门  departmentid
	      String selectbumenSql="select departmentid from HrmResource where id="+idss+"";

			rs.execute(selectbumenSql);
	      if(rs.next()){
	    	  cgb=rs.getString("departmentid");
	      }
	      amount=amounts+"";//转换为字符
	      bb.writeLog("查看获取创建人的部门："+selectbumenSql+"获取到的部门："+cgb+"最终的采购总价格："+amount);
			//只取最后一次循环的值
			String wfId ="152";//流程id  申购流程2
			bb.writeLog("---嘉和开始创建流程 ---");
			
			
				WorkflowServiceImpl wf=new WorkflowServiceImpl();
				//流程编号，紧急程度，申请人，申请日期，申请部门，申请公司，采购组，采购组成员，备注，附件，上级领导，采购部门，财务部门，财务部门成员
				//主字段 (lcbh,jjcd,sqr,sqrq,xqbm,	xqrgs,caigouzu,cgr,bz,fj,manager,cgb,cwb,cwbcy)
				WorkflowRequestTableField[] mainField = new WorkflowRequestTableField[6]; // 字段信息	
				int i = 0;
				mainField[i] = new WorkflowRequestTableField();
				mainField[i].setFieldName("lcbh");//流程编号
				mainField[i].setFieldValue("");
				mainField[i].setView(true);
				mainField[i].setEdit(true);

				i++;
				mainField[i] = new WorkflowRequestTableField();
				mainField[i].setFieldName("xqrgs");//申请公司
				mainField[i].setFieldValue(xqrgs);
				mainField[i].setView(true);
				mainField[i].setEdit(true);

				i++;
				mainField[i] = new WorkflowRequestTableField();
				mainField[i].setFieldName("sqr");//申请人对应的是创建人
				mainField[i].setFieldValue(sqr);
				mainField[i].setView(true);
				mainField[i].setEdit(true);
				i++;
				mainField[i] = new WorkflowRequestTableField();
				mainField[i].setFieldName("cgb");//采购部
				mainField[i].setFieldValue(cgb);
				mainField[i].setView(true);
				mainField[i].setEdit(true);
				i++;
				mainField[i] = new WorkflowRequestTableField();
				mainField[i].setFieldName("amount");//采购总金额
				mainField[i].setFieldValue(amount);
				mainField[i].setView(true);
				mainField[i].setEdit(true);
				i++;
				mainField[i] = new WorkflowRequestTableField();
				mainField[i].setFieldName("cgjezj");//采购总金额大写
				mainField[i].setFieldValue(amount);
				mainField[i].setView(true);
				mainField[i].setEdit(true);
				

				WorkflowRequestTableRecord[] mainRecord = new WorkflowRequestTableRecord[1];// 主字段只有一行数据
				mainRecord[0] = new WorkflowRequestTableRecord();
				mainRecord[0].setWorkflowRequestTableFields(mainField);
				WorkflowMainTableInfo wmi = new WorkflowMainTableInfo();
				wmi.setRequestRecords(mainRecord);
				bb.writeLog("主表已创建完成！");	
				
				//明细字段
				WorkflowDetailTableInfo wdti[] = new WorkflowDetailTableInfo[1];//这里只插入一张明细表
				bb.writeLog("开始添加流程明细  ");				
				//明细表				
				int detailRecordNum1 = idStrArray.length;//根据勾选数据创建明细数据
				bb.writeLog("需要创建明细行"+detailRecordNum1+" ");
				WorkflowRequestTableRecord[] detailRecord1 = new WorkflowRequestTableRecord[detailRecordNum1];// 数据行数，假设添加detailRecordNum行明细数据
				bb.writeLog("申购流程2明细开始 条数="+detailRecordNum1);
				int mx1 = 0;  // uf_cgc_dt1
				
				for(int j = 0; j < idStrArray.length; j++){
					WorkflowRequestTableField[] detailField1 = new WorkflowRequestTableField[48]; // 每行44个字段
					
					
					String smpjSql="select * from uf_cgc where id="+idStrArray[j]+"";
					cgcsjid=""+idStrArray[j]+"";
				    bb.writeLog("获取到主表数据sql："+smpjSql+"对应采购池的id是"+cgcsjid);
					rs1.execute(smpjSql);			
					if(rs1.next()){
						cglx=rs1.getString("cglx");//采购类型
					    year=rs1.getString("year");//预算年份		
						cost=rs1.getString("cost");//成本中心
						charge=rs1.getString("charge");//费用明细
						GLAccount=rs1.getString("GLAccount");//费用科目
						projectid=rs1.getString("projectid");//项目编号	
						WBSElement=rs1.getString("WBSElement");//WBS
						ysmx=rs1.getString("ysmx");//预算明细
						totalprice=rs1.getString("totalprice");//行项目总价
						cbzxkymx=rs1.getString("cbzxkymx");//	成本中心可用预算
						materialtext=rs1.getString("materialtext");//物料短文本
						ysmxkyys=rs1.getString("ysmxkyys");//预算明细可用预算
						quantity=rs1.getString("quantity");//申请数量	
						wlmc=rs1.getString("wlmc");//物料名称
						basicunit=rs1.getString("basicunit");//	基本计量单位
						gg=rs1.getString("gg");//	规格
						cjhh=rs1.getString("cjhh");//	厂家货号
						deliverydate=rs1.getString("deliverydate");//交货日期
						cjmc=rs1.getString("cjmc");//厂家名称
						prid=rs1.getString("prid");//PR号				
						line=rs1.getString("line");//行项目号	
						sgdh=rs1.getString("lcbh");//申购单号
						wlbm=rs1.getString("wlbm");//物料编码
					  
					    bjfj=rs1.getString("bjfj");//报价附件
					    gdzcbh=rs1.getString("gdzcbh");//固定资产编号	
						FixedSupplier=rs1.getString("FixedSupplier");//最终供应商
						PurchaseRequisitionPrice=rs1.getString("PurchaseRequisitionPrice");//采购总价格（未税）
						cksl=rs1.getString("cksl");//参考税率
						danjia=rs1.getString("danjia");//单价
						bz=rs1.getString("bz");//询价信息
						kcsl=rs1.getString("kcsl");//存货数量
						jyrq=rs1.getString("jyrq");//期望到货期日				
						cbzxfzr=rs1.getString("cbzxfzr");//成本中心负责人
						CostCenter=rs1.getString("CostCenter");//成本中心编号
						
						 PurReqnItemCurrency=rs1.getString("PurReqnItemCurrency");//币种
						 jhts =rs1.getString("jhts");//交货天数
						 Plant =rs1.getString("Plant");//工厂
						 CompanyCode =rs1.getString("CompanyCode");//公司代码
						 MaterialGroup=rs1.getString("MaterialGroup");//物料组
						 FixedAsset=rs1.getString("FixedAsset");//固定资产编号
						
						 gkglbm=rs1.getString("gkglbm");//归口管理部门
						 cgdw=rs1.getString("cgdw");//采购单位
						 cgsl=rs1.getString("cgsl");//采购数量
						 PurReqnPriceQuantity=rs1.getString("PurReqnPriceQuantity");//价格数量单位
						 mxsqr=rs1.getString("sqr");//明细申请人
						 
						    fenzi=0;//分子
						    fenmu=0;//分母
						    cgdwdj=0;//采购单位单价(备份)
						    //根据物料编号去单位转换表中进行数据转换
						    String dwSql="select * from uf_ProductUomCds  where Product='"+wlbm+"' and 	AlternativeUnit=PurchaseOrderQuantityUnit";
						    rs.execute(dwSql);
						    bb.writeLog("根据物料获取单位Sql："+dwSql);
						    danjia1= Double.valueOf(danjia);
						    PurReqnPriceQuantity1= Double.valueOf(PurReqnPriceQuantity);
						    //	PurchaseOrderQuantityUnit  订单单位
						    // 	BaseUnit 基本单位
						    if(rs.next()){
						    	
						    	fenzi=rs.getInt("QuantityNumerator");//分子
						    	fenmu=rs.getInt("QuantityDenominator");//分母
					
						    		cgdwdj = danjia1/PurReqnPriceQuantity1*fenzi/fenmu;
						    		bb.writeLog("分子："+fenzi+"分母："+fenmu+"采购单位单价:"+cgdwdj);
			    	
						    }else{
						    	
						    	cgdwdj=danjia1/PurReqnPriceQuantity1;
						    	
						    }
						    pricebak=String.valueOf(cgdwdj);
								 
						 
						bb.writeLog("行项目号："+line+"费用明细:"+charge+"成本中心:"+cost+"预算年份："+year+"行项目总价:"+totalprice
								+"物料短文本:"+materialtext+"申请数量:"+quantity+"PR号:"+prid+"申购单号："+sgdh+"物料编码:"+wlbm
								+"基本计量单位:"+basicunit+"交货日期:"+deliverydate+"交货日期:"+deliverydate+"备注："+comments
								+"申购类型:"+cglx+"采购税额:"+cgse+"报价附件:"+bjfj+"固定资产编号:"+"最终供应商:"+FixedSupplier
								+"固定资产编号:"+gdzcbh+"采购总价格（未税）:"+PurchaseRequisitionPrice+"参考税率:"+cksl+"单价:"+danjia
								+"询价信息:"+bz+"存货数量:"+kcsl+"期望到货期日:"+jyrq+"成本中心负责人："+cbzxfzr+"成本中心编号:"+CostCenter
								+"币种:"+PurReqnItemCurrency+"交货天数:"+jhts+"工厂:"+Plant+"公司代码:"+CompanyCode+"物料组:"+MaterialGroup
								+"固定资产编号:"+FixedAsset+"归口管理部门："+gkglbm+"采购单位:"+cgdw+"采购数量:"+cgsl+"采购单位单价（未税）:"+pricebak
								+"价格数量单位:"+PurReqnPriceQuantity+"采购池中的申请人："+mxsqr);
					}
								
					
				    i = 0;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("sqlx");//采购类型
					detailField1[i].setFieldValue(cglx);		
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//1
					
					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("bugetyear");//预算年份
					detailField1[i].setFieldValue(year);
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					

					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("cbzx");//成本中心
					detailField1[i].setFieldValue(cost);
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
				
					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("feiyongmx");//费用明细
					detailField1[i].setFieldValue(charge);
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);

					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("GLAccount");
					detailField1[i].setFieldValue(GLAccount);//费用科目
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//5
					
					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("cbzxfzr");
					detailField1[i].setFieldValue(cbzxfzr);//成本中心负责人
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					
					
					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("xmbh");
					detailField1[i].setFieldValue(projectid);//项目编号
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					
					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("WBSElement");
					detailField1[i].setFieldValue(WBSElement);//WBS
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);

					i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("ysmx");
					detailField1[i].setFieldValue(ysmx);//预算明细
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);

					i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("totalprice");
					detailField1[i].setFieldValue(totalprice);//行项目总价
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//10


					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("cbzxky");
					detailField1[i].setFieldValue(cbzxkymx);//成本中心可用预算
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);

					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("PurchaseRequisitionItemText");//物料短文本
					detailField1[i].setFieldValue(materialtext);
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);

					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("ysmxkyys");
					detailField1[i].setFieldValue(ysmxkyys);//预算明细可用预算
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);

					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("RequestedQuantity");
					detailField1[i].setFieldValue(quantity);//申请数量
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					 i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("wlmc");
					detailField1[i].setFieldValue(wlmc);//物料名称
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//15
				
			
					i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("BaseUnit");
					detailField1[i].setFieldValue(basicunit);//基本计量单位
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("gg");
					detailField1[i].setFieldValue(gg);//规格
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("cjhh");
					detailField1[i].setFieldValue(cjhh);//厂家货号
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("RequirementTracking");
					detailField1[i].setFieldValue(deliverydate);//交货日期
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("cjmc");
					detailField1[i].setFieldValue(cjmc);//厂家名称
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//20
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("prid");
					detailField1[i].setFieldValue(prid);//PR号
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("hmxh");
					detailField1[i].setFieldValue(line);//行项目号
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("Material");
					detailField1[i].setFieldValue(wlbm);//物料编码
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("bjfj");
					detailField1[i].setFieldValue(bjfj);//报价附件
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("gdzcbh");
					detailField1[i].setFieldValue(gdzcbh);//固定资产编号
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("FixedSupplier");
					detailField1[i].setFieldValue(FixedSupplier);//最终供应商
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("PurchaseRequisitionPrice");
					detailField1[i].setFieldValue(PurchaseRequisitionPrice);//采购总价格（未税）
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//27
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("cksl");
					detailField1[i].setFieldValue(cksl);//参考税率
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("hxmzj");
					detailField1[i].setFieldValue(danjia);//单价
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("bz");
					detailField1[i].setFieldValue(bz);//询价信息
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("kcsl");
					detailField1[i].setFieldValue(kcsl);//存货数量
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("jyrq");
					detailField1[i].setFieldValue(jyrq);//期望到货期日
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("CostCenter");
					detailField1[i].setFieldValue(CostCenter);//成本中心编号
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("PurReqnItemCurrency");
					detailField1[i].setFieldValue(PurReqnItemCurrency);//币种
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("DeliveryDate");
					detailField1[i].setFieldValue(jhts);//交货天数
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("Plant");
					detailField1[i].setFieldValue(Plant);//工厂
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("CompanyCode");
					detailField1[i].setFieldValue(CompanyCode);//公司代码
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("MaterialGroup");
					detailField1[i].setFieldValue(MaterialGroup);//物料组
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);				
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("FixedAsset");
					detailField1[i].setFieldValue(FixedAsset);//固定资产编号
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("gkglbm");
					detailField1[i].setFieldValue(gkglbm);//归口管理部门
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("cgdw");
					detailField1[i].setFieldValue(cgdw);//采购单位
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("cgsl");
					detailField1[i].setFieldValue(cgsl);//采购数量
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("pricebak");
					detailField1[i].setFieldValue(pricebak);//采购单位单价（未税）
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//44
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("PurReqnPriceQuantity");
					detailField1[i].setFieldValue(PurReqnPriceQuantity);//价格数量单位
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//45
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("sqrname");
					detailField1[i].setFieldValue(mxsqr);//明细申请人
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//46
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("cgcsjid");
					detailField1[i].setFieldValue(cgcsjid);//价格数量单位
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//47
					i++;	
					detailField1[i] = new WorkflowRequestTableField();
					detailField1[i].setFieldName("sgdh");
					detailField1[i].setFieldValue(sgdh);//申购编号
					detailField1[i].setView(true);
					detailField1[i].setEdit(true);//48
					 
					detailRecord1[mx1] = new WorkflowRequestTableRecord();
					detailRecord1[mx1].setWorkflowRequestTableFields(detailField1);

					mx1++;
					//每生成一次明细，就要更新一次sql
					String UpdateSql="update uf_cgc set lczt='1' where id ="+idStrArray[j]+"";
					bb.writeLog("更新采购池sql："+UpdateSql);
					rs2.execute(UpdateSql);		
				}
				bb.writeLog("明细创建完成！");
				wdti[0] = new WorkflowDetailTableInfo();
				wdti[0].setWorkflowRequestTableRecords(detailRecord1);
		
				WorkflowBaseInfo wbi = new WorkflowBaseInfo();
				wbi.setWorkflowId(wfId);// workflowid 
				String UserID=String.valueOf(idss);
				
				//读取标题
				DateUtil du=new DateUtil();
				  ResourceComInfo resourceComInfo=null;
				  WorkflowComInfo workflowCominfo=null;
				  try {
				   resourceComInfo = new ResourceComInfo();
				   workflowCominfo=new WorkflowComInfo();
				  } catch (Exception e1) {
				   e1.printStackTrace();
				  }
				  String lcbt=du.getWFTitle(wfId, idss+"", resourceComInfo.getLastname(idss+""));
				  if("".equals(lcbt)){
				   lcbt=workflowCominfo.getWorkflowname(wfId);
				  }
				
				  bb.writeLog("查看创建人："+UserID+"获取到的流程标题："+lcbt);
				WorkflowRequestInfo wri = new WorkflowRequestInfo();// 流程基本信息
			    wri.setCreatorId(UserID);
			    wri.setRequestName(lcbt);// 流程标题
				
				wri.setRequestLevel("0");// 0 正常，1重要，2紧急
				wri.setWorkflowBaseInfo(wbi);
				wri.setWorkflowMainTableInfo(wmi);// 添加主表字段数据
				wri.setWorkflowDetailTableInfos(wdti);// 添加明细表字段数据
				
				
				String requestid = "";
				
				requestid=wf.doCreateWorkflowRequest(wri,idss);
				String updateSql="update formtable_main_146 set cgb ='"+cgb+"' where requestid='"+requestid+"'";
				rs2.execute(updateSql);	
				bb.writeLog("更新部门："+updateSql);
				//更新每条从采购池里面出来的流程
				  for (int j = 0; j < idStrArray.length; j++) {

					String upRcqlcSql="update uf_cgc set hbhlc='"+requestid+"' where id ="+idStrArray[j]+"";
					rs3.execute(upRcqlcSql);	
					 bb.writeLog("更新采购池中的合并后流程SQL"+upRcqlcSql+"从采购池中创建合并流程的数据有:"+idStrArray[j]);
				  }
			//更新评价状态
			
			returnState="1"; 
			
		
		
		return returnState;
	}

}
