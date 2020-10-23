<%@ page contentType="text/json;charset=UTF-8" language="java" %>
<jsp:useBean id="GetProductUomCds" class="msdev.jhswyy.sap.schedule.GetProductUomCds" scope="page" />
<%
    GetProductUomCds.execute();
%>
