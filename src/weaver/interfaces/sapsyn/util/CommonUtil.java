package weaver.interfaces.sapsyn.util;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import weaver.general.Util;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import msdev.jhswyy.sap.util.HTTPUtils;
import weaver.integration.util.XMLUtil;
import weaver.interfaces.sapsyn.bean.*;
import weaver.interfaces.sapsyn.job.ProductJob;

import java.util.*;

public class CommonUtil {
    //日志打印对象
    private static Logger logger = LoggerFactory.getLogger(ProductJob.class);
    /**
     * 获取数据
     * xml数据结构：
     * <feed>
     *     <entry>
     *         <content>
     *             <properties>
     *                 <prop><prop/>
     **/
    public ArrayList<Object> getData(Map attrs,String urlRoot,String username,String password){
        ArrayList<Object> productList = new ArrayList<Object>();
        try{
            String data = HTTPUtils.doGetWithBasicAuth(urlRoot,username,password);
            logger.info(">>>>>>>>>>>>>>>-getData.data:"+data);
            Document document = XMLUtil.parseXMLStr(data);
            //获取根节点
            Element rootNode = XMLUtil.getRootNode(document);
            List<Element> entryList = XMLUtil.getChildrenNodesByName(rootNode,"entry");
            Element currentElement = null;
            Object dataObj = null;

            //遍历数据存储节点list
            for (Element e:entryList){
                currentElement = e.element("content");
                currentElement = currentElement.element("properties");
                String clazz = attrs.get("class").toString().substring(6);

                if(clazz.equals("weaver.interfaces.sapsyn.bean.Product")){
                    dataObj = new Product();
                }else if(clazz.equals("weaver.interfaces.sapsyn.bean.Project")){
                    dataObj = new Project();
                }else if(clazz.equals("weaver.interfaces.sapsyn.bean.Supplier")){
                    dataObj = new Supplier();
                }

                Set cols = attrs.keySet();
                for (Iterator iter = cols.iterator(); iter.hasNext();){
                    String col = (String) iter.next();
                    String tempCol = String.valueOf(col);
                    tempCol = tempCol.substring(0,1).toUpperCase() +tempCol.substring(1);//     tempCol.replace(tempCol.substring(0,1),tempCol.substring(0,1).toUpperCase());
                    if(currentElement.element(tempCol) != null && !tempCol.equals("class")){
                        String val = Util.null2String(currentElement.element(tempCol).getText());
                        BeanUtils.setProperty(dataObj,col,val);
                    }else{
                        if(!tempCol.equalsIgnoreCase("class")){
                            logger.error(">>>>>>>"+tempCol+"值为空");
                        }
                    }
                }
                productList.add(dataObj);
            }
        }catch (Exception e){
            logger.error(">>>>>>>>>>>>>>>-getData-获取数据报错："+e,e);
        }
        return productList;
    }

    /**
     * 转换xml
     * @param purchaseReqCreateRequest 请购单对象
     * **/
    public static String parseXML(PurchaseReqCreateRequest purchaseReqCreateRequest){
        String prefix = "";//xml前缀
        String postfix = "";//xml后缀
        String xml = "";//xml内容
        String xmlDetail = "";//明细表xml内容
        ArrayList<PurchaseRequisitionCreateItem> purchaseRequisitionCreateItems = purchaseReqCreateRequest.getPurchaseRequisitionCreateItems();
        prefix = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:pur=\"http://www.genor.com/PurchaseReqCreate/\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <pur:PurchaseReqCreateRequest>";
        postfix = "      </pur:PurchaseReqCreateRequest>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";
        try{
            //拼接主表xml
            xml += "<PurchaseRequisition>"+Util.null2String(purchaseReqCreateRequest.getPurchaseRequisition())+"</PurchaseRequisition>";
            xml += "<PurchaseRequisitionType>"+Util.null2String(purchaseReqCreateRequest.getPurchaseRequisitionType())+"</PurchaseRequisitionType>";
            xml += "<PurReqnDescription>"+Util.null2String(purchaseReqCreateRequest.getPurReqnDescription())+"</PurReqnDescription>";
//            xml += "<PurchaseRequisitionCreateItem>"+Util.null2String(purchaseReqCreateRequest.getPurchaseRequisition())+"</PurchaseRequisitionCreateItem>";

            //拼接明细表xml
            for(PurchaseRequisitionCreateItem purchaseRequisitionCreateItem:purchaseRequisitionCreateItems){
                xmlDetail += "<A_PurchaseRequisitionItemType>";
                xmlDetail += "<PurchaseRequisition>"+Util.null2String(purchaseRequisitionCreateItem.getPurchaseRequisition())+"</PurchaseRequisition>";
                xmlDetail += "<PurchaseRequisitionItem>"+Util.null2String(purchaseRequisitionCreateItem.getPurchaseRequisitionItem())+"</PurchaseRequisitionItem>";
                xmlDetail += "<PurchaseRequisitionType>"+Util.null2String(purchaseRequisitionCreateItem.getPurchaseRequisitionType())+"</PurchaseRequisitionType>";
                xmlDetail += "<PurchaseRequisitionItemText>"+Util.null2String(purchaseRequisitionCreateItem.getPurchaseRequisitionItemText())+"</PurchaseRequisitionItemText>";
                xmlDetail += "<AccountAssignmentCategory>"+Util.null2String(purchaseRequisitionCreateItem.getAccountAssignmentCategory())+"</AccountAssignmentCategory>";
                xmlDetail += "<Material>"+Util.null2String(purchaseRequisitionCreateItem.getMaterial())+"</Material>";
                xmlDetail += "<MaterialGroup>"+Util.null2String(purchaseRequisitionCreateItem.getMaterialGroup())+"</MaterialGroup>";
                xmlDetail += "<BaseUnit>"+Util.null2String(purchaseRequisitionCreateItem.getBaseUnit())+"</BaseUnit>";
                xmlDetail += "<PurchaseRequisitionPrice>"+Util.null2String(purchaseRequisitionCreateItem.getPurchaseRequisitionPrice())+"</PurchaseRequisitionPrice>";
                xmlDetail += "<PurReqnPriceQuantity>"+Util.null2String(purchaseRequisitionCreateItem.getPurReqnPriceQuantity())+"</PurReqnPriceQuantity>";
                xmlDetail += "<PurchasingOrganization>"+Util.null2String(purchaseRequisitionCreateItem.getPurchasingOrganization())+"</PurchasingOrganization>";
                xmlDetail += "<FixedSupplier>"+Util.null2String(purchaseRequisitionCreateItem.getFixedSupplier())+"</FixedSupplier>";
                xmlDetail += "<CreatedByUser>"+Util.null2String(purchaseRequisitionCreateItem.getCreatedByUser())+"</CreatedByUser>";
                xmlDetail += "<PurReqnItemCurrency>"+Util.null2String(purchaseRequisitionCreateItem.getPurReqnItemCurrency())+"</PurReqnItemCurrency>";
                xmlDetail += "<SourceOfSupplyIsAssigned>"+Util.null2String(purchaseRequisitionCreateItem.getSourceOfSupplyIsAssigned())+"</SourceOfSupplyIsAssigned>";
                xmlDetail += "<PurchasingGroup>"+Util.null2String(purchaseRequisitionCreateItem.getPurchasingGroup())+"</PurchasingGroup>";
                xmlDetail += "<Plant>"+Util.null2String(purchaseRequisitionCreateItem.getPlant())+"</Plant>";
                xmlDetail += "<CompanyCode>"+Util.null2String(purchaseRequisitionCreateItem.getCompanyCode())+"</CompanyCode>";
                xmlDetail += "<DeliveryDate>"+Util.null2String(purchaseRequisitionCreateItem.getDeliveryDate())+"</DeliveryDate>";
                xmlDetail += "<RequirementTracking>"+Util.null2String(purchaseRequisitionCreateItem.getRequirementTracking())+"</RequirementTracking>";
                xmlDetail += "<RequisitionerName>"+Util.null2String(purchaseRequisitionCreateItem.getRequisitionerName())+"</RequisitionerName>";
                xmlDetail += "<RequestedQuantity>"+Util.null2String(purchaseRequisitionCreateItem.getRequestedQuantity())+"</RequestedQuantity>";
                xmlDetail += "<SupplierMaterialNumber>"+Util.null2String(purchaseRequisitionCreateItem.getSupplierMaterialNumber())+"</SupplierMaterialNumber>";
                xmlDetail += "<GoodsReceiptIsNonValuated>"+Util.null2String(purchaseRequisitionCreateItem.getGoodsReceiptIsNonValuated(),"false")+"</GoodsReceiptIsNonValuated>";
                xmlDetail += "<InvoiceIsExpected>"+Util.null2String(purchaseRequisitionCreateItem.getInvoiceIsExpected(),"true")+"</InvoiceIsExpected>";
                xmlDetail += "<GoodsReceiptIsExpected>"+Util.null2String(purchaseRequisitionCreateItem.getGoodsReceiptIsExpected(),"true")+"</GoodsReceiptIsExpected>";
                //拼接 科目分配（结构）
                xmlDetail += "<PurchaseReqnAcctAssgmt><A_PurReqnAcctAssgmtType>";

                xmlDetail += "<PurchaseRequisition>"+Util.null2String(purchaseRequisitionCreateItem.getPurchaseRequisition(),"true")+"</PurchaseRequisition>";
                xmlDetail += "<PurchaseRequisitionItem>"+Util.null2String(purchaseRequisitionCreateItem.getPurchaseRequisitionItem(),"true")+"</PurchaseRequisitionItem>";
                xmlDetail += "<CostCenter>"+Util.null2String(purchaseRequisitionCreateItem.getCostCenter())+"</CostCenter>";
                xmlDetail += "<FixedAsset>"+Util.null2String(purchaseRequisitionCreateItem.getFixedAsset())+"</FixedAsset>";
                xmlDetail += "<WBSElement>"+Util.null2String(purchaseRequisitionCreateItem.getWBSElement())+"</WBSElement>";
                xmlDetail += "<ChartOfAccounts>"+Util.null2String("")+"</ChartOfAccounts>";
                xmlDetail += "<GLAccount>"+Util.null2String(purchaseRequisitionCreateItem.getGLAccount())+"</GLAccount>";

                xmlDetail += "</A_PurReqnAcctAssgmtType></PurchaseReqnAcctAssgmt>";
                xmlDetail += "</A_PurchaseRequisitionItemType>";
            }
            xmlDetail = "<PurchaseRequisitionCreateItem>" + xmlDetail + "</PurchaseRequisitionCreateItem>";
            xml = prefix + xml + xmlDetail + postfix;
            logger.info(">>>>>>>>>>>>>拼接xml输出："+xml);
        }catch (Exception e){
            logger.error(">>>>>>>>>>>>拼接xml报错："+e,e);
        }
        return xml;
    }

    /**
     * 获取供应商数据
     * xml数据结构：
     * <A_BusinessPartner>
     *     <A_BusinessPartnerType>
     *         <to_BusinessPartnerBank>银行信息</to_BusinessPartnerBank>
*              <to_Supplier>供应商信息</to_Supplier>
*              <BusinessPartnerFullName></BusinessPartnerFullName>
*              <BusinessPartnerName></BusinessPartnerName>
*              <BusinessPartner></BusinessPartner>
     **/
    public ArrayList<Supplier> getDataOfSupplier(String urlRoot,String username,String password){
        ArrayList<Supplier> supplierList = new ArrayList<Supplier>();
        try{
            String data = HTTPUtils.doGetWithBasicAuth(urlRoot,username,password);
            logger.info(">>>>>>>>>>>>>>>-getData.data:"+data);
            Document document = XMLUtil.parseXMLStr(data);
            //获取根节点
            Element rootNode = XMLUtil.getRootNode(document);
            List<Element> entryList = XMLUtil.getChildrenNodesByName(rootNode,"A_BusinessPartnerType");
            Element currentElement = null;
            Supplier supplier = null;
            SupplierBanks bank = null;
            List<SupplierBanks> banks = null;
            //遍历数据存储节点list
            for (Element e:entryList){
                //获取主表信息
                supplier = new Supplier();
                supplier.setBusinessPartner(Util.null2String(this.myNull2String(e,"BusinessPartner")));
                supplier.setBusinessPartnerName(Util.null2String(this.myNull2String(e,"BusinessPartnerName")));
                supplier.setBusinessPartnerFullName(Util.null2String(this.myNull2String(e,"BusinessPartnerFullName")));
                Element supplierElement = e.element("to_Supplier").element("A_SupplierType");
                supplier.setSupplier(Util.null2String(this.myNull2String(supplierElement,"Supplier")));
                supplier.setSupplierName(Util.null2String(this.myNull2String(supplierElement,"SupplierName")));
                supplier.setSupplierFullName(Util.null2String(this.myNull2String(supplierElement,"SupplierFullName")));
                //获取明细表信息
                banks = new ArrayList<SupplierBanks>();
                Element to_BusinessPartnerBank = e.element("to_BusinessPartnerBank");
//                List<Element> bankList = XMLUtil.getChildrenNodesByName(e,"to_BusinessPartnerBank");
                List<Element> bankList = XMLUtil.getChildrenNodesByName(to_BusinessPartnerBank,"A_BusinessPartnerBankType");
                for(Element banksElement:bankList){
                    bank = new SupplierBanks();
//                    Element bankElement = banksElement.element("A_BusinessPartnerBankType");
                    Element bankElement = banksElement;
                    bank.setBankAccount(Util.null2String(this.myNull2String(bankElement,"BankAccount")));
                    bank.setBankName(Util.null2String(this.myNull2String(bankElement,"BankName")));
                    bank.setBankAccountHolderName(Util.null2String(this.myNull2String(bankElement,"BankAccountHolderName")));
                    bank.setBankAccountReferenceText(Util.null2String(this.myNull2String(bankElement,"BankAccountReferenceText")));
                    bank.setBankIdentification(Util.null2String(this.myNull2String(bankElement,"BankIdentification")));
                    banks.add(bank);
                }
                supplier.setBanks(banks);
                supplierList.add(supplier);
            }
        }catch (Exception e){
            logger.error(">>>>>>>>>>>>>>>-getData-获取数据报错："+e,e);
        }
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>smile-supplierList.size:"+supplierList.size());
        return supplierList;
    }

    /**
    * @Description TODO xml节点判空
    * @auther phf
    * @date   2019/6/17 14:11
    * @param  element
    * @param  key
    * @return java.lang.String
    **/
    public String myNull2String(Element element,String key){
//        if(element == null){
//            logger.info(">>>>>>>>>>>>>>smile:"+key+"为空");
//        }
        return element == null ? "" : element.element(key).getText();
    }


}
