<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8"/>
    <title>Проверка точки — Результат</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/style.css"/>
</head>
<body>
<header class="site-header"><h1>Результат проверки</h1></header>

<c:if test="${not empty requestScope.error}">
    <div class="error-message">${requestScope.error}</div>
    <p class="form-actions"><a class="btn" href="${pageContext.request.contextPath}/app/">Назад к форме</a></p>
</c:if>

<c:if test="${empty requestScope.error}">
    <c:choose>
        <c:when test="${not empty currentResult}">
            <c:set var="res" value="${currentResult}"/>
            <section class="card">
                <h2>Текущий результат</h2>
                <table class="table">
                    <tbody>
                    <tr><th>X</th><td>${res.request().x()}</td></tr>
                    <tr><th>Y</th><td>${res.request().y()}</td></tr>
                    <tr><th>R</th><td>${res.request().r()}</td></tr>
                    <tr><th>Попадание</th><td><c:out value="${res.hit() ? 'hit' : 'miss'}"/></td></tr>
                    <tr>
                        <th>Время пользователя</th>
                        <td>
                            <c:if test="${not empty res.request().clientTimeText()}">${res.request().clientTimeText()}</c:if>
                            <c:if test="${empty res.request().clientTimeText()}">${res.request().clientTimeMillis()}</c:if>
                        </td>
                    </tr>
                    <tr><th>Вычисление (нс)</th><td>${res.calcNanos()}</td></tr>
                    </tbody>
                </table>
            </section>
            <p class="form-actions"><a class="btn btn-primary" href="${pageContext.request.contextPath}/app/">Новый запрос</a></p>
        </c:when>
        <c:otherwise>
            <div class="placeholder">Нет данных для отображения.</div>
            <p class="form-actions"><a class="btn" href="${pageContext.request.contextPath}/app/">Назад к форме</a></p>
        </c:otherwise>
    </c:choose>
</c:if>

<footer class="site-footer"><small>&copy; 2025 Учебный проект</small></footer>
</body>
</html>