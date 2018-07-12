<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.Person"%>
<%@ page session="true"%>
<%@ page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Управление данными о человеке</title>
</head>
<body>

<%
    HashMap<String,String> jsp_parameters = new HashMap<String,String>();
    HashMap<String,String> jsp_parameters_session = new HashMap<String,String>();
    Person person = new Person();
    String error_message = "";
    String user_message = "";
    if (session.getAttribute("jsp_parameters") != null)
    {
        jsp_parameters_session = (HashMap<String,String>)session.getAttribute("jsp_parameters");
        session.removeAttribute("jsp_parameters");
        jsp_parameters=(HashMap<String,String>)request.getAttribute("jsp_parameters");
    }
    if (request.getAttribute("person") != null)
    {
        person=(Person)request.getAttribute("person");
    }
    user_message = jsp_parameters_session.get("current_action_result_label");
    error_message = jsp_parameters.get("error_message");
%>

<form action="<%=request.getContextPath()%>/" method="post">
    <input type="hidden" name="id" value="<%=person.getId()%>"/>
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
        <%
            if ((user_message != null)&&(!user_message.equals("")))
            {
        %>
        <tr>
            <td colspan="6" align="center"><%=user_message%></td>
        </tr>
        <%
            }
        %>
        <tr>
            <td colspan="2" align="center">Информация о человеке</td>
        </tr>
        <tr>
            <td>Фамилия:</td>
            <td><input type="text" name="surname" value="<%=person.getSurname()%>"/></td>
        </tr>
        <tr>
            <td>Имя:</td>
            <td><input type="text" name="name" value="<%=person.getName()%>"/></td>
        </tr>
        <tr>
            <td>Отчество:</td>
            <td><input type="text" name="middlename" value="<%=person.getMiddlename()%>"/></td>
        </tr>
        <tr>
            <td>Телефоны:</td>
            <td>
                <c:forEach items="${person.getPhones()}" var="entry">
                    ${entry.value}
                    <input type="hidden" name="owner" value="${entry.key}"/>
                    <a href="<%=request.getContextPath()%>/manage-number?action=edit&id=${entry.key}&owner=${person.getId()} ">Редактировать</a>
                    <a href="<%=request.getContextPath()%>/manage-number?action=delete&id=${entry.key}&owner=${person.getId()}">Удалить</a><br>
                </c:forEach>
                <a href="<%=request.getContextPath()%>/manage-number?action=add&id=${entry.key}&owner=${person.getId()}">Добавить</a>
            </td>

        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="submit" name="edit_go" value="Сохранить" />
                <a href="<%=jsp_parameters.get("next_action")%>" value="<%=jsp_parameters.get("next_action_label")%>">Вернуться к списку</a>

            </td>
        </tr>
    </table>
</form>
</body>
</html>