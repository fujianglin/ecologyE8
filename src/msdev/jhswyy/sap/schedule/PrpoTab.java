package msdev.jhswyy.sap.schedule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

public class PrpoTab extends BaseCronJob{
	public void execute(){
		BaseBean bb = new BaseBean();
		
		bb.writeLog("计划任务开关！");
		String jhrwl="msdev.jhswyy.sap.schedule.PrpoTab";//计划任务类路径
		RecordSet jhrwRs = new RecordSet();
		String jhrwZtSql="select sfqy from uf_sapJhControl where jhrwl='"+jhrwl+"'";
		jhrwRs.execute(jhrwZtSql);
		if(jhrwRs.next()){
			String sfqy = jhrwRs.getString("sfqy");//是否启用 0是启用  1否禁用
			if("0".equals(sfqy)){//括号里面放置计划任务代码
				bb.writeLog("执行计划任务！");
				RecordSet rs = new RecordSet();
				String tablename = bb.getPropValue("saputil", "sap.prpo.tableName");
				String deleteSql = "delete from "+tablename;
				rs.execute(deleteSql);
				
				String formmodeid = bb.getPropValue("saputil", "sap.prpo.formmodeid");
				String urlRoot = bb.getPropValue("saputil", "sap.prpo.urlRoot");
				String username = bb.getPropValue("saputil", "sap.username");
				String password = bb.getPropValue("saputil", "sap.password");
				String[] myParams = new String[] {
						"PurchaseRequisition",
						"PurchaseRequisitionItem",
						"PurchaseOrder",
						"PurchaseOrderItem",
						"Material",
						"PurchaseOrderItemText",
						"PurchaseOrderQuantityUnit",
						"NetPriceQuantity",
						"NetPriceAmount",
						"OrderQuantity",
						"OrderPriceUnitToOrderUnitNmrtr",
						"OrdPriceUnitToOrderUnitDnmntr",
						"DocumentCurrency"
				};
				prpoDataUtil(urlRoot, username, password,tablename,formmodeid, myParams);
			}else{
				bb.writeLog("不执行计划任务！");
			}
		}
		
	}
	
	/**
	 * 获取sapXML的数据
	 * @param urlRoot  sap接口地址
	 * @param username  sap接口username
	 * @param password  sap接口password
	 * @param tablename 同步到OA的表名
	 * @param formmodeid 同步到OA表的formmodeid
	 * @param myParams  同步的字段需要与sap相对应
	 */
	public void prpoDataUtil(String urlRoot,String username,String password,String tablename,String formmodeid,String[] myParams){
		RecordSet rs = new RecordSet();
		String nameAndpwd=username+":"+password;
		String base64=new String (weaver.general.Base64.encode(nameAndpwd.getBytes()));
		String code = "Basic "+base64;
		//获取token
		//String token= "";
		HttpGet httpget = new HttpGet(urlRoot);
		rs.writeLog("urlRoot="+urlRoot);
		httpget.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpget.setHeader("Authorization", code);
		httpget.setHeader("x-csrf-token", "fetch");
		//Header headers[] = httpget.getAllHeaders();
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse res;
		String content ="";
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
            	Map<String, String> paramsMap=new HashMap<String, String>();
            	while (entry.hasNext()) {
                	Element recordEle = (Element) entry.next();
                	Element contentElt = recordEle.element("content");
                	Element propertiesElt = contentElt.element("properties");
                	for (String myparam : myParams) {
                		paramsMap.put(myparam, propertiesElt.elementTextTrim(myparam));
                	}
                	/*计算后总价价 
                	 * [ 订单数量（OrderQuantity）*分子(OrderPriceUnitToOrderUnitNmrtr) /分母(OrdPriceUnitToOrderUnitDnmntr)  ]
                	 * * 订单净价（NetPriceAmount）*（1+税率）/价格数量单位（NetPriceQuantity）
                	 * 
                	 */
                	double hxmzj=Util.round2(((Util.getDoubleValue(paramsMap.get("OrderQuantity"))*Util.getDoubleValue(paramsMap.get("OrderPriceUnitToOrderUnitNmrtr")))
                				/Util.getDoubleValue(paramsMap.get("OrdPriceUnitToOrderUnitDnmntr")))*(Util.getDoubleValue(paramsMap.get("NetPriceAmount"))
                						/Util.getDoubleValue(paramsMap.get("NetPriceQuantity")))+"",2);
                	paramsMap.put("hxmzj", hxmzj+"");
                	//计算后单价
                	double NetPriceAmount=Util.round2((hxmzj/Util.getDoubleValue(paramsMap.get("OrderQuantity")))+"",2);
                	paramsMap.put("NetPriceAmount", NetPriceAmount+"");
                	String paramsSqlStr = "formmodeid,";
                	String valuesSqlStr = formmodeid+",";
                	for(Map.Entry<String, String> params : paramsMap.entrySet()){
                		paramsSqlStr+=params.getKey()+",";
                		valuesSqlStr+="'"+params.getValue().replace("'", "''")+"',";
            		}
                	paramsSqlStr = paramsSqlStr.substring(0, paramsSqlStr.length() - 1);
                	valuesSqlStr = valuesSqlStr.substring(0, valuesSqlStr.length() - 1);
                	String insertSql = "insert into "+tablename+" ("+paramsSqlStr+") values("+valuesSqlStr+")";
                	rs.execute(insertSql);
                	//给权限
        			int maxid= 0;
        			rs.execute(" select MAX(ID) AS MAXID from "+tablename);
        			if(rs.next()){
        				maxid=rs.getInt("MAXID");
        			}
        			ModeRightInfo moderightinfo = new ModeRightInfo();
        			moderightinfo.editModeDataShare(1, Util.getIntValue(formmodeid), maxid);// 新建的时候添加共享

            		
            	}             	
		}catch (Exception e) {
			rs.writeLog("xfx291418解析返回值异常"+e);
			e.printStackTrace();
		}
	}
	
	
}
