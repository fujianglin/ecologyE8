package msdev.shjh.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import msdev.shjh.util.ExcelGeneratorUtil;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.TimeUtil;

public class SaveExcelService extends HttpServlet   {

	
	private static final long serialVersionUID = 1L;

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String requestid = request.getParameter("requestid");
		
		BaseBean bb = new BaseBean();
		String fileName = "";
		String pgrq = "";
		bb.writeLog("进入日志获取到的requestid是："+requestid);
		
		String url1=request.getSession().getServletContext().getRealPath("/");
		bb.writeLog("url1"+url1);
	
		String url=url1+"msdev/materiel/template/SAP物料主数据_OA导出模板 V1.1.xls";
		String ur2=request.getSession().getServletContext().getRealPath("SAP物料主数据_OA导出模板 V1.1.xls");
		String ur3=request.getSession().getServletContext().getRealPath("msdev/materiel/template/SAP物料主数据_OA导出模板 V1.1.xls");
		bb.writeLog("ur2"+ur2+"ur3:"+ur3);
		bb.writeLog("这里没出错");
		String sql="select * from  formtable_main_87 where  requestId='"+requestid+"' ";//获取表单中的数据
		RecordSet record = new RecordSet();
		record.execute(sql);
		
		if(record.next()){
			bb.writeLog("获取到数据");
			fileName = record.getString("bt");
			pgrq = record.getString("wlms");
			bb.writeLog("标题："+fileName+"sql语句:"+sql);
			//pgrq = pgrq.replaceAll("-", "");
			record.beforFirst();
		}
		bb.writeLog("没有获取到数据！执行sql"+sql);
		File file = null;
		InputStream fin = null;
		ServletOutputStream out = null;
		
		try {
			bb.writeLog("进入try");
			fileName += "上海嘉和导出excel测试_"+pgrq;
			//  
			ExcelGeneratorUtil excelGeneratorUtil = new ExcelGeneratorUtil();
			
			file = excelGeneratorUtil.createExcel(record,url);//获取到 excel文件
			fin = new FileInputStream(file);
			
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/vnd.ms-excel");
			
			response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(fileName+".xls","UTF-8"));
			
			out = response.getOutputStream();
			byte[] buffer = new byte[512];	//  
			int bytesToRead = -1;
			// 通过循环将读入的Excel文件的内容输出到浏览器中
			while((bytesToRead = fin.read(buffer)) != -1) {
				out.write(buffer, 0, bytesToRead);
			}
		} finally {
			if(fin != null) fin.close();
			if(out != null) out.close();
			if(file != null) file.delete();	//删除临时文件
		}
		
		
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
