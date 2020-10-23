package weaver.interfaces.sapsyn.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetTrans;
import weaver.file.Prop;
import weaver.general.Util;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.interfaces.sapsyn.bean.Supplier;
import weaver.interfaces.sapsyn.bean.SupplierBanks;
import weaver.interfaces.sapsyn.util.CommonUtil;
import weaver.interfaces.schedule.BaseCronJob;

public class SupplierJob extends BaseCronJob
{
  private Logger logger = LoggerFactory.getLogger(SupplierJob.class);

  private String urlRoot = Util.null2String(Prop.getPropValue("sapsyn", "sap.supplier.url"));

  private String username = Util.null2String(Prop.getPropValue("sapsyn", "sap.username"));

  private String password = Util.null2String(Prop.getPropValue("sapsyn", "sap.password"));

  private String tablename = Util.null2String(Prop.getPropValue("sapsyn", "oa.supplier.tablename"));

  private String detailtablename = Util.null2String(Prop.getPropValue("sapsyn", "oa.supplier.detailtablename"));

  private String tableid = Util.null2String(Prop.getPropValue("sapsyn", "oa.supplier.id"));

  private String detailtableid = Util.null2String(Prop.getPropValue("sapsyn", "oa.supplier.detailid"));

  private Map<String,String> mainTableField = new HashMap<String,String>();

  private Map<String,String> detailTableField = new HashMap<String,String>();

  public SupplierJob() {
    this.tableid = (this.tableid.substring(0, 1).toLowerCase() + this.tableid.substring(1));
    this.detailtableid = (this.detailtableid.substring(0, 1).toLowerCase() + this.detailtableid.substring(1));
    Supplier supplierField = new Supplier();
    SupplierBanks supplierBanksField = new SupplierBanks();
    try {
      Map attrs = BeanUtils.describe(supplierField);
      Set cols = attrs.keySet();
      for (Iterator iter = cols.iterator(); iter.hasNext(); ) {
        String col = (String)iter.next();
        String val = Util.null2String(Prop.getPropValue("sapsyn", "sap.supplier." + col));
        if ((!col.equals("class")) && (!col.equalsIgnoreCase("banks"))) {
          this.mainTableField.put(col, val);
        }
      }
      Map attrs1 = BeanUtils.describe(supplierBanksField);
      Set cols1 = attrs1.keySet();
      for (Iterator iter = cols1.iterator(); iter.hasNext(); ) {
        String col = (String)iter.next();
        String val = Util.null2String(Prop.getPropValue("sapsyn", "sap.supplier.detail." + col));
        if (!col.equals("class"))
          this.detailTableField.put(col, val);
      }
    }
    catch (Exception e)
    {
      this.logger.error(">>>>>>>>>>>>>>>构造函数初始化失败！" + e, e);
    }
  }

  private boolean initParam()
  {
    boolean flag = true;
    if ((this.urlRoot.equals("")) || (this.username.equals("")) || (this.password.equals("")) || (this.tablename.equals("")) || (this.tableid.equals("")) || (this.mainTableField == null)) {
      this.logger.info("###urlRoot:" + this.urlRoot + "###username:" + this.username + "###password:" + this.password + "###tablename:" + this.tablename + "###tableid:" + this.tableid + "###supplier:" + this.mainTableField);
      flag = false;
    }
    return flag;
  }

  public void execute() {
    this.logger.info("******************************************同步供应商数据开始******************************************");
    	
        this.logger.info("计划任务开关！");
        String jhrwl = "weaver.interfaces.sapsyn.job.SupplierJob";
        RecordSet jhrwRs = new RecordSet();
        String jhrwZtSql = "select sfqy from uf_sapJhControl where jhrwl='" + jhrwl + "'";
        jhrwRs.execute(jhrwZtSql);
        if (jhrwRs.next()) {
          String sfqy = jhrwRs.getString("sfqy");
          if ("0".equals(sfqy)) {
            this.logger.info("执行计划任务！");

            this.logger.info("******************************************同步供应商数据开始******************************************");
            if (initParam())
              synSupplier();
            else {
              this.logger.error("--SupplierJob：初始化配置数据失败！");
            }
            this.logger.info("******************************************同步供应商数据结束******************************************");
          }
          else {
            this.logger.info("不执行计划任务！");
          }
        }
    this.logger.info("******************************************同步供应商数据结束******************************************");
  }

  private void synSupplier() {
    try {
      CommonUtil commonUtil = new CommonUtil();
      String url = this.urlRoot.replaceAll(" ", "%20");
      ArrayList<Supplier> supplierList = commonUtil.getDataOfSupplier(url, this.username, this.password);
      RecordSet  recordSet = new RecordSet();
      for (Supplier supplier : supplierList) {
        try {
          recordSet.executeQuery("select id from  " + this.tablename + " where " + this.tableid + " = ?", new Object[] { BeanUtils.getProperty(supplier, this.tableid) });
        }
        catch (Exception e) {
          this.logger.error(">>>>>>>>>>>>>>获取供应商唯一标识属性值异常：" + e, e);
        }
        recordSet.next();
        if (recordSet.getCounts() < 1)
          addSupplier(supplier);
        else
          updateSupplier(supplier);
      }
    }
    catch (Exception e)
    {
      this.logger.error(">>>>>>>>>>>>>>同步供应商数据失败：" + e, e);
    }
  }

  private void addSupplier(Supplier supplier) {
    try {
      Map attrs = this.mainTableField;
      Set cols = attrs.keySet();
      boolean result = true;
      RecordSet recordSet = new RecordSet();

      String sql_cols = "";
      String sql_vals = "";

      for (Iterator iter = cols.iterator(); iter.hasNext(); ) {
        String col = (String)iter.next();
        String val = (String)attrs.get(col);
        if ((!val.equals("")) && (!col.equals("class")) && (!col.equalsIgnoreCase("banks"))) {
          sql_cols = sql_cols + val + ",";
          val = BeanUtils.getProperty(supplier, col);
          sql_vals = sql_vals + "'" + val + "',";
        }
      }
      if (sql_cols.endsWith(",")) {
        sql_cols = sql_cols.substring(0, sql_cols.length() - 1);
      }
      if (sql_vals.endsWith(",")) {
        sql_vals = sql_vals.substring(0, sql_vals.length() - 1);
      }
      String sql = "insert into " + this.tablename + " (" + sql_cols + ") values (" + sql_vals + ")";
      String mainid = "-1";
      RecordSetTrans recordSetTrans = new RecordSetTrans();
      recordSetTrans.setAutoCommit(false);
      try {
        this.logger.info(">>>>>>>>>>>insert sql:" + sql);
        recordSetTrans.executeUpdate(sql, new Object[0]);
        recordSetTrans.commit();
        recordSet.executeQuery("select id from " + this.tablename + " where " + this.tableid + "=" + supplier.getSupplier(), new Object[0]);
        if (recordSet.next()) {
          mainid = recordSet.getString("id");
        }
        synSupplierBanks(supplier.getBanks(), mainid);
      } catch (Exception e) {
        recordSetTrans.rollback();
        this.logger.info(">>>>>>>>>>>>>insert sql:" + sql);
        this.logger.info("新增供应商数据报错 -执行sql异常：" + e, e);
        result = false;
      } finally {
        this.logger.info("新增供应商数据 " + BeanUtils.getProperty(supplier, this.tableid) + "-result:" + result + " -" + supplier.toString());
      }
    } catch (Exception e) {
      this.logger.info("新增供应商数据报错：" + e, e);
    }
  }

  private void updateSupplier(Supplier supplier)
  {
    try {
      Map attrs = this.mainTableField;
      Set cols = attrs.keySet();
      boolean result = true;
      RecordSet recordSet = new RecordSet();

      String sql_set = "";

      for (Iterator iter = cols.iterator(); iter.hasNext(); ) {
        String col = (String)iter.next();
        String val = (String)attrs.get(col);
        if ((!val.equals("")) && (!col.equals("class")) && (!col.equalsIgnoreCase("banks"))) {
          sql_set = sql_set + val + "='" + BeanUtils.getProperty(supplier, col) + "',";
        }
      }
      if (sql_set.endsWith(",")) {
        sql_set = sql_set.substring(0, sql_set.length() - 1);
      }
      String sql = "update " + this.tablename + " set " + sql_set + " where " + this.tableid + "='" + BeanUtils.getProperty(supplier, this.tableid) + "'";
      String mainid = "-1";
      RecordSetTrans recordSetTrans = new RecordSetTrans();
      recordSetTrans.setAutoCommit(false);
      try {
        this.logger.info(">>>>>>>>>>>>>update sql:" + sql);
        recordSetTrans.executeUpdate(sql, new Object[0]);
        recordSetTrans.commit();
        recordSet.executeQuery("select id from " + this.tablename + " where " + this.tableid + "=" + supplier.getSupplier(), new Object[0]);
        if (recordSet.next()) {
          mainid = recordSet.getString("id");
        }
        synSupplierBanks(supplier.getBanks(), mainid);
      } catch (Exception e) {
        recordSetTrans.rollback();
        this.logger.info(">>>>>>>>>>>>>update sql:" + sql);
        this.logger.info("更新供应商数据报错 -执行sql异常：" + e, e);
        result = false;
      } finally {
        this.logger.info("更新供应商数据 " + BeanUtils.getProperty(supplier, this.tableid) + "-result:" + result + " -" + supplier.toString());
      }
    } catch (Exception e) {
      this.logger.info("更新供应商数据：" + e, e);
    }
  }

  private void synSupplierBanks(List<SupplierBanks> supplierBanks, String mainid)
  {
    RecordSet recordSet = new RecordSet();
    for (SupplierBanks bank : supplierBanks) {
      try {
        recordSet.executeQuery("select id from  " + this.detailtablename + " where " + this.detailtableid + " = ? and mainid = ?", new Object[] { BeanUtils.getProperty(bank, this.detailtableid), mainid });
      }
      catch (Exception e) {
        this.logger.error(">>>>>>>>>>>>>>获取供应商银行唯一标识属性值异常：" + e, e);
      }
      recordSet.next();
      if (recordSet.getCounts() < 1)
        addSupplierBanks(bank, mainid);
      else
        updateSupplierBanks(bank, mainid);
    }
  }

  private void addSupplierBanks(SupplierBanks supplierBanks, String mainid)
  {
    try
    {
      Map attrs = this.detailTableField;
      Set cols = attrs.keySet();
      boolean result = true;

      String sql_cols = "";
      String sql_vals = "";

      for (Iterator iter = cols.iterator(); iter.hasNext(); ) {
        String col = (String)iter.next();
        String val = (String)attrs.get(col);
        if ((!val.equals("")) && (!col.equals("class"))) {
          sql_cols = sql_cols + val + ",";
          val = BeanUtils.getProperty(supplierBanks, col);
          sql_vals = sql_vals + "'" + val + "',";
        }
      }
      sql_cols = sql_cols + "mainid,";
      sql_vals = sql_vals + "'" + mainid + "',";
      if (sql_cols.endsWith(",")) {
        sql_cols = sql_cols.substring(0, sql_cols.length() - 1);
      }
      if (sql_vals.endsWith(",")) {
        sql_vals = sql_vals.substring(0, sql_vals.length() - 1);
      }
      String sql = "insert into " + this.detailtablename + " (" + sql_cols + ") values (" + sql_vals + ")";
      RecordSetTrans recordSetTrans = new RecordSetTrans();
      recordSetTrans.setAutoCommit(false);
      try {
        this.logger.info(">>>>>>>>>>>insert detail sql:" + sql);
        recordSetTrans.executeUpdate(sql, new Object[0]);
        recordSetTrans.commit();
      } catch (Exception e) {
        recordSetTrans.rollback();
        this.logger.info(">>>>>>>>>>>>>insert sql:" + sql);
        this.logger.info("新增供应商银行数据报错 -执行sql异常：" + e, e);
        result = false;
      } finally {
        this.logger.info("新增供应商银行数据 " + BeanUtils.getProperty(supplierBanks, this.detailtableid) + "-result:" + result + " -" + supplierBanks.toString());
      }
    } catch (Exception e) {
      this.logger.info("新增供应商银行数据报错：" + e, e);
    }
  }

  private void updateSupplierBanks(SupplierBanks supplierBanks, String mainid)
  {
    try {
      Map attrs = this.detailTableField;
      Set cols = attrs.keySet();
      boolean result = true;

      String sql_set = "";

      for (Iterator iter = cols.iterator(); iter.hasNext(); ) {
        String col = (String)iter.next();
        String val = (String)attrs.get(col);
        if ((!val.equals("")) && (!col.equals("class"))) {
          sql_set = sql_set + val + "='" + BeanUtils.getProperty(supplierBanks, col) + "',";
        }
      }
      if (sql_set.endsWith(",")) {
        sql_set = sql_set.substring(0, sql_set.length() - 1);
      }
      String sql = "update " + this.detailtablename + " set " + sql_set + " where " + this.detailtableid + "='" + BeanUtils.getProperty(supplierBanks, this.detailtableid) + "' and mainid = '" + mainid + "'";
      RecordSetTrans recordSetTrans = new RecordSetTrans();
      recordSetTrans.setAutoCommit(false);
      try {
        this.logger.info(">>>>>>>>>>>>>update detail sql:" + sql);
        recordSetTrans.executeUpdate(sql, new Object[0]);
        recordSetTrans.commit();
      } catch (Exception e) {
        recordSetTrans.rollback();
        this.logger.info(">>>>>>>>>>>>>update sql:" + sql);
        this.logger.info("更新供应商银行数据报错 -执行sql异常：" + e, e);
        result = false;
      } finally {
        this.logger.info("更新供应商银行数据 " + BeanUtils.getProperty(supplierBanks, this.detailtableid) + "-result:" + result + " -" + supplierBanks.toString());
      }
    } catch (Exception e) {
      this.logger.info("更新供应商银行数据：" + e, e);
    }
  }
}