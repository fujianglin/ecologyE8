package weaver.interfaces.sapsyn.action;
import weaver.conn.RecordSet;
import weaver.file.Prop;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

/**
 * @author misetech
 * 业务伙伴主数据（供应商）流程推送供应商信息到SAP
 */
public class SupplierPush implements Action {	
 
    public String execute(RequestInfo requestInfo) {
    	  RequestManager requestManager = null;//流程流转对象
   	      BaseBean bb = new BaseBean();	
   	      bb.writeLog("进入嘉和供应商推送接口：");
    	    //请购申请数据接口请求url根路径  测试
    		String urlRoot = Util.null2String(Prop.getPropValue("sapsyn","sap.gy.url"));
    		
    		String username = Util.null2String(Prop.getPropValue("sapsyn","sap.gy.username"));//OA_INTEGRATION
    		String password = Util.null2String(Prop.getPropValue("sapsyn","sap.gy.password"));//nJwkCqDWoZHdwrlzTRqVcHliZC}TWBC2ZAFNNDdM
    		
    		String nameAndpwd=username+":"+password;
    		String base64=new String (weaver.general.Base64.encode(nameAndpwd.getBytes()));
    		String code = "Basic "+base64;
    		
    		// 获取token
    		String token= "";
    		String cookie="";
    		HttpGet httpget = new HttpGet(urlRoot);
    		httpget.setHeader("Content-Type", "application/json");
    		httpget.setHeader("Authorization", code);
    		httpget.setHeader("x-csrf-token", "fetch");
    		
    		Header headers[] = httpget.getAllHeaders();
    		HttpClient httpclient = new DefaultHttpClient();
    		HttpResponse res;
    		try {
    			res = httpclient.execute(httpget);
    			headers = res.getAllHeaders();
    			
    			for (Header h : headers) {
    				if (h.getName().equals("set-cookie")) {
    					cookie =h.getValue();
    					bb.writeLog("cookie值："+cookie);
    				}
    				if (h.getName().equals("x-csrf-token")) {
    					token = h.getValue();
    					bb.writeLog("token值："+token);
    				}


    			}
	    
    		//	  bb.writeLog("token:"+token+",cookie值："+cookie);		
    		} catch (ClientProtocolException e) {			
    			e.printStackTrace();
    		} catch (IOException e) {			
    			e.printStackTrace();
    		}
    	 
        try{
            requestManager = requestInfo.getRequestManager();        
            int requestid = requestManager.getRequestid(); //流程requestid   
            String mainTableName = Util.null2String(requestManager.getBillTableName());  //获取流程主表名    
            bb.writeLog("主表名称："+mainTableName); //获取流程明细表名
            String detailTableName = mainTableName + "_dt1"; //明细表
            RecordSet recordSet = new RecordSet();
            RecordSet rs = new RecordSet();
            String gysmc ="";//供应商名称
            String dzjd ="";//地址/街道
            String country ="";//国家
            String language ="";//语言
            String  cs2 ="";//城市
            String area ="";//地区
            String  lxdh ="";//电话
            String fenjihao ="";//分机号
            String lxryx2 ="";//联系人邮箱
            String yhgj ="";//银行国家
            String yhdm ="";//银行代码
            String yhzh ="";//银行账号
            String ckmx ="";//参考明细
            String qyfr2 ="";//开户人
            String ssfl ="";//税收分类
            String shh ="";//税号
            String gsdm ="";//公司代码
            String fktj ="";//付款条件
            String fkfs ="";//付款方式
            String tykm ="";//统驭科目
            String cgzz ="";//采购组织
            String huobi ="";//货币
            String lxr ="";//供应商联系人
           
            //开户行根据申请公司进行判断
            String shenqinggs="";//所属公司
            String HouseBank="";//开户行
            //查询主表 
            String sql = "select * from " + mainTableName + " where requestid = " + requestid;
           
            recordSet.executeQuery(sql);
            
            if(recordSet.next()){
                gysmc =Util.null2String(recordSet.getString("gysmc"));//供应商名称
                dzjd =Util.null2String(recordSet.getString("dzjd"));//地址/街道
                country =Util.null2String(recordSet.getString("country"));//国家
                language =Util.null2String(recordSet.getString("language"));//语言
                 cs2 =Util.null2String(recordSet.getString("cs2"));//城市
                 area =Util.null2String(recordSet.getString("area"));//地区
                 lxdh =Util.null2String(recordSet.getString("lxdh"));//电话
                 fenjihao =Util.null2String(recordSet.getString("fenjihao"));//分机号
                lxryx2 =Util.null2String(recordSet.getString("lxryx2"));//联系人邮箱
                yhgj =Util.null2String(recordSet.getString("yhgj"));//银行国家
                yhdm =Util.null2String(recordSet.getString("yhdmwb"));//银行代码
                yhzh =Util.null2String(recordSet.getString("yhzh"));//银行账号
                ckmx =Util.null2String(recordSet.getString("ckmx"));//参考明细
            	qyfr2 =Util.null2String(recordSet.getString("qyfr2"));//开户人
                ssfl =Util.null2String(recordSet.getString("ssfl"));//税收分类
                shh =Util.null2String(recordSet.getString("shh"));//税号
                gsdm =Util.null2String(recordSet.getString("gsdm"));//公司代码
                fktj =Util.null2String(recordSet.getString("fktj"));//付款条件
                fkfs =Util.null2String(recordSet.getString("fkfs"));//付款方式
                tykm =Util.null2String(recordSet.getString("tykm"));//统驭科目
                cgzz =Util.null2String(recordSet.getString("cgzz"));//采购组织
                huobi =Util.null2String(recordSet.getString("huobi"));//货币
                lxr =Util.null2String(recordSet.getString("lxr"));//供应商联系人
                shenqinggs=Util.null2String(recordSet.getString("sqfb"));//所属公司
               
                
            }//针对银行账户进行判断
         
            if(yhzh.length()>18){     	
            	ckmx=yhzh.substring(18,yhzh.length());
            	yhzh=yhzh.substring(0,18);
            }
            
            //根据公司代码进行转换
            if("0".equals(gsdm)){
            	gsdm="3100";
            	HouseBank="SH310";
                bb.writeLog("传的公司是上海3100");
            	
            }else{
            	gsdm="4100";
            	 HouseBank="YX410";
           	  bb.writeLog("传的公司是玉溪4100");
            }
            //根据银行国家进行转换
            if("0".equals(yhgj)){
            	yhgj="CN";
            	
            }
              //采购组织转换
            if("0".equals(cgzz)){
            	cgzz="3100";
            }
          
            bb.writeLog("供应商名称:"+gysmc+"地址/街道:"+dzjd+"国家:"+country+"语言:"+language+"城市："+cs2+"地区:"+area+"电话:"+lxdh+"分机号："+fenjihao+
					"联系人邮箱:"+lxryx2+"银行国家:"+yhgj+"银行代码:"+yhdm+"银行账号:"+yhzh+"参考明细:"
					+ckmx+"开户人:"+qyfr2+"税收分类:"+ssfl+"税号:"+shh
					+"公司代码:"+gsdm+"付款条件:"+fktj+"付款方式:"+fkfs+"统驭科目："+tykm+"采购组织："+cgzz
					+"货币:"+huobi+"供应商联系人:"+lxr+"所属公司:"+shenqinggs);
           
          
        	//拼接json
    		String jsonData = "	{	"
    				+"	  \"BusinessPartner\": \"\",	"
    				+"	  \"Supplier\": \"\",	"
    				+"	  \"BusinessPartnerCategory\": \"2\",	"
    			//	+"	  \"BusinessPartnerFullName\": \"供应商接口测试_001\",	"
    				+"	  \"BusinessPartnerGrouping\": \"BP01\",	"
    			//	+"	  \"BusinessPartnerName\": \"供应商接口测试_001\",	"
    			  //+"	  \"BusinessPartnerUUID\": \"fa163e1e-7463-1ee9-96d2-beb108bd31eb\",	"
    				+"	  \"FormOfAddress\": \"0003\",	"
    				+"	  \"OrganizationBPName1\": \""+gysmc+"\",	"
    				+"	  \"OrganizationBPName2\": \"\",	"
    				+"	  \"OrganizationBPName3\": \"\",	"
    				+"	  \"OrganizationBPName4\": \"\",	"
    				+"	  \"SearchTerm1\": \"\",	"
    				+"	  \"to_BusinessPartnerAddress\": {	"
    				+"	    \"results\": [	"
    				+"	      {	"
    				+"	        \"BusinessPartner\": \"\",	"
    				//+"	        \"AdditionalStreetPrefixName\": \"上海浦东张江张衡路1690号\",	"
    				//+"	        \"AdditionalStreetSuffixName\": \"科技园3#楼\",	"
    				+"	        \"AddressTimeZone\": \"UTC+8\",	"
    				//+"	        \"CityCode\": \"SH\",	"
    				+"	        \"CityName\": \""+cs2+"\",	"
    				+"	        \"Country\": \""+country+"\",	"
    				//+"	        \"County\": \"\",	"
    				//+"	        \"District\": \"BJ\",	"
    				+"	        \"FormOfAddress\": \"0003\",	"
    			//	+"	        \"FullName\": \"供应商接口测试_001\",	"
    				+"	        \"HouseNumber\": \"\",	"
    				+"	        \"Language\": \""+language+"\",	"
    				+"	        \"PostalCode\": \"\",	"
    				+"	        \"Region\": \""+area+"\",	"
    				+"	        \"StreetName\": \""+dzjd+"\",	"
    				+"	        \"to_AddressUsage\": {	"
    				+"	          \"results\": [	"
    				+"	            {	"
    				+"	               \"BusinessPartner\": \"\",	"
    				+"	               \"AddressUsage\": \"XXDEFAULT\",	"
    				+"	               \"StandardUsage\": false,	"
    				+"	               \"AuthorizationGroup\": \"\"	"
    				+"	            }	"
    				+"	          ]	"
    				+"	        },	"
    				+"		"
    				+"	        \"to_EmailAddress\": {	"
    				+"	          \"results\": [	"
    				+"	            {	"
    				+"	              \"AddressID\": \"\",	"
    				+"	              \"OrdinalNumber\": \"\",	"
    				+"	              \"IsDefaultEmailAddress\": true,	"
    				+"	              \"EmailAddress\": \""+lxryx2+"\"	"
    			//	+"	              \"SearchEmailAddress\": \"ZZZ\"	"
    				+"	            }	"
    				+"	          ]	"
    				+"	        },	"
    				/*+"	        \"to_FaxNumber\": {	"
    				+"	          \"results\": [	"
    				+"	            {	"
    				+"	              \"AddressID\": \"\",	"
    				+"	              \"OrdinalNumber\": \"\",	"
    				+"	              \"IsDefaultFaxNumber\": true,	"
    				+"	              \"FaxCountry\": \"CN\",	"
    				+"	              \"FaxNumber\": \"2345670\",	"
    				+"	              \"FaxNumberExtension\": \"456\",	"
    				+"	              \"InternationalFaxNumber\": \"34579712\"	"
    				+"	            }	"
    				+"	          ]	"
    				+"	        },	"*/
    				/*+"	        \"to_MobilePhoneNumber\": {	"
    				+"	          \"results\": [	"
    				+"	            {	"
    				+"	              \"AddressID\": \"\",	"
    				+"	              \"OrdinalNumber\": \"\",	"
    				+"	              \"DestinationLocationCountry\": \"CN\",	"
    				+"	              \"IsDefaultPhoneNumber\":true,	"
    				+"	              \"PhoneNumber\": \"\",	"
    				+"	              \"PhoneNumberExtension\": \"\",	"
    				+"	              \"InternationalPhoneNumber\": \"\",	"
    				+"	              \"PhoneNumberType\": \"\"	"
    				+"	            }	"
    				+"	          ]	"
    				+"	        },	"*/
    				+"	        \"to_PhoneNumber\": {	"
    				+"	          \"results\": [	"
    				+"	            {	"
    				+"	              \"AddressID\": \"\",	"
    				+"	              \"OrdinalNumber\": \"\",	"
    				+"	              \"DestinationLocationCountry\": \""+country+"\",	"
    				+"	              \"IsDefaultPhoneNumber\": true,	"
    				+"	              \"PhoneNumber\": \""+lxdh+"\",	"
    				+"	              \"PhoneNumberExtension\": \""+fenjihao+"\",	"
    				//+"	              \"InternationalPhoneNumber\": \"34567890\",	"
    				+"	              \"PhoneNumberType\": \"\"	"
    				+"	            }	"
    				+"	          ]	"
    				+"	        },	"
    				+"	        \"to_URLAddress\": {	"
    				+"	          \"results\": [	"
    				+"	            {	"
    				+"	              \"AddressID\": \"\",	"
    				+"	              \"Person\": \"\",	"
    				+"	              \"OrdinalNumber\": \"\",	"
    				+"		"
    				+"	              \"IsDefaultURLAddress\": true,	"
    				+"	              \"SearchURLAddress\": \"\",	"
    				+"		"
    				+"	              \"WebsiteURL\": \"\"	"
    				+"	            }	"
    				+"	          ]	"
    				+"	        }	"
    				+"	      }	"
    				+"	    ]	"
    				+"	  },	"
    				+"	  \"to_BusinessPartnerBank\": {	"
    				+"	    \"results\": [	"
    				+"	      {	"
    				+"	        \"BusinessPartner\": \"\",	"
    				+"	        \"BankIdentification\": \"0001\",	"
    				+"	        \"BankCountryKey\": \""+yhgj+"\",	"
    				+"	        \"BankName\": \"\",	"
    				+"	        \"BankNumber\": \""+yhdm+"\",	"
    				+"	        \"BankAccountHolderName\": \""+qyfr2+"\",	"
    				+"	        \"BankAccount\": \""+yhzh+"\",	"
    				+"	        \"BankAccountReferenceText\": \""+ckmx+"\",	"
    				+"	        \"CityName\": \"\"	"
    				+"	      }	"
    				+"	    ]	"
    				+"	  },	"
    				+"	  \"to_BusinessPartnerTax\": {	"
    				+"	    \"results\": [	"
    				+"	      {	"
    				+"	        \"BusinessPartner\": \"\",	"
    				+"	        \"BPTaxType\": \"CN5\",	"
    				+"	        \"BPTaxNumber\": \" "+shh+"\"	"
    				+"	      }	"
    				+"	    ]	"
    				+"	  },	"
    				+"	  \"to_BusinessPartnerRole\": {	"
    				+"	    \"results\": [	"
    				+"	      {	"
    				+"	        \"BusinessPartner\": \"\",	"
    				+"	        \"BusinessPartnerRole\": \"FLVN00\"	"
    				+"	      },	"
    				+"	      {	"
    				+"	        \"BusinessPartner\": \"\",	"
    				+"	        \"BusinessPartnerRole\": \"FLVN01\"	"
    				+"	      }	"
    				+"	    ]	"
    				+"	  },	"
    				+"	   \"to_Supplier\": {	"
    				+"	    \"Supplier\": \"\",	"
    				+"	    \"SupplierAccountGroup\": \"SUPL\",	"
    				+"	    \"SupplierFullName\": \"\",	"
    				+"	    \"SupplierName\": \"\",	"
    				+"	    \"VATRegistration\": \"\",	"
    				+"	    \"to_SupplierCompany\": {	"
    				+"	      \"results\": [	"
    				+"	        {	"
    				+"	          \"Supplier\": \"\",	"
    				+"	          \"CompanyCode\": \""+gsdm+"\",	"
    				+"	          \"CompanyCodeName\": \"\",	"
    				+"	          \"PaymentMethodsList\": \""+fkfs+"\",	"
    				+"	          \"PaymentTerms\": \""+fktj+"\",	"
    				+"	          \"HouseBank\": \""+HouseBank+"\",	"
    				+"	          \"ReconciliationAccount\": \""+tykm+"\",	"
    				+"	          \"LayoutSortingRule\": \"001\",	"
    				+"	          \"IsToBeCheckedForDuplicates\": true,	"
    				+"	          \"SupplierAccountGroup\": \"SUPL\"	"
    				+"	        }]},	"
    		/*		+"	        {	"
    				+"	          \"Supplier\": \"\",	"
    				+"	          \"CompanyCode\": \"4100\",	"
    				+"	          \"CompanyCodeName\": \"\",	"
    				+"	          \"PaymentMethodsList\": \"T\",	"
    				+"	          \"PaymentTerms\": \"NT00\",	"
    				+"	          \"HouseBank\": \"YX410\",	"
    				+"	          \"ReconciliationAccount\": \"21100000\",	"
    				+"	          \"LayoutSortingRule\": \"001\",	"
    				+"	          \"IsToBeCheckedForDuplicates\": true,	"
    				+"	          \"SupplierAccountGroup\": \"SUPL\"	"
    				+"	        }	"
    				+"	      ]	"
    				+"	    },	"*/
    				+"	    \"to_SupplierPurchasingOrg\": {	"
    				+"	      \"results\": [	"
    				+"	        {	"
    				+"	          \"Supplier\": \"\",	"
    				+"	          \"PurchasingOrganization\": \""+cgzz+"\",	"
    				+"	          \"InvoiceIsGoodsReceiptBased\":true,	"
    				+"	          \"PaymentTerms\": \""+fktj+"\",	"
    				+"	          \"PurchaseOrderCurrency\": \""+huobi+"\",	"
    				+"	          \"ShippingCondition\": \"\",	"
    				+"	          \"SupplierPhoneNumber\": \""+lxdh+"\",	"
    				+"	          \"SupplierRespSalesPersonName\": \""+lxr+"\",	"
    				+"	          \"SupplierAccountGroup\": \"SUPL\",	"
    				+"	          \"to_PartnerFunction\": {	"
    				+"	            \"results\": [	"
    				+"	              {	"
    				+"	                \"Supplier\": \"\",	"
    				+"	                \"PurchasingOrganization\": \""+cgzz+"\",	"
    				+"	                \"PartnerFunction\": \"VN\",	"
    				+"	                \"PartnerCounter\": \"1\"}]}}]}}}";		
    		// 获取token
    	    bb.writeLog("json:"+jsonData);
    		String result = "";
    		urlRoot =Util.null2String(Prop.getPropValue("sapsyn","sap.gy.urlRoot")); 
    		urlRoot = urlRoot.replaceAll(" ", "%20");			
    		HttpClient httpclient2 = new DefaultHttpClient();
    		HttpPost post2 = new HttpPost(urlRoot);
    		post2.setHeader("Content-Type", "application/json;charset=UTF-8");
    		post2.setHeader("Accept", "application/json");//addHeader
    		post2.setHeader("x-csrf-token",token);
    	    post2.setHeader("cookie", cookie);
    	    post2.setHeader("Authorization", code);
    		post2.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0");
    	       
    		post2.setHeader("Pragma:", "no-cache");
    		post2.setHeader("Cache-Control", "no-cache");
     
    		try {
    			 StringEntity s = new StringEntity(jsonData, "utf8");
    	         s.setContentType(new BasicHeader("Content-Type", "application/json"));
    	         post2.setEntity(s);
    	         // 发送请求
    	         HttpResponse httpResponse = httpclient2.execute(post2);
    	         // 获取响应输入流
    	         InputStream inStream = httpResponse.getEntity().getContent();
    	         BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
    	         StringBuilder strber = new StringBuilder();
    	         String line = null;
    	         while ((line = reader.readLine()) != null)
    	            strber.append(line + "\n");
    	         inStream.close();
    	         result = strber.toString();
    	           bb.writeLog("result:"+result); 
    	         if (httpResponse.getStatusLine().getStatusCode() == 201) {
    	              bb.writeLog("已成功创建供应商！");
    	              JSONObject jsonObject = JSONObject.fromObject(result);
       		       
      		       	String data=jsonObject.getString("d");
      		      
      				JSONObject jsonObject1 = JSONObject.fromObject(data);
      				String EBELN1=jsonObject1.getString("__metadata");
      			
      				JSONObject jsonObject2 = JSONObject.fromObject(EBELN1);
      				String chars=jsonObject2.getString("id");
      				
      				String gys=chars.substring(chars.indexOf("A_BusinessPartner('")+19, chars.indexOf("')"));
      				 bb.writeLog("供应商创建之后返回值:"+gys);	  
      				 String updateSalMin="update "+mainTableName+" set gysbm='"+gys+"' where requestid ="+requestid+"";
      	             rs.execute(updateSalMin);
      	         //httpResponse.getStatusLine().
      	           bb.writeLog("返回值："+httpResponse.getStatusLine().getStatusCode());
    	              
    	         } else {
    	              bb.writeLog("创建供应商失败！");
    	              JSONObject jsonObject = JSONObject.fromObject(result);
          		       
        		       	String data=jsonObject.getString("error");  
        				JSONObject jsonObject1 = JSONObject.fromObject(data);
        				String EBELN1=jsonObject1.getString("message");
    	              requestManager.setMessagecontent("请求接口报错！"+EBELN1);
       	              return Action.FAILURE_AND_CONTINUE;
    	         }
     
    		} catch (ClientProtocolException e) {
    			requestManager.setMessagecontent("异常1！"+e);
                return Action.FAILURE_AND_CONTINUE;		
    		} catch (IOException e) {			
    			requestManager.setMessagecontent("异常2！"+e);
                return Action.FAILURE_AND_CONTINUE;
    		}
      
        }catch(Exception e){
        	bb.writeLog("请求接口报错："+e,e);
            requestManager.setMessagecontent("请求接口报错,请核对信息！");
            return Action.FAILURE_AND_CONTINUE;
        }
        return Action.SUCCESS;
    }
}

