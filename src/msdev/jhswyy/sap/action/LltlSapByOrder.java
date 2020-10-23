package msdev.jhswyy.sap.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSON;

import weaver.general.BaseBean;

public class LltlSapByOrder extends BaseBean{
	/**
	 * 领料退料根据订单获取明细
	 * @param ddbh 订单编号
	 * @return
	 */
	public String lltlList(String ddbh){
		writeLog("--- msdev/jhswyy/sap/action/lltlList  开始获取订单总详细数据  ---");
		String ddRootUrl = (getPropValue("lltlSap", "sap.rootUrl")+"/sap/opu/odata/sap/YY1_MFGORDER_COMPONENT_CDS/YY1_MFGORDER_COMPONENT?$filter=(ManufacturingOrder eq '"+ddbh+"')").replaceAll(" ","%20");  
		
		String[] myddParams = new String[] {
				"Material_01",					//生产物料    需要传到获取批次的接口
				"Batch_01",						//批次  		需要传到获取批次的接口
				"ReservationItem",				//行项目
				"Material",						//原材料
				"ProductionPlant",				//生产工厂
				"RequiredQuantity",				//总需求量
				"WithdrawnQuantity",			//提取数量
				"EntryUnit",					//条目单位
				"ReservationIsFinallyIssued"   	//最后发货
		};
		List<Map<String,String>> ddList = sapUtil(ddRootUrl,myddParams);
		for(Map<String,String> dd:ddList){
			Double finalQuantity = Double.parseDouble(dd.get("RequiredQuantity"))-Double.parseDouble(dd.get("WithdrawnQuantity"));
			dd.put("finalQuantity", finalQuantity+"");//OA显示需求量
			String pcRootUrl = (getPropValue("lltlSap", "sap.rootUrl")+"/sap/opu/odata/sap/API_BATCH_SRV/BatchCharc(Material='"+dd.get("Material_01")+"',BatchIdentifyingPlant='',Batch='"+dd.get("Batch_01")+"',CharcInternalID='4')/to_BatchCharcValue").replaceAll(" ","%20");  
			String[] mypcParams = new String[] {
					"CharcValue"					//内部批次号
			};
			List<Map<String,String>> pcList = sapUtil(pcRootUrl,mypcParams);
			if(pcList.size()>0){
				String pc = pcList.get(0).get("CharcValue");
				dd.put("CharcValue", pc);
			}else{
				dd.put("CharcValue", "");
			}
			String kcRootUrl = (getPropValue("lltlSap", "sap.rootUrl")+"/sap/opu/odata/sap/YY1_MATERIALSTOCT_CDS/YY1_MATERIALSTOCT?$filter=(Material eq '"+dd.get("Material")+"' and Plant eq '"+dd.get("ProductionPlant")+"')").replaceAll(" ","%20");  
			
			String[] mykcParams = new String[] {
					"InventoryStockType",							//库存类型  01/02
					"MatlWrhsStkQtyInMatlBaseUni",					//库存数量
					"MaterialBaseUnit"								//基本计量单位
			};
			List<Map<String,String>> kcList = sapUtil(kcRootUrl,mykcParams);
			double kykc = 0;   //库存类型  01
			double djkc = 0;	//库存类型  02
			for(Map<String,String> kc:kcList){
				if("01".equals(kc.get("InventoryStockType"))){
					kykc += Double.parseDouble(kc.get("MatlWrhsStkQtyInMatlBaseUni"));
				}else if("02".equals(kc.get("InventoryStockType"))){
					djkc += Double.parseDouble(kc.get("MatlWrhsStkQtyInMatlBaseUni"));
				}
				String MaterialBaseUnit = kcList.get(0).get("MaterialBaseUnit");
				dd.put("MaterialBaseUnit", MaterialBaseUnit);
			}
			DecimalFormat def  = new DecimalFormat("#0.00");
			dd.put("kykc", def.format(kykc));
			dd.put("djkc", def.format(djkc));
		}
		String result = JSON.toJSONString(ddList);
		writeLog("--- msdev/jhswyy/sap/action/lltlList  结束获取订单总详细数据  ---");
		return result;
	}
	
	public List<Map<String,String>> sapUtil(String rootUrl,String[] myParams){
		writeLog("--- msdev/jhswyy/sap/action/lltlList  开始获取接口数据  ---");
		String urlRoot = rootUrl;
		String username = getPropValue("lltlSap", "sap.username");
		String password = getPropValue("lltlSap", "sap.password");
		String nameAndpwd=username+":"+password;
		String base64=new String (weaver.general.Base64.encode(nameAndpwd.getBytes()));
		String code = "Basic "+base64;
		//获取token
		String token= "";
		HttpGet httpget = new HttpGet(urlRoot);
		//System.out.println("urlRoot="+urlRoot);
		httpget.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpget.setHeader("Authorization", code);
		httpget.setHeader("x-csrf-token", "fetch");
		Header headers[] = httpget.getAllHeaders();
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse res;
		String content ="";
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try { 
			res = httpclient.execute(httpget);
			BufferedReader in = new BufferedReader(new InputStreamReader(res.getEntity().getContent(),"UTF-8"));  
            StringBuffer sb = new StringBuffer("");  
            String line = "";  
            String NL = System.getProperty("line.separator");  
            while ((line = in.readLine()) != null) {  
                sb.append(line + NL);  
            }  
            in.close();  
            content = sb.toString();
            Document doc = null;
            // 将字符串转为XML 
            doc = DocumentHelper.parseText(content);
            // 获取根节点 feed
            Element rootElt = doc.getRootElement();
            Iterator entry = rootElt.elementIterator("entry"); // 获取根节点下的子节点entry
            while (entry.hasNext()) {
            	Map<String,String> paramValue = new HashMap<String,String>();
            	Element recordEle = (Element) entry.next();
            	Element contentElt = recordEle.element("content");
            	Element propertiesElt = contentElt.element("properties");
            	for (String myparam : myParams) {
            		String sapValue = propertiesElt.elementTextTrim(myparam);
            		paramValue.put(myparam, sapValue);
                }
            	list.add(paramValue);
            }
		}catch (Exception e) {
			e.printStackTrace();
		}
		writeLog("--- msdev/jhswyy/sap/action/lltlList  结束获取接口数据  ---");
		return list;
	}
	
	public static void main(String[] args) {
		LltlSapByOrder test = new LltlSapByOrder();
		String resultList = test.lltlList("1000175");
		System.out.println(resultList);
	}
}
