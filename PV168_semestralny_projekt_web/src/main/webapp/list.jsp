<%-- 
    Document   : list
    Created on : Apr 21, 2013, 11:30:00 AM
    Author     : Martin Otahal
--%>

<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <body>
        <h1>Groups!</h1>
        <table border="1">
            <thead>
            <tr>
                <th>Group type</th>
                <th>Note</th>
            </tr>
            </thead>
            <c:forEach items="${groups}" var="group">
                <tr>
                    <td><c:out value="${group.type}"/></td>
                    <td><c:out value="${group.note}"/></td>
                    <td><form method="post" action="${pageContext.request.contextPath}/groups/delete?id=${group.id}"
                              style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
                </tr>
            </c:forEach>
        </table>

        <h2>Input group</h2>
        <c:if test="${not empty chyba}">
            <div style="border: solid 1px red; background-color: yellow; padding: 10px">
                <c:out value="${chyba}"/>
            </div>
        </c:if>
        <form action="${pageContext.request.contextPath}/groups/add" method="post">
            <table>
                <tr>
                    <th>Group type:</th>
                    <td><input type="text" name="type" value="<c:out value='${param.type}'/>"/></td>
                </tr>
                <tr>
                    <th>Note:</th>
                    <td><input type="text" name="note" value="<c:out value='${param.note}'/>"/></td>
                </tr>
            </table>
            <input type="Submit" value="Zadat" />
        </form>
    </body>
</html>
