<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1"/>
    <title>Слишком много запросов</title>
    <style>
        .center{display:flex;min-height:100vh;align-items:center;justify-content:center;background:#f4f6f8;padding:16px}
        .card{max-width:720px;background:#fff;padding:32px;border-radius:8px;box-shadow:0 10px 30px rgba(2,6,23,0.08);text-align:center}
        .title{font-size:26px;margin:0 0 8px}
        .msg{color:#333;margin:12px 0}
        .img-wrap{margin:16px 0}
        .err-img{max-width:260px;opacity:.95}
        .btn{display:inline-block;margin-top:12px;padding:10px 18px;background:#0b69ff;color:#fff;text-decoration:none;border-radius:6px}
    </style>
</head>
<body>
<div class="center">
    <div class="card">
        <h1 class="title">Слишком много запросов</h1>
        <p class="msg">
            Разрешено <strong><c:out value="${maxRequests}"/></strong> запрос(ов) за <strong><c:out value="${windowMillis / 1000}"/></strong> сек.
        </p>
        <div class="img-wrap">
            <c:choose>
                <c:when test="${not empty errorImageUrl}">
                    <img class="err-img" src="${fn:escapeXml(errorImageUrl)}" alt="Error image"/>
                </c:when>
                <c:otherwise>
                    <img class="err-img" src="${pageContext.request.contextPath}/images/img.png" alt="Error image"/>
                </c:otherwise>
            </c:choose>
        </div>
        <p class="msg">Попробуйте снова через <strong><c:out value="${retryAfterSeconds}"/></strong> сек.</p>
        <a class="btn" href="${pageContext.request.contextPath}/">На главную</a>
    </div>
</div>
</body>
</html>