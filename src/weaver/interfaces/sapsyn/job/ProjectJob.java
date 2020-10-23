package weaver.interfaces.sapsyn.job;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetTrans;
import weaver.file.Prop;
import weaver.general.Util;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.integration.util.XMLUtil;
import weaver.interfaces.sapsyn.bean.Product;
import weaver.interfaces.sapsyn.bean.Project;
import weaver.interfaces.sapsyn.util.CommonUtil;
import weaver.interfaces.schedule.BaseCronJob;

import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.*;

public class ProjectJob extends BaseCronJob {
    //日志打印对象
    private Logger logger = LoggerFactory.getLogger(ProductJob.class);
    //项目数据接口请求url根路径
    private String urlRoot = Util.null2String(Prop.getPropValue("sapsyn","sap.project.url"));
    //用户名
    private String username = Util.null2String(Prop.getPropValue("sapsyn","sap.username"));
    //密码
    private String password = Util.null2String(Prop.getPropValue("sapsyn","sap.password"));
    //OA项目表名
    private String tablename = Util.null2String(Prop.getPropValue("sapsyn","oa.project.tablename"));
    //OA项目表唯一标识
    private String tableid = Util.null2String(Prop.getPropValue("sapsyn","oa.project.id"));
    //项目信息同步字段
    private Project projectField;

    public ProjectJob(){
        this.tableid = this.tableid.substring(0,1).toLowerCase() +this.tableid.substring(1);//首字母小写处理
        projectField = new Project();
        try {
            Map attrs = BeanUtils.describe(projectField);
            Set cols = attrs.keySet();
            for (Iterator iter = cols.iterator(); iter.hasNext();){
                String col = (String) iter.next();
                String val = Util.null2String(Prop.getPropValue("sapsyn","sap.project."+col));
                if(!col.equals("class")) {
                    BeanUtils.setProperty(projectField, col, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *配置数据是否初始化成功
     */
    private boolean initParam(){
        boolean flag = true;
        if (urlRoot.equals("") || username.equals("") || password.equals("") || tablename.equals("") || tableid.equals("") || projectField == null){
            logger.info("###urlRoot:"+urlRoot+"###username:"+username+"###password:"+password+"###tablename:"+tablename+"###tableid:"+tableid+"###project:"+projectField);
            flag = false;
        }
        return flag;
    }
    @Override
    public void execute() {
        logger.info("******************************************同步项目数据开始******************************************");
        if(this.initParam()){
            this.synProduct();
        }else{
            logger.error("--ProjectJob：初始化配置数据失败！");
        }
        logger.info("******************************************同步项目数据结束******************************************");
    }
    //同步项目数据
    private void synProduct(){
        try{
            CommonUtil commonUtil = new CommonUtil();
            Map attrs = BeanUtils.describe(this.projectField);
            String url = this.urlRoot.replaceAll(" ","%20");//"https://"+URLEncoder.encode(this.urlRoot.substring(8));
            ArrayList projectList = commonUtil.getData(attrs,url,this.username,this.password);
            RecordSet recordSet = new RecordSet();
            for(Object project:projectList){
                try {
                    recordSet.executeQuery("select id from  "+this.tablename+" where "+this.tableid+" = ?", BeanUtils.getProperty((Product) project,this.tableid));
                } catch (Exception e) {
                    logger.error(">>>>>>>>>>>>>>获取项目唯一标识属性值异常："+e,e);
                }
                if(recordSet.getCounts()<1){//不存在，新增
                    this.addProduct((Product) project);
                }else{//存在，更新
                    this.updateProduct((Product) project);
                }
            }
        }catch (Exception e){
            logger.error(">>>>>>>>>>>>>>同步项目数据失败："+e,e);
        }
    }
    //新增项目数据
    private void addProduct(Product project){
        try {
            Map attrs = BeanUtils.describe(this.projectField);
            Set cols = attrs.keySet();
            boolean result =true;

            //拼接sql
            String sql_cols = "";
            String sql_vals = "";
            //获取需要同步的数据库字段
            for (Iterator iter = cols.iterator(); iter.hasNext();){
                String col = (String) iter.next();
                String val = (String) attrs.get(col);
                if(!val.equals("") && !col.equals("class")){
                    sql_cols += val+",";
                    val = (String)BeanUtils.getProperty(project,col);//获取同步字段值
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
            RecordSetTrans recordSetTrans = new RecordSetTrans();
            recordSetTrans.setAutoCommit(false);
            try {
                recordSetTrans.executeUpdate(sql);
                recordSetTrans.commit();
            }catch (Exception e){
                recordSetTrans.rollback();
                logger.info(">>>>>>>>>>>>>insert sql:"+sql);
                logger.info("新增项目数据报错 -执行sql异常："+e,e);
                result = false;
            }finally {
                logger.info("新增项目数据 " + BeanUtils.getProperty(project,this.tableid) + "-result:" + result + " -" + project.toString());
            }
        }catch (Exception e){
            logger.info("新增项目数据报错："+e,e);
        }
    }

    //更新项目数据
    private void updateProduct(Product product){
        try {
            Map attrs = BeanUtils.describe(this.projectField);
            Set cols = attrs.keySet();
            boolean result =true;

            //拼接sql
            String sql_set = "";
            //获取需要同步的数据库字段
            for (Iterator iter = cols.iterator(); iter.hasNext();){
                String col = (String) iter.next();
                String val = (String) attrs.get(col);
                if(!val.equals("") && !col.equals("class")){
                    sql_set += val + "='" + BeanUtils.getProperty(product,col) + "',";
                }
            }
            if(sql_set.endsWith(",")) {
                sql_set = sql_set.substring(0, sql_set.length()-1);//删除逗号
            }
            String sql = "update "+this.tablename+" set "+sql_set+" where "+tableid+"='"+BeanUtils.getProperty(product,tableid)+"'";
            RecordSetTrans recordSetTrans = new RecordSetTrans();
            recordSetTrans.setAutoCommit(false);
            try {
                recordSetTrans.executeUpdate(sql);
                recordSetTrans.commit();
            }catch (Exception e){
                recordSetTrans.rollback();
                logger.info(">>>>>>>>>>>>>update sql:"+sql);
                logger.info("更新项目数据报错 -执行sql异常："+e,e);
                result = false;
            }finally {
                logger.info("更新项目数据 " + BeanUtils.getProperty(product,this.tableid) + "-result:" + result + " -" + product.toString());
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
//    public ArrayList<Project> getData(){
//        ArrayList<Project> productList = new ArrayList<Project>();
//        try{
//            String data = "";//HTTPUtil.doGetWithBasicAuth(this.urlRoot,this.username,this.password);
//            Document document = XMLUtil.parseXMLStr(data);
//            //获取根节点
//            Element rootNode = XMLUtil.getRootNode(document);
//            List<Element> entryList = XMLUtil.getChildrenNodesByName(rootNode,"entry");
//            Element currentElement = null;
//            Project projectObj = null;
//
//            //遍历数据存储节点list
//            for (Element e:entryList){
//                currentElement = e.element("content");
//                currentElement = currentElement.element("properties");
//                projectObj = new Project();
//                Map attrs = BeanUtils.describe(this.projectField);
//                Set cols = attrs.keySet();
//                for (Iterator iter = cols.iterator(); iter.hasNext();){
//                    String col = (String) iter.next();
//                    String val = Util.null2String(currentElement.element(col).getText());
//                    BeanUtils.setProperty(projectObj,col,val);
//                }
//                productList.add(projectObj);
//            }
//        }catch (Exception e){
//            logger.error(">>>>>>>>>>>>>>>-getData-获取数据报错："+e,e);
//        }
//        return productList;
//    }
}
