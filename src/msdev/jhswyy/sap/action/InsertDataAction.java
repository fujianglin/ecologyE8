package msdev.jhswyy.sap.action;
import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;


import com.weaver.general.BaseBean;
public class InsertDataAction implements Action {
	public String execute(RequestInfo requestInfo) {
		BaseBean bb = new BaseBean(); 
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();
		//RecordSet whileRs = new RecordSet();
		bb.writeLog("进入嘉和，将明细所有数据存入到台账表单中！");
		String requestId = requestInfo.getRequestid();// 获取流程的ID
		int formid = -requestInfo.getRequestManager().getFormid();// 获取流程对应的表名id
		//主表字段
		String id ="";//主表id
		String jjcd="";//紧急程度
		String  sqr="";//申请人
		String 	xqrgs="";//申请公司
		String 	lcbh="";//流程编号
		//明细字段
		String 	line="";//行项目号
		//String  type="";//申购类型
		String 	cost="";//成本中心
		String 	charge="";//费用明细
		String 	projectid="";//项目编号
		String 	year="";//预算年份
		//String 	totalprice="";//行项目总价
		String 	materialtext="";//物料短文本
		String  quantity="";//申请数量
		String 	basicunit="";//单位
		String 	prid="";//PR号
		String 	deliverydate="";//交货日期
		String 	comments="";//备注
		String  gg="";//规格
		String  cjhh="";//厂家货号
		String  cjmc="";//厂家名称
		
		String cksl ="";//	参考税率
		//String  danjia="";//单价
		String  sqlx="";//申购类型
		String 	cgjghs="";//采购总价格（未税）
		String 	cgse="";//采购税额
		String 	bjfj="";//报价附件
		String 	gdzcbh="";//固定资产编号
		//String 	FixedSupplier="";//最终供应商
		String  chengbzx="";//成本中心负责人
		String GLAccount ="";//费用科目
		String Material="";//物料编号
		String wlmc	="";//物料名称
		String ysmx ="";//预算明细
		String PurReqnItemCurrency="";//币种
		String jhts ="";//交货天数
		String CostCenter ="";//成本中心编号
		String Plant ="";//工厂
		String CompanyCode ="";//公司代码
		String MaterialGroup="";//物料组
		String FixedAsset="";//固定资产编号
		//2019年12月14日10:49:46增加字段
		
		String WBSElement ="";//WBS
		Double cbzxkymx =0.0;//成本中心可用预算
		Double ysmxkyys =0.0;//预算明细可用预算
		Double PurchaseRequisitionPrice=0.0;//采购总价格（未税）
		String bz="";//询价信息
		int kcsl =0;//存货数量
		
		String gkglbm="";//归口管理部门
		String cgdw="";//采购单位
		Double cgsl=0.0;//采购数量
		Double pricebak=0.0;//采购单位单价（未税）
         	
        
		//先获取主表id
		String maiRrequestInfoSQL = "select * from formtable_main_"+formid+" where requestId ='" + requestId+"'";//查询当前的流程主表信息的SQL
		rs.execute(maiRrequestInfoSQL);
		bb.writeLog("执行查询流程主表信息的SQL:"+maiRrequestInfoSQL);
	
		if(rs.next()){
			id=rs.getString("id");
			jjcd=rs.getString("jjcd");
			sqr=rs.getString("sqr");
			xqrgs=rs.getString("xqrgs");//申请公司
			lcbh=rs.getString("lcbh");//流程编号
		}
		bb.writeLog("查看数据是否需要进采购池:"+jjcd+"申请人："+sqr);
		if(!"0".equals(jjcd)){
		//明细表数据
		String mingXiSQL = "select * from formtable_main_"+formid+"_dt1"+ " where mainid ='"+id+"'";//查询当前明细SQL
		rs1.execute(mingXiSQL);
		bb.writeLog("明细sql:"+mingXiSQL);
		while(rs1.next()){	
			sqlx=rs1.getString("sqlx");//申购类型
			cost=rs1.getString("cbzx");//成本中心
			gdzcbh=rs1.getString("gdzcbh");//固定资产编号
			GLAccount=rs1.getString("GLAccount");//费用科目
			chengbzx=rs1.getString("cbzxfzr");//成本中心负责人
			Material=rs1.getString("Material");//物料编号
			wlmc=rs1.getString("wlmc");//物料名称
			materialtext=rs1.getString("PurchaseRequisitionItemText");//物料短文本	
			quantity=rs1.getString("RequestedQuantity");//数量	
			gg=rs1.getString("gg");//规格
			cjhh=rs1.getString("cjhh");//厂家货号
			cjmc=rs1.getString("cjmc");//厂家名称
			basicunit=rs1.getString("BaseUnit");//	基本计量单位
			year=rs1.getString("bugetyear");//预算年份
			ysmx=rs1.getString("ysmx");//预算明细
			deliverydate=rs1.getString("RequirementTracking");//交货日期
			PurReqnItemCurrency=rs1.getString("PurReqnItemCurrency");//币种	

			jhts =rs1.getString("DeliveryDate");//交货天数
			CostCenter =rs1.getString("CostCenter");//成本中心编号
			Plant =rs1.getString("Plant");//工厂
			CompanyCode =rs1.getString("CompanyCode");//公司代码
			MaterialGroup=rs1.getString("MaterialGroup");//物料组
			FixedAsset=rs1.getString("FixedAsset");//固定资产编号
			//2019年12月14日10:53:10 新增
			
			charge=rs1.getString("feiyongmx");//费用明细
			line=rs1.getString("hmxh");//项目行号
			cksl=rs1.getString("cksl");//参考税率
			bz=rs1.getString("bz");//询价信息
			kcsl=Util.getIntValue(rs1.getString("kcsl"),0);//库存数量
			WBSElement=rs1.getString("WBSElement");//WBS	
		    cbzxkymx =Util.getDoubleValue(rs1.getString("cbzxkyys"),0);//成本中心可用预算（参考值）
			ysmxkyys =Util.getDoubleValue(rs1.getString("ysmxkyys"),0);//预算明细可用预算
		    PurchaseRequisitionPrice=Util.getDoubleValue(rs1.getString("PurchaseRequisitionPrice"),0);//采购总价格（未税）
		    gkglbm=rs1.getString("gkglbm");//归口管理部门
			cgdw=rs1.getString("cgdw");//采购单位
			cgsl=Util.getDoubleValue(rs1.getString("cgsl"),0);//采购数量
			bjfj=rs1.getString("bjfj");//报价附件
			pricebak=Util.getDoubleValue(rs1.getString("pricebak"),0);//采购单位单价（未税）
			bb.writeLog("规格:"+gg+"厂家货号:"+cjhh+"厂家名称:"+cjmc+"参考税率:"+cksl+"行项目号："+line+"费用明细:"+charge+"成本中心:"+cost+"预算年份："+year+
					"物料短文本:"+materialtext+"数量:"+quantity+"prid号:"+prid+"申购类型:"+sqlx+"采购总价格（未税）:"
					+cgjghs+"采购税额:"+cgse+"报价附件:"+bjfj+"固定资产编号:"+gdzcbh+"预算明细："+ysmx
					+"基本计量单位:"+basicunit+"交货日期:"+deliverydate+"交货日期:"+deliverydate+"备注："+comments+"成本中心负责人："+chengbzx
					+"交货天数:"+jhts+"成本中心编号:"+"工厂:"+Plant+"公司代码:"+CompanyCode+"物料组:"+MaterialGroup+"固定资产编号:"+FixedAsset
					+"询价信息:"+bz+"库存数量:"+kcsl+"WBS:"+WBSElement+"成本中心可用预算:"+cbzxkymx+"预算明细可用预算:"+ysmxkyys
					+"归口管理部门:"+gkglbm+"采购单位:"+cgdw+"采购数量:"+cgsl+"采购单位单价（未税）："+pricebak+"入池前流程："+requestId);
	   //将查询到的数据存入到采购池
			String InsertSql ="Insert into  uf_cgc(formmodeid,lczt,sqr,xqrgs,lcbh,cglx,cost,gdzcbh,GLAccount,cbzxfzr,wlbm,wlmc,"
					+ "materialtext,quantity,gg,cjhh,cjmc,basicunit,year,ysmx,deliverydate,PurReqnItemCurrency,"
					+ "jhts,CostCenter,Plant,CompanyCode,MaterialGroup,FixedAsset,charge,line,cksl,bz,kcsl,WBSElement,"
					+ "cbzxkymx,ysmxkyys,PurchaseRequisitionPrice,gkglbm,cgdw,cgsl,bjfj,pricebak,rcqlc)  "
					+ "values(80,'0','"+sqr+"','"+xqrgs+"','"+lcbh+"','"+sqlx+"','"+cost+"','"+gdzcbh+"','"+GLAccount+"','"+chengbzx+"','"+Material+"','"+wlmc+"',"
					+ "'"+materialtext+"','"+quantity+"','"+gg+"','"+cjhh+"','"+cjmc+"', '"+basicunit+"','"+year+"', '" + ysmx+"',"
					+ "'"+deliverydate+"','"+PurReqnItemCurrency+"','"+jhts+"','"+CostCenter+"','"+Plant+"','"+CompanyCode+"','"+MaterialGroup+"','"+FixedAsset+"',"
					+ "'"+charge+"','"+line+"','"+cksl+"','"+bz+"','"+kcsl+"','"+WBSElement+"','"+cbzxkymx+"','"+ysmxkyys+"','"+PurchaseRequisitionPrice+"',"
					+ "'"+gkglbm+"','"+cgdw+"','"+cgsl+"','"+bjfj+"','"+pricebak+"','"+requestId+"')";
		
			rs.execute(InsertSql);
			bb.writeLog("将数据存到表单中:"+InsertSql);
			//赋权
			int maxid= 0;
			int formmodeid=80;
			rs.execute(" select MAX(ID) AS MAXID from uf_cgc ");
			if(rs.next()){
				maxid=rs.getInt("MAXID");
			}
			ModeRightInfo moderightinfo = new ModeRightInfo();
			moderightinfo.editModeDataShare(1, formmodeid, maxid);
		}
		//UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
		
		bb.writeLog("嘉和，将明细所有数据存入到台账表单中！完成");
		}
		return SUCCESS;//代码执行无异常接口执行成功


	}

}

