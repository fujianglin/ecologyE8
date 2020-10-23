package msdev.shjh.util;


import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

public class ExcelGeneratorUtil {

	
    public File createExcel(RecordSet record,String url){
    	BaseBean bb = new BaseBean();
		String name = "temp" + (int) (Math.random() * 100000) + ".xls";//创建一个随机excel文件
		bb.writeLog("url:"+url);
		File file = new File(url);
		File file1 = new File(name);
    	
		
			
			String MATNR = "";//物料流水号
			
			String MTART = "";//物料类型
			String WERKS = "";//工厂			
			String MAKTX = "";//物料描述
			String MEINS ="";//基本计量单位
			String MATKL ="";//物料组
			String BISMT = "";//旧物料号
			String YY1_Model = "";//规格
			String YY1_ManufactureNo = "";//厂商货号
			String YY1_Manufacturer ="";//制造商
			String YY1_Longname = "";//物料全称
			String XCHPF = "";//批次管理需求的标识
			String KLART = "";//类别种类
			String CLASS_01 = "";//类号
			//新加默认
			String OBTAB ="MARA";//Name of database table for object
			String STATU ="1";//Classification status
			String PSTAT ="KCVQBGDEAX";//Material Maintenance Status
			String PSTAT1 ="KCVQBGDEAX";//Plant Maintenance Status
					
			
			String VKORG ="";//销售组织
			String VTWEG ="";//分销渠道
			String SPART = "";//产品组
			String DWERK ="";//交货工厂
			//国家
			String ALAND="CN";
			String TATY1 ="";//税类别
			String TAXKM ="";//物料的税分类
			String KTGRM = "";//科目设置组
			String MTPOS = "";//项目类别组
			String TRAGR="";//运输组
			String LADGR="";//装载组
			
			String BSTME = "";//采购订单的计量单位
			String BSTUZ = "";//有关单位向基本单位转换的分子
			String BSTUN ="";//单位到基本单位转换的分母
			String EKGRP = "";//采购组
			String KORDX = "";//标识: 源清单要求    这个可能不要 沟通一下
			String DISMM ="";//物料需求计划类型
			String DISGR = "";//MRP组
			String DISPO = "";//MRP 控制者（物料计划人）
			String DISLS = "";//批量 (物料计划)
			String BSTMI ="";//最小订单量
			String BSTMA = "";//最大订单量
			String DZEIT = "";//厂内生产时间
			String PLIFZ = "";//计划交货时间
			String WEBAZ = "";//收货处理时间
			String BSTRF ="";//舍入值（最小包装量）
			String RGEKZ = "";//反冲
			String BESKZ ="";//采购类型
			String SOBSL ="";//特殊采购类型
			String LGPRO = "";//生产仓储地点
			String LGFSB = "";//外部采购的缺省仓储位置
			String FHORI = "";//浮动的计划边际码
			String EISBE = "";//安全库存
			String STRGR = "";//计划策略组
			String VRMOD = "";//消耗模式
			
			String VINT1 = "";//消耗期间:逆向
			String VINT2 = "";//消耗时期-向前
			String MTVFP = "";//可用性检查
			String SBDKZ = "";//独立/集中
			String MINBE ="";//重订货点
			String BSTFE ="";//固定订单量
			String MABST ="";//最大库存水平
			
			
			String FEVOR = "";//生产管理员
			String SFCPF ="";//生产计划参数文件
			String ART_01 ="";//检验类型
			String AKTIV_01 = "";//激活
			String BKLAS = "";//评估分类
			String VPRSV ="";//价格控制
			String MLAST ="2";//价格确认
			String PVPRS_1 ="";//移动平均价
			
			String PEINH = "";//价格单位
			String lbzl = "";//差异码     这个值不知道SAP中没提供
			String EKALR = "";//用QS的成本估算
			String MMSTA = "";//特定工厂的物料状态
			String PRCTR ="";//利润中心
			String LOSGR = "";//成本核算批量
			//新增加
			String RAUBE = "";//存储条件
			String MHDRZ = "";//最小剩余货架寿命
			String MHDHB = "";//总货架存放期
			String IPRKZ ="";//SLED的期间标识
			String INSMK = "";//入库到质检状态
			String AWSLS ="";//这个现在没对应
			String SPRAS ="";
 			if(record.next()){
				bb.writeLog("获取数据");
				MATNR = record.getString("流水号");//物料流水号
	    		
				 MTART = record.getString("wllx");//物料类型
				 WERKS = record.getString("gc");//工厂			
				 MAKTX = record.getString("wlms");//物料描述
				 MEINS = record.getString("jbjldw");//基本计量单位
				 MATKL = record.getString("wlz");//物料组
				 BISMT = record.getString("jwlh");//旧物料号
				 YY1_Model = record.getString("guige");//规格
				 YY1_ManufactureNo = record.getString("cshh");//厂商货号
				 YY1_Manufacturer = record.getString("zzs");//制造商
				 YY1_Longname = record.getString("wlqc");//物料全称
				 XCHPF = record.getString("pcglxqdbs");//批次管理需求的标识
				 KLART = record.getString("lbzl");//类别种类
				 CLASS_01 = record.getString("leihao");//类号
				//新加默认
				 OBTAB ="MARA";//Name of database table for object
				 STATU ="1";//Classification status
				 PSTAT ="KCVQBGDEAX";//Material Maintenance Status
				 PSTAT1 ="KCVQBGDEAX";//Plant Maintenance Status
						
				
				 VKORG = record.getString("sszz");//销售组织
				 VTWEG = record.getString("fxqd");//分销渠道
				 SPART = record.getString("qpz");//产品组
				 DWERK = record.getString("jhgc");//交货工厂
				//国家
				 ALAND="CN";
				 TATY1 = record.getString("slb");//税类别
				 TAXKM = record.getString("wldsfl");//物料的税分类
				 KTGRM = record.getString("kmszz");//科目设置组
				 MTPOS = record.getString("xmlbz");//项目类别组
				 TRAGR=record.getString("ysz");;//运输组
				 LADGR=record.getString("zzz");;//装载组
				
				 BSTME = record.getString("cgdddjldw");//采购订单的计量单位
				 BSTUZ = record.getString("dwxjbdwzhfz");//有关单位向基本单位转换的分子
				 BSTUN = record.getString("dwdjbdwzhfm");//单位到基本单位转换的分母
				 EKGRP = record.getString("cgz");//采购组
				 KORDX = record.getString("bsyqdyq");//标识: 源清单要求    这个可能不要 沟通一下
				 DISMM = record.getString("wlxqjhlx");//物料需求计划类型
				 DISGR = record.getString("MRPz");//MRP组
				 DISPO = record.getString("MRPkzz");//MRP 控制者（物料计划人）
				 DISLS = record.getString("piliang");//批量 (物料计划)
				 BSTMI = record.getString("zxddl");//最小订单量
				 BSTMA = record.getString("zdddl");//最大订单量
				 DZEIT = record.getString("cnscsj");//厂内生产时间
				 PLIFZ = record.getString("nhjhsj");//计划交货时间
				 WEBAZ = record.getString("shclsj");//收货处理时间
				 BSTRF = record.getString("sheruz");//舍入值（最小包装量）
				 RGEKZ = record.getString("fanchong");//反冲
				 BESKZ = record.getString("cglx");//采购类型
				 SOBSL = record.getString("tscglx");//特殊采购类型
				 LGPRO = record.getString("sccsdd");//生产仓储地点
				 LGFSB = record.getString("wbcgdwz");//外部采购的缺省仓储位置
				 FHORI = record.getString("fddjhbjm");//浮动的计划边际码
				 EISBE = record.getString("aqkc");//安全库存
				 STRGR = record.getString("jhclz");//计划策略组
				 VRMOD = record.getString("xhms");//消耗模式
				
				 VINT1 = record.getString("xhqjnx");//消耗期间:逆向
				 VINT2 = record.getString("xhsjxq");//消耗时期-向前
				 MTVFP = record.getString("kyxjc");//可用性检查
				 SBDKZ = record.getString("dljz");//独立/集中
				 MINBE =record.getString("cdhd");//重订货点
				 BSTFE =record.getString("gdddl");//固定订单量
				 MABST =record.getString("zdkcsp");//最大库存水平
				
				
				 FEVOR = record.getString("scgly");//生产管理员
				 SFCPF = record.getString("scjhcswj");//生产计划参数文件
				 ART_01 = record.getString("jylx");//检验类型
				 AKTIV_01 = record.getString("jihuo");//激活
				 BKLAS = record.getString("pffl");//评估分类
				 VPRSV = record.getString("jgkz");//价格控制
				 MLAST ="2";//价格确认
				 PVPRS_1 = record.getString("ydpjj");//移动平均价
				
				 PEINH = record.getString("jgdw");//价格单位
				 lbzl = record.getString("chaym");//差异码     这个值不知道SAP中没提供
				 EKALR = record.getString("QScbgs");//用QS的成本估算
				 MMSTA = record.getString("tdgcdwl");//特定工厂的物料状态
				 PRCTR = record.getString("lrzx");//利润中心
				 LOSGR = record.getString("cbhspl");//成本核算批量
				//新增加
				 RAUBE = record.getString("cctj");//存储条件
				 MHDRZ = record.getString("zxsyjgsm");//最小剩余货架寿命
				 MHDHB = record.getString("zhjcfq");//总货架存放期
				 IPRKZ = record.getString("SLEDqjzt");//SLED的期间标识
				 INSMK = record.getString("rkdzzzt");//入库到质检状态	
				 
				 
			        try {
			            // Excel获得文件
			            Workbook wb = Workbook.getWorkbook(file);
			            // 打开一个文件的副本，并且指定数据写回到原文件
			        
			            WritableWorkbook book = Workbook.createWorkbook(file1,wb);
			            // 添加一个工作表
			            WritableSheet sheet = book.getSheet(2);//第3个sheet
			            sheet.addCell(new Label(0,9, MATNR));
			            sheet.addCell(new Label(1,9, SPRAS));
			            sheet.addCell(new Label(2,9, MAKTX));
			            sheet.addCell(new Label(3,9, MTART));         
			            sheet.addCell(new Label(5,9, XCHPF));
			            sheet.addCell(new Label(9,9, MATKL));
			            sheet.addCell(new Label(10,9,MEINS));
			            sheet.addCell(new Label(11,9, BISMT));
			            sheet.addCell(new Label(12,9, SPART));
			            sheet.addCell(new Label(33,9, TRAGR));
			            sheet.addCell(new Label(42,9, BSTME));
			            sheet.addCell(new Label(45,9, RAUBE));
			            sheet.addCell(new Label(52,9, MHDRZ));
			            sheet.addCell(new Label(53,9, MHDHB));
			            sheet.addCell(new Label(56,9, IPRKZ));
			            //开始执行下一个sheet页
			            WritableSheet sheet1 = book.getSheet(3);//第4个sheet
			            sheet1.addCell(new Label(0,9, MATNR));
			            sheet1.addCell(new Label(1,9, WERKS));
			            sheet1.addCell(new Label(8,9, MTVFP));
			            sheet1.addCell(new Label(11,9, WEBAZ));         
			            sheet1.addCell(new Label(12,9, LGPRO));
			            sheet1.addCell(new Label(13,9, INSMK));
			            sheet1.addCell(new Label(14,9, LADGR));
			            sheet1.addCell(new Label(18,9, PRCTR));
			            sheet1.addCell(new Label(19,9, EKGRP));
			            sheet1.addCell(new Label(22,9, KORDX));
			            sheet1.addCell(new Label(27,9, DISGR));
			            sheet1.addCell(new Label(29,9, DISMM));
			            sheet1.addCell(new Label(30,9, MINBE));
			            sheet1.addCell(new Label(33,9, DISPO));
			            sheet1.addCell(new Label(34,9, DISLS));
			            sheet1.addCell(new Label(35,9, BSTMI));
			            sheet1.addCell(new Label(36,9, BSTMA));
			            sheet1.addCell(new Label(37,9, BSTFE));
			            sheet1.addCell(new Label(38,9, MABST));
			            sheet1.addCell(new Label(42,9, BSTRF));
			            sheet1.addCell(new Label(43,9, BESKZ));
			            sheet1.addCell(new Label(44,9, SOBSL));
			            sheet1.addCell(new Label(45,9, RGEKZ));
			            sheet1.addCell(new Label(48,9, LGFSB));
			            sheet1.addCell(new Label(52,9, PLIFZ));
			            sheet1.addCell(new Label(53,9, FHORI));
			            sheet1.addCell(new Label(55,9, EISBE));
			            sheet1.addCell(new Label(65,9, STRGR));
			            sheet1.addCell(new Label(66,9, VRMOD));
			            sheet1.addCell(new Label(67,9, VINT1));
			            sheet1.addCell(new Label(68,9, VINT2));
			            sheet1.addCell(new Label(75,9, SBDKZ));
			            sheet1.addCell(new Label(85,9, SFCPF));
			            sheet1.addCell(new Label(86,9, FEVOR));
			            sheet1.addCell(new Label(93,9, DZEIT));
			            sheet1.addCell(new Label(101,9, AWSLS)); //这个没有
			            sheet1.addCell(new Label(102,9, LOSGR));
			            WritableSheet sheet2 = book.getSheet(4);//第5个sheet
			            sheet2.addCell(new Label(0,9, MATNR));
			            sheet2.addCell(new Label(1,9, BSTME));
			            sheet2.addCell(new Label(2,9, BSTUN));
			            sheet2.addCell(new Label(3,9, BSTUZ));         
			            WritableSheet sheet3 = book.getSheet(5);//第6个sheet
			            sheet3.addCell(new Label(0,9, MATNR));
			            sheet3.addCell(new Label(1,9, VKORG));
			            sheet3.addCell(new Label(2,9, VTWEG));
			            sheet3.addCell(new Label(6,9,DWERK));         
			              
			            sheet3.addCell(new Label(14,9, MTPOS));         
			            sheet3.addCell(new Label(17,9, KTGRM));         
			            WritableSheet sheet4 = book.getSheet(6);//Accounting
			            sheet4.addCell(new Label(0,9, MATNR));
			            sheet4.addCell(new Label(1,9, WERKS));
			            sheet4.addCell(new Label(4,9, BKLAS));
			            sheet4.addCell(new Label(8,9, MLAST));         
			            sheet4.addCell(new Label(9,9, VPRSV));         
			            sheet4.addCell(new Label(10,9, PVPRS_1));         
			            sheet4.addCell(new Label(12,9, PEINH));   
			            sheet4.addCell(new Label(21,9, EKALR));   
			            WritableSheet sheet5 = book.getSheet(7);//第8个sheet
			            sheet5.addCell(new Label(0,9, MATNR));
			            sheet5.addCell(new Label(1,9, WERKS));
			            sheet5.addCell(new Label(2,9, LGPRO));
			            WritableSheet sheet6 = book.getSheet(8);//第9个sheet
			            sheet6.addCell(new Label(0,9, MATNR));
			            sheet6.addCell(new Label(1,9, ALAND));
			            sheet6.addCell(new Label(2,9, TATY1));
			            sheet6.addCell(new Label(3,9, TAXKM));
			            WritableSheet sheet7 = book.getSheet(10);//第9个sheet
			            sheet7.addCell(new Label(0,9, KLART));
			            sheet7.addCell(new Label(1,9, OBTAB));
			            sheet7.addCell(new Label(2,9, CLASS_01));
			            sheet7.addCell(new Label(3,9, MATNR));
			            sheet7.addCell(new Label(4,9, STATU));
			            WritableSheet sheet8 = book.getSheet(11);//第10个sheet
			            sheet8.addCell(new Label(0,9, MATNR));
			            sheet8.addCell(new Label(1,9, WERKS));
			            sheet8.addCell(new Label(2,9, ART_01));       
			            sheet8.addCell(new Label(4,9, AKTIV_01));
			            WritableSheet sheet9 = book.getSheet(12);//第11个sheet
			            sheet9.addCell(new Label(0,9, MATNR));
			            sheet9.addCell(new Label(1,9, YY1_Model));
			            sheet9.addCell(new Label(2,9, YY1_ManufactureNo));       
			            sheet9.addCell(new Label(3,9, YY1_Manufacturer));
			            sheet9.addCell(new Label(4,9, YY1_Longname));
			            book.write();
			            book.close();
			           
			        } catch (Exception e) {
			            bb.writeLog("出现错误信息："+e);
			        }
			}
    
		
    	return file1;
    }
    
}
	
	
 
