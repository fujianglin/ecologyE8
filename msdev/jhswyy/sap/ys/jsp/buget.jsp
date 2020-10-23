<%@ page import="weaver.conn.RecordSet" %>
<%@ page import="weaver.general.BaseBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="weaver.general.Util" %>
<%@ page import="com.alibaba.fastjson.JSONObject" %>
<%@ page import="weaver.hrm.User" %>
<%@ page import="weaver.hrm.HrmUserVarify" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%
    BaseBean bb = new BaseBean();
    bb.writeLog("-----start msdev/jh/jsp/buget.jsp-----");
    User user2 = HrmUserVarify.getUser(request, response);
    int userDepartment = user2.getUserDepartment();
    bb.writeLog("-----msdev/jh/jsp/buget.jsp-----userDepartment =" + userDepartment);
    String[] Jurisdiction = new String[]{"28", "29"};
    RecordSet rs = new RecordSet();
    String bmStr = request.getParameter("bm");//部门
    String[] bms = bmStr.split(",");//部门数组
    String year = Util.null2String(request.getParameter("year"));//年度
    bb.writeLog("-----msdev/jh/jsp/buget.jsp-----bms =" + JSONObject.toJSONString(bms));
    bb.writeLog("-----msdev/jh/jsp/buget.jsp-----year =" + year);
    Map<String, Object> responseData = new HashMap<String, Object>();
    responseData.put("code", 0);
    try {
        Boolean isJurisdiction = false;
        for (String deptId : Jurisdiction) {
            if (String.valueOf(userDepartment).equals(deptId)) {
                isJurisdiction = true;
            }
        }
        if (bms.length > 0) {
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            int index = 0;
            for (String bm : bms) {
                if (!isJurisdiction) {
                    if (!bm.equals(String.valueOf(userDepartment))) {
                        throw new Exception("对不起！您没有权限查询其他部门！");
                    }
                }
                RecordSet rs2 = new RecordSet();
                String bmName = "";//部门名称
                String cbzx = "";//成本中心
                //根据部门id查询成本中心编码
                String querCbzxSql = "select v.*,d.departmentname from dept_view v,HrmDepartment d where v.id = d.id and v.id = '" + bm + "'";
                bb.writeLog("-----msdev/jh/jsp/buget.jsp-----querCbzxSql =" + querCbzxSql);
                rs2.execute(querCbzxSql);
                if (rs2.next()) {
                    cbzx = rs2.getString("sapcbzxx");
                    bmName = rs2.getString("departmentname");
                    bb.writeLog("-----msdev/jh/jsp/buget.jsp-----cbzx =" + cbzx);
                    String querySql = "select * from uf_buget b, uf_kmlb km where ('00'+b.GLAccount)=km.kmdm and b.CostCenter like '%" + cbzx + "%' and b.budgetyear like '%" + year + "%'";
                    bb.writeLog("-----msdev/jh/jsp/buget.jsp-----querySql =" + querySql);
                    rs.execute(querySql);
                    while (rs.next()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("idName", "chart" + index);
                        map.put("title", Util.null2String(rs.getString("kmms")) + "(" + bmName + ")");
                        Map<String, String> yyys = new HashMap<String, String>();//已用预算
                        yyys.put("value", Util.null2String(rs.getString("ActualAmountInGlobalCurrenc")));
                        yyys.put("name", "已用预算 " + yyys.get("value"));
                        Map<String, String> djys = new HashMap<String, String>();//冻结预算
                        djys.put("value", Util.null2String(rs.getString("dongjie")));
                        djys.put("name", "冻结预算 " + djys.get("value"));
                        Map<String, String> kyys = new HashMap<String, String>();//可用预算
                        kyys.put("value", Util.null2String(rs.getString("keyong")));
                        kyys.put("name", "可用预算" + kyys.get("value"));
                        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                        list.add(yyys);
                        list.add(djys);
                        list.add(kyys);
                        map.put("data", list);
                        data.add(map);
                        index++;
                    }
                } else {
                    bb.writeLog("-----msdev/jh/jsp/buget.jsp-----查不到该部门下的成本中心编码!");
                    throw new Exception("查不到该部门下的成本中心编码！");
                }
            }
            responseData.put("data", data);
        } else {
            throw new Exception("部门未选择！");
        }
    } catch (Exception e) {
        responseData.put("code", -1);
        responseData.put("message", e.getMessage());
    }
    String responseStr = JSONObject.toJSONString(responseData);
    bb.writeLog("-----end msdev/jh/jsp/buget.jsp-----");
%>
<%=responseStr%>
