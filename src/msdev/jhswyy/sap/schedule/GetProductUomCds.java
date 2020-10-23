package msdev.jhswyy.sap.schedule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.http.Header;
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

/**
 * @author joshua.cheng
 * 获取SAP的物料单位转换表
 */
public class GetProductUomCds extends BaseCronJob{
	public void execute(){
		BaseBean bb = new BaseBean();
		bb.writeLog("计划任务开关！");
		String jhrwl="msdev.jhswyy.sap.schedule.GetProductUomCds";//计划任务类路径
		RecordSet jhrwRs = new RecordSet();
		String jhrwZtSql="select sfqy from uf_sapJhControl where jhrwl='"+jhrwl+"'";
		jhrwRs.execute(jhrwZtSql);
		if(jhrwRs.next()){
			String sfqy = jhrwRs.getString("sfqy");//是否启用 0是启用  1否禁用
			if("0".equals(sfqy)){//括号里面放置计划任务代码
				bb.writeLog("执行计划任务！");
				
				RecordSet rs = new RecordSet();
				String tablename = bb.getPropValue("saputil", "sap.productUomCds.tableName");
				String deleteSql = "delete from "+tablename;
				rs.execute(deleteSql);
				
				String formmodeid = bb.getPropValue("saputil", "sap.productUomCds.formmodeid");
				String urlRoot = bb.getPropValue("saputil", "sap.productUomCds.urlRoot");
				String username = bb.getPropValue("saputil", "sap.username");
				String password = bb.getPropValue("saputil", "sap.password");
				String[] myParams = new String[] {
						"Product",
						"AlternativeUnit",
						"BaseUnit",
						"QuantityNumerator",
						"QuantityDenominator",
						"PurchaseOrderQuantityUnit"
				};
				getProductUomCdsData(urlRoot, username, password,tablename,formmodeid, myParams);

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
	public void getProductUomCdsData(String urlRoot,String username,String password,String tablename,String formmodeid,String[] myParams){
		BaseBean bb = new BaseBean();
		RecordSet rs = new RecordSet();
		String nameAndpwd=username+":"+password;
		String base64=new String (weaver.general.Base64.encode(nameAndpwd.getBytes()));
		String code = "Basic "+base64;
		//获取token
		String token= "";
		HttpGet httpget = new HttpGet(urlRoot);
		System.out.println("urlRoot="+urlRoot);
		httpget.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpget.setHeader("Authorization", code);
		httpget.setHeader("x-csrf-token", "fetch");
		Header headers[] = httpget.getAllHeaders();
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
	            
	            bb.writeLog("content87:"+content);
	            
	            Document doc = null;
                // 将字符串转为XML 
                doc = DocumentHelper.parseText(content);
                // 获取根节点 feed
                Element rootElt = doc.getRootElement();
                Iterator entry = rootElt.elementIterator("entry"); // 获取根节点下的子节点entry
                while (entry.hasNext()) {
                	Element recordEle = (Element) entry.next();
                	Element contentElt = recordEle.element("content");
                	Element propertiesElt = contentElt.element("properties");
                	
                	String paramsSqlStr = "";
                	String valuesSqlStr = "";
                	for (String myparam : myParams) {
                		String sapValue = propertiesElt.elementTextTrim(myparam);
                		paramsSqlStr+=myparam+",";
                		valuesSqlStr+="'"+sapValue.replace("'", "''")+"',";
                    	//System.out.println("myparam="+sapValue);
                		 bb.writeLog("myparam="+sapValue);
                    }
                	paramsSqlStr = paramsSqlStr.substring(0, paramsSqlStr.length() - 1);
                	valuesSqlStr = valuesSqlStr.substring(0, valuesSqlStr.length() - 1);
                	String insertSql = "insert into "+tablename+" ("+paramsSqlStr+") values("+valuesSqlStr+")";
                	//System.out.println("myparam="+insertSql);
                	 bb.writeLog("myparam=113"+insertSql);
                	rs.execute(insertSql);
                	
                	//给权限
        			int maxid= 0;
        			rs.executeSql(" select MAX(ID) AS MAXID from "+tablename);
        			if(rs.next()){
        				maxid=rs.getInt("MAXID");
        			}
        			ModeRightInfo moderightinfo = new ModeRightInfo();
        			moderightinfo.editModeDataShare(1, Util.getIntValue(formmodeid), maxid);// 新建的时候添加共享
                }
	            
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
