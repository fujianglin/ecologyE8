package weaver.interfaces.sapsyn.job;

import org.apache.commons.beanutils.BeanUtils;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetTrans;
import weaver.file.Prop;

import weaver.general.Util;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.interfaces.sapsyn.bean.Product;
import weaver.interfaces.sapsyn.util.CommonUtil;
import weaver.interfaces.schedule.BaseCronJob;
import java.util.*;

//物料数据同步计划任务类
public class ProductJob extends BaseCronJob {
    //日志打印对象
    private Logger logger = LoggerFactory.getLogger(ProductJob.class);
    //物料数据接口请求url根路径
    //private String urlRoot = Util.null2String(Prop.getPropValue("sapsyn","sap.product.url"));
    //正式的地址！！！！！！！！！！！！！！！！！！
   // private String urlRoot = "https://l600119-iflmap.hcisbp.cn1.hana.ondemand.com/gw/odata/SAP/OA%20TO%20S4%20QUERYMATERIAL;v=1/YY1_PRODUCTINFO_FINAL";
    private String urlRoot = Util.null2String(Prop.getPropValue("sapsyn","sap.product.url")).replace(" ", "%20"); 
    //用户名   
    private String username = Util.null2String(Prop.getPropValue("sapsyn","sap.username"));
    //密码
    private String password = Util.null2String(Prop.getPropValue("sapsyn","sap.password"));
    //OA物料表名
    private String tablename = Util.null2String(Prop.getPropValue("sapsyn","oa.product.tablename"));
    //OA物料表唯一标识
    private String tableid = Util.null2String(Prop.getPropValue("sapsyn","oa.product.id"));
    //物料信息同步字段
    private Product productField;

    public ProductJob(){
        logger.info("URL地址测试：" + urlRoot) ;
        this.tableid = this.tableid.substring(0,1).toLowerCase() +this.tableid.substring(1);//首字母小写处理
        productField = new Product();
        try {
            Map attrs = BeanUtils.describe(productField);
            Set cols = attrs.keySet();
            String ziduanming="";//需要同步的字段名
            for (Iterator iter = cols.iterator(); iter.hasNext();){
                String col = (String) iter.next();
                String val = Util.null2String(Prop.getPropValue("sapsyn","sap.product."+col));
                if(!col.equals("class")){
                    BeanUtils.setProperty(productField,col,val);
                }
                logger.info("单个字段："+col+"对应的字段名："+val) ;
                ziduanming+=","+col;
            }
            logger.info("需要同步的数据字段信息："+ziduanming) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *配置数据是否初始化成功
     */
    public boolean initParam(){
        boolean flag = true;
        if (urlRoot.equals("") || username.equals("") || password.equals("") || tablename.equals("") || tableid.equals("") || productField == null){
            logger.info("###urlRoot:"+urlRoot+"###username:"+username+"###password:"+password+"###tablename:"+tablename+"###tableid:"+tableid+"###product:"+productField);
            flag = false;
        }
        return flag;
    }

    @Override
    public void execute() {
    	logger.info("计划任务开关！");
    	String jhrwl="weaver.interfaces.sapsyn.job.ProductJob";//计划任务类路径
    	RecordSet jhrwRs = new RecordSet();
    	String jhrwZtSql="select sfqy from uf_sapJhControl where jhrwl='"+jhrwl+"'";
    	jhrwRs.execute(jhrwZtSql);
    	if(jhrwRs.next()){
    		String sfqy = jhrwRs.getString("sfqy");//是否启用 0是启用  1否禁用
    		if("0".equals(sfqy)){//括号里面放置计划任务代码
    			logger.info("执行计划任务！");
    			
    			logger.info("******************************************同步物料数据开始******************************************");
    	        if(this.initParam()){
    	            this.synProduct();
    	        }else{
    	            logger.error("--ProductJob：初始化配置数据失败！");
    	        }
    	        logger.info("******************************************同步物料数据结束******************************************");
    			
    		}else{
    			logger.info("不执行计划任务！");
    		}
    	} 
    }

    //同步物料数据
    private void synProduct(){
        try{
            CommonUtil commonUtil = new CommonUtil();
            Map attrs = BeanUtils.describe(this.productField);
            String url = this.urlRoot.replaceAll(" ","%20");//"https://"+URLEncoder.encode(this.urlRoot.substring(8));
            ArrayList productList = commonUtil.getData(attrs, url,this.username,this.password);
            logger.info("返回值信息："+productList);
            RecordSet recordSet = new RecordSet();
            String sss ="";
            for(Object product:productList){
                try {
                	 sss =BeanUtils.getProperty((Product)product, this.tableid);
                    recordSet.executeQuery("select id from  "+this.tablename+" where "+this.tableid+" = ?", BeanUtils.getProperty((Product)product, this.tableid));
                } catch (Exception e) {
                    logger.error(">>>>>>>>>>>>>>获取物料唯一标识属性值异常："+e,e);
                }
                if(recordSet.getCounts()<1){//不存在，新增
                    this.addProduct((Product) product);
                }else{//存在，更新
                    //logger.info(">>>>>>>>>>>>>>>>>>>>存在，更新-QuantityNumerator:"+((Product) product).getQuantityNumerator());
                    this.updateProduct((Product) product);
                }
            }
        	
        	String selectSql="select id from  "+this.tablename+" where "+this.tableid+" = ?"+sss;
        	logger.info("表单id："+sss+",查询数据sql："+selectSql);
        }catch (Exception e){
            logger.error(">>>>>>>>>>>>>>同步物料数据失败："+e,e);
        }
    }

    //新增物料数据
    private void addProduct(Product product){
        try {
        	logger.info("开始新增物料数据");
            Map attrs = BeanUtils.describe(this.productField);
            Set cols = attrs.keySet();
            List<String> fieldList = new ArrayList<String>();//入库字段属性名list
            boolean result =true;

            //拼接sql
            String sql_cols = "";
            String sql_vals = "";
            //获取需要同步的数据库字段
            for (Iterator iter = cols.iterator(); iter.hasNext();){
                String col = (String) iter.next();
                logger.info("开始新增物料数据col"+col);
                String val = (String) attrs.get(col);
//                logger.info(">>>>>>>>>>>>>>>col-val->"+col+":"+val);
                if(!val.equals("") && !col.equals("class")){
                    sql_cols += val+",";
                    fieldList.add(col);
                }
            }
           // logger.info("查看需要同步的数据："+sql_cols);
            //获取同步字段值
            logger.info("开始新增物料数据fieldList"+fieldList);
            for(String field:fieldList){
                String val = "";
                val = (String)BeanUtils.getProperty(product,field);
                logger.info("新增字段名："+field+"对应的值："+val);
                if("isBatchManagementRequired".equals(field)){
                	if("true".equals(val)){
                		val="0";
                		
                	}else{
                		val="1";
                	}
                	
                }
                
                logger.info("开始新增物料数据val"+val);
                if(val!=null){
                	sql_vals = sql_vals+ "'" + val.replace("'","''") + "'" + "," ;//中存在单引号，用两个单引号做转义，sqlserver
                }else{
                	sql_vals = sql_vals+ "'" + val + "'" + "," ;
                }
                
            }
            if(sql_cols.endsWith(",")) {
                sql_cols = sql_cols.substring(0, sql_cols.length()-1);//删除逗号
            }
            if(sql_vals.endsWith(",")) {
                sql_vals = sql_vals.substring(0, sql_vals.length()-1);//删除逗号
            }
            String sql = "insert into "+this.tablename+" (" + sql_cols + ") values (" + sql_vals + ")";
            logger.info("开始新增物料数据sql"+sql);
            RecordSetTrans recordSetTrans = new RecordSetTrans();
            recordSetTrans.setAutoCommit(false);
            try {
                //logger.info(">>>>>>>>>>>>>insert sql:"+sql);
                recordSetTrans.executeUpdate(sql);
                recordSetTrans.commit();
            }catch (Exception e){
                recordSetTrans.rollback();
                logger.info(">>>>>>>>>>>>>insert sql:"+sql);
                logger.info("新增物料数据报错 -执行sql异常："+e,e);
                result = false;
            }finally {
                logger.info("新增物料数据 " + BeanUtils.getProperty(product,this.tableid) + "-result:" + result + " -" + product.toString());
            }
        }catch (Exception e){
            logger.info("新增物料数据报错："+e,e);
        }
    }

    //更新物料数据
    private void updateProduct(Product product){
        try {
            Map attrs = BeanUtils.describe(this.productField);
            Set cols = attrs.keySet();
//            List<String> fieldList = new ArrayList<String>();//入库字段属性名list
            boolean result =true;

//            logger.info(">>>>>>>>>>this.productField:"+this.productField.toString());
            //拼接sql
            String sql_set = "";
            //获取需要同步的数据库字段    这里针对数据进行转换
            for (Iterator iter = cols.iterator(); iter.hasNext();){
                String col = (String) iter.next();
                String val = (String) attrs.get(col);
                String values=BeanUtils.getProperty(product,col);
                logger.info("set值："+val+"values值："+values);
                if("IsBatchManagementRequired".equals(val)){
                	if("true".equals(values)){
                		values="0";
                		
                	}else{
                		values="1";
                	}
                	
                }
                if(!val.equals("") && !col.equals("class")){
                	if(BeanUtils.getProperty(product,col)!=null){
                		
                		sql_set += val + "='" +values.replace("'","''") + "',";//sql中存在单引号，用两个单引号做转义，sqlserver
                	}else{
                		sql_set += val + "='" + values + "',";//sql中存在单引号，用两个单引号做转义，sqlserver
                	}
                    
                }
            }
            if(sql_set.endsWith(",")) {
                sql_set = sql_set.substring(0, sql_set.length()-1);//删除逗号
            }
            logger.info("查看需要更新同步的数据："+sql_set);
          
            
            
            String sql = "update "+this.tablename+" set "+sql_set+" where "+tableid+"='"+BeanUtils.getProperty(product,tableid)+"'";
            logger.info("开始更新物料数据sql"+sql);
            RecordSetTrans recordSetTrans = new RecordSetTrans();
            recordSetTrans.setAutoCommit(false);
            try {
                //logger.info(">>>>>>>>>>>>>update sql:"+sql);
                recordSetTrans.executeUpdate(sql);
                recordSetTrans.commit();
            }catch (Exception e){
                recordSetTrans.rollback();
                logger.info(">>>>>>>>>>>>>update sql:"+sql);
                logger.info("更新物料数据报错 -执行sql异常："+e,e);
                result = false;
            }finally {
                logger.info("更新物料数据 " + BeanUtils.getProperty(product,this.tableid) + "-result:" + result + " -" + product.toString());
            }
        }catch (Exception e){
            logger.info("更新物料数据报错："+e,e);
        }
    }

//    /**
//     * 获取数据
//     * xml数据结构：
//     * <feed>
//     *     <entry>
//     *         <content>
//     *             <properties>
//     *                 <prop><prop/>
//     **/
//    public ArrayList<Product> getData(){
//        ArrayList<Product> productList = new ArrayList<Product>();
//        String data = "";//HTTPUtil.doGetWithBasicAuth(this.urlRoot,this.username,this.password);
//        Document document = XMLUtil.parseXMLStr(data);
//        //获取根节点
//        Element rootNode = XMLUtil.getRootNode(document);
//        List<Element> entryList = XMLUtil.getChildrenNodesByName(rootNode,"entry");
//        Element currentElement = null;
//        Product productObj = null;
//        String Product;
//        String ProductDescription;
//        String MaterialGroup;
//        String MaterialGroupText;
//        String UnitOfMeasure;
//        String ProductType;
//        String MaterialTypeName;
//        String CreationDate;
//        String CreatedByUser;
//        String LastChangeDate;
//        String ProductGroup;
//        String BaseUnit;
//        String Division;
//        String Language;
//        String MaterialGroupName;
//        String LastChangedByUser;
//        //遍历数据存储节点list
//        for (Element e:entryList){
//            currentElement = e.element("content");
//            currentElement = currentElement.element("properties");
//
//            Product = Util.null2String(currentElement.element("Product").getText());
//            ProductDescription = Util.null2String(currentElement.element("ProductDescription").getText());
//            MaterialGroup = Util.null2String(currentElement.element("MaterialGroup").getText());
//            MaterialGroupText = Util.null2String(currentElement.element("MaterialGroupText").getText());
//            UnitOfMeasure = Util.null2String(currentElement.element("UnitOfMeasure").getText());
//            ProductType = Util.null2String(currentElement.element("ProductType").getText());
//            MaterialTypeName = Util.null2String(currentElement.element("MaterialTypeName").getText());
//            CreationDate = Util.null2String(currentElement.element("CreationDate").getText());
//            CreatedByUser = Util.null2String(currentElement.element("CreatedByUser").getText());
//            LastChangeDate = Util.null2String(currentElement.element("LastChangeDate").getText());
//            ProductGroup = Util.null2String(currentElement.element("ProductGroup").getText());
//            BaseUnit = Util.null2String(currentElement.element("BaseUnit").getText());
//            Division = Util.null2String(currentElement.element("Division").getText());
//            Language = Util.null2String(currentElement.element("Language").getText());
//            MaterialGroupName = Util.null2String(currentElement.element("MaterialGroupName").getText());
//            LastChangedByUser = Util.null2String(currentElement.element("LastChangedByUser").getText());
//
//            productObj = new Product();
//            productObj.setProduct(Product);
//            productObj.setProductDescription(ProductDescription);
//            productObj.setMaterialGroup(MaterialGroup);
//            productObj.setMaterialGroupText(MaterialGroupText);
//            productObj.setUnitOfMeasure(UnitOfMeasure);
//            productObj.setProductType(ProductType);
//            productObj.setMaterialTypeName(MaterialTypeName);
//            productObj.setCreationDate(CreationDate);
//            productObj.setCreatedByUser(CreatedByUser);
//            productObj.setLastChangeDate(LastChangeDate);
//            productObj.setProductGroup(ProductGroup);
//            productObj.setBaseUnit(BaseUnit);
//            productObj.setDivision(Division);
//            productObj.setLanguage(Language);
//            productObj.setMaterialGroupName(MaterialGroupName);
//            productObj.setLastChangedByUser(LastChangedByUser);
//
//            productList.add(productObj);
//        }
//        return productList;
//    }
}
