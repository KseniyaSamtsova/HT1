<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.Number"%>
<%@ page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title></title>
</head>
<body>
<%
    HashMap<String,String> jsp_parameters = new HashMap<String,String>();
    Number number = new Number();
    String fullName="";
    String error_message = "";
    if (request.getAttribute("jsp_parameters") != null)
    {
        jsp_parameters = (HashMap<String,String>)request.getAttribute("jsp_parameters");
    }
    if (request.getAttribute("number") != null)
    {
        number=(Number) request.getAttribute("number");
    }
    if (request.getAttribute("fullName") != null)
    {
        fullName=(String) request.getAttribute("fullName");
    }
    error_message = jsp_parameters.get("error_message");

%>


<form action="<%=request.getContextPath()%>/manage-number" method="post">
    <input type="hidden" name="id" value="<%=number.getId()%>"/>
    <table align="center" border="1" width="70%">
        <%
            if ((error_message != null)&&(!error_message.equals("")))
            {
        %>
        <tr>
            <td colspan="2" align="center"><span style="color:red"><%=error_message%></span></td>
        </tr>
        <%
            }
        %>
        <tr>
            <td colspan="2" align="center">Информация о телефоне владельца:
                <input align="center" type="text" name="fullName" value="<%=fullName%>"/>
                <input type="hidden" name="owner" value="<%=number.getOwner()%>"/>
            </td>
        </tr>
        <tr>
            <td>Номер:</td>
            <td><input type="text" name="number" value="<%=number.getPhoneNumber()%>"/></td>
        </tr>

        <td colspan="2" align="center">

            <input type="submit" name="<%=jsp_parameters.get("next_action")%>" value="<%=jsp_parameters.get("next_action_label")%>"  /><br/>
            <a href="<%=request.getContextPath()%>/?action=edit&id=${number.getOwner()} ">Вернуться к данным о человеке</a>
        </td>
    </table>
</form>
</body>
</html></html>