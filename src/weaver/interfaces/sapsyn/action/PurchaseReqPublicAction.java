package weaver.interfaces.sapsyn.action;


import weaver.conn.RecordSet;
import weaver.file.Prop;
import weaver.fna.fnaVoucher.financesetting.InvokeHelperHTTP;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.interfaces.sapsyn.bean.PurchaseReqCreateRequest;
import weaver.interfaces.sapsyn.bean.PurchaseRequisitionCreateItem;
import weaver.interfaces.sapsyn.job.SupplierJob;
import weaver.interfaces.sapsyn.util.CommonUtil;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

import java.util.*;

/**
 * @author misetech
 * 采购申请-公共费用流程推送SAP生成PR
 */
public class PurchaseReqPublicAction implements Action {
    //日志打印对象
    private Logger logger = LoggerFactory.getLogger(SupplierJob.class);
    //请购申请数据接口请求url根路径  测试
    private String urlRoot = Util.null2String(Prop.getPropValue("sapsyn","sap.pr.url"));
  
    //用户名
    private String username = Util.null2String(Prop.getPropValue("sapsyn","sap.username"));
    //密码
    private String password = Util.null2String(Prop.getPropValue("sapsyn","sap.password"));
    //流程流转对象
    private RequestManager requestManager;

    @Override
    public String execute(RequestInfo requestInfo) {
    	
    	  logger.info("进入嘉和公共接口：");
        try{
            requestManager = requestInfo.getRequestManager();
            int mainid = 0;//主表id
            String sql = "";
            
            String 	shenQr="";//申请人id
            String RequisitionerName="";//申请人姓名
            String cglx="";//采购类型
            int jhts=0;//交货天数
            String mingxiid="";//明细ID
            String DeliveryDate="";//传到SAP中 交货天数
            String jlDanwei="";//计量单位
            //流程requestid
            int requestid = requestManager.getRequestid();
            //获取流程主表名
            String mainTableName = Util.null2String(requestManager.getBillTableName());
            //获取流程明细表名
            logger.info("主表名称："+mainTableName);
            String detailTableName = mainTableName + "_dt1";

            PurchaseReqCreateRequest purchaseReqCreateRequest = new PurchaseReqCreateRequest();
            ArrayList<PurchaseRequisitionCreateItem> purchaseRequisitionCreateItems = new ArrayList<PurchaseRequisitionCreateItem>();
            PurchaseRequisitionCreateItem purchaseRequisitionCreateItem;

            RecordSet recordSet = new RecordSet();
            RecordSet rs = new RecordSet();
        
            //查询主表 
            sql = "select * from " + mainTableName + " where requestid = " + requestid;
            String shenqinggs="";//申请公司
            recordSet.executeQuery(sql);
            if(recordSet.next()){
                purchaseReqCreateRequest.setPurchaseRequisition(Util.null2String(recordSet.getString("PurchaseRequisition")));//SAP采购申请单号
                purchaseReqCreateRequest.setPurchaseRequisitionType("NBS");//采购申请类型（固定）
                purchaseReqCreateRequest.setPurReqnDescription(Util.null2String(recordSet.getString("PurReqnDescription")));
                mainid = recordSet.getInt("id");
                shenqinggs=recordSet.getString("xqrgs");//申请公司
                shenQr=recordSet.getString("sqr");//申请人       
               
                
            }
              String selectSql="select lastname from hrmresource where id ="+shenQr+"";//查询申请下姓名
              rs.execute(selectSql);
              logger.info("获取申请人姓名sql："+selectSql);
              if(rs.next()){
            	  RequisitionerName=rs.getString("lastname");
              }
              logger.info("申请公司："+shenqinggs+"申请人姓名："+RequisitionerName);
            //查询明细表 采购明细信息
            sql = "select * from " + detailTableName + " where mainid = " + mainid;
            recordSet.executeQuery(sql);
            int zz=10;  
                //采购类型  BAZ
            while (recordSet.next()){
            	
                purchaseRequisitionCreateItem = new PurchaseRequisitionCreateItem();
                purchaseRequisitionCreateItem.setPurchaseRequisition(Util.null2String(purchaseReqCreateRequest.getPurchaseRequisition()));
                purchaseRequisitionCreateItem.setPurchaseRequisitionItem(String.valueOf(zz));//采购申请项目
                purchaseRequisitionCreateItem.setPurchaseRequisitionType(Util.null2String(purchaseReqCreateRequest.getPurchaseRequisitionType()));
                purchaseRequisitionCreateItem.setPurchaseRequisitionItemText(Util.null2String(recordSet.getString("PurchaseRequisitionItemText")));//物料短文本
                purchaseRequisitionCreateItem.setMaterial("4000001831");//	物料编号
                purchaseRequisitionCreateItem.setMaterialGroup(Util.null2String(recordSet.getString("MaterialGroup")));//物料组             
                purchaseRequisitionCreateItem.setPurchaseRequisitionPrice(Util.null2String(recordSet.getString("hxmzj")));//单价               
                purchaseRequisitionCreateItem.setFixedSupplier(Util.null2String(recordSet.getString("FixedSupplier")));//最终供应商
                purchaseRequisitionCreateItem.setCreatedByUser("S0019372014");//CreatedByUser
                purchaseRequisitionCreateItem.setPurReqnItemCurrency(Util.null2String(recordSet.getString("PurReqnItemCurrency")));//币种               
                purchaseRequisitionCreateItem.setAccountAssignmentCategory("K");//科目分配类别-SAP
                purchaseRequisitionCreateItem.setPurchasingGroup("103");//采购组
               
                purchaseRequisitionCreateItem.setSourceOfSupplyIsAssigned("true");//货源已分配标识
                purchaseRequisitionCreateItem.setPurReqnPriceQuantity("1");//价格数量单位
                purchaseRequisitionCreateItem.setPurchasingOrganization("3100");//采购组织
                
               // jlDanwei=Util.null2String(recordSet.getString("BaseUnit"));//计量单位
                if("1".equals(cglx) ){
               	 purchaseRequisitionCreateItem.setBaseUnit("EA");//基本计量单位
               }else{
               	purchaseRequisitionCreateItem.setBaseUnit("");//基本计量单位
               }
               
                 //申购类型是A资产类型
                
                if("5".equals(shenqinggs)){
                	  purchaseRequisitionCreateItem.setPlant("3100");//	工厂
                      purchaseRequisitionCreateItem.setCompanyCode("3100");//公司代码
                      logger.info("传的公司是上海3100");
                }
                else{
                	 purchaseRequisitionCreateItem.setPlant("4100");//	工厂
                     purchaseRequisitionCreateItem.setCompanyCode("4100");//公司代码
                     logger.info("传的公司是玉溪4100");
                }
               //判断交货日期如果小于0
                jhts=recordSet.getInt("DeliveryDate");
                if(jhts<0){
                	DeliveryDate="0";
                }
                else{
                	DeliveryDate=String.valueOf(jhts);
                }
                logger.info("获取到的交货天数:"+DeliveryDate);
                purchaseRequisitionCreateItem.setDeliveryDate(DeliveryDate);//交货天数  DeliveryDate
                purchaseRequisitionCreateItem.setRequirementTracking(Util.null2String(recordSet.getString("RequirementTracking")));//交货日期
                purchaseRequisitionCreateItem.setRequisitionerName(RequisitionerName);//申请人
              
                
                purchaseRequisitionCreateItem.setRequestedQuantity(Util.null2String(recordSet.getString("RequestedQuantity")));//申请数量
                purchaseRequisitionCreateItem.setSupplierMaterialNumber(Util.null2String(recordSet.getString("SupplierMaterialNumber")));//供应商货号
                
                purchaseRequisitionCreateItem.setCostCenter(Util.null2String(recordSet.getString("CostCenter")));//成本中心编号
                purchaseRequisitionCreateItem.setFixedAsset("");//固定资产编号（资产号）
                purchaseRequisitionCreateItem.setWBSElement("");//WBS
                purchaseRequisitionCreateItem.setGLAccount(Util.null2String(recordSet.getString("GLAccount")));//费用科目
                purchaseRequisitionCreateItems.add(purchaseRequisitionCreateItem);
                //将行项目号  	hmxh    更新到OA中
                mingxiid=Util.null2String(recordSet.getString("id"));
                String updateSalMin="update "+detailTableName+" set hmxh="+zz+" where id ="+mingxiid+"";
                rs.execute(updateSalMin);
                logger.info("更新明细行项目号："+updateSalMin);
              
                zz=zz+10;
            }
            purchaseReqCreateRequest.setPurchaseRequisitionCreateItems(purchaseRequisitionCreateItems);
            //拼接xml
            String xml = Util.null2String(CommonUtil.parseXML(purchaseReqCreateRequest));
            logger.info("xml："+xml);
            if(xml.equals("")){
                logger.error(">>>>>>>>>>>>>>>>>拼接xml出错");
                requestManager.setMessagecontent("拼接xml出错！");
                return Action.FAILURE_AND_CONTINUE;
            }else{
                Map<String,String> accountInfo = new HashMap<String, String>();
                accountInfo.put("uid",this.username);
                accountInfo.put("pwd",this.password);
                String jsonReceive = "";//响应 JSON
                jsonReceive = InvokeHelperHTTP.fun10sendVoucher(urlRoot,xml,accountInfo);
                logger.info("接口返回信息："+jsonReceive);
                int start = jsonReceive.indexOf("<PurchaseRequisition>");
                int end = jsonReceive.indexOf("</PurchaseRequisition>");
                //返回值
                String PurchaseRequisition = Util.null2String(jsonReceive.substring(start + 21,end));
                logger.info("返回值："+PurchaseRequisition);
                //回写 采购订单号 PurchaseRequisition
                recordSet.executeUpdate("update "+detailTableName+" set prid=? where mainid =?",PurchaseRequisition,mainid);
                logger.info(">>>>>>>>>>>>>>>>响应返回信息-PurchaseRequisition:"+PurchaseRequisition);
                logger.info(">>>>>>>>>>>>>>>>响应返回信息："+jsonReceive);
            }

        }catch(Exception e){
            logger.error(">>>>>>>>>>>>>>>>>请求接口报错："+e,e);
            requestManager.setMessagecontent("请求接口报错！");
            return Action.FAILURE_AND_CONTINUE;
        }
        return Action.SUCCESS;
    }
}
