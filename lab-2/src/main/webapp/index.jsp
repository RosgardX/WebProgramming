<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8"/>
    <title>Проверка точки — Форма</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/style.css"/>
</head>
<body>
<header class="site-header">
    <h1>Проверка попадания точки в область</h1>
    <div class="header-meta">
        <span>Студент: <strong>Гришин Артем Евгеньевич</strong></span>
        <span>ИСУ: <strong>465657</strong></span>
        <span>Вариант: <strong>73745</strong></span>
    </div>
</header>

<c:if test="${not empty sessionScope.flash}">
    <div class="flash">${sessionScope.flash}</div>
    <c:remove var="flash" scope="session"/>
</c:if>

<c:if test="${not empty requestScope.error}">
    <div class="error-message">${requestScope.error}</div>
</c:if>

<main class="card">
    <form id="point-form" method="get" action="${pageContext.request.contextPath}/app/" class="form">
        <input type="hidden" id="xHidden" name="x"/>
        <input type="hidden" id="clientTimeMillis" name="clientTimeMillis"/>
        <input type="hidden" id="clientTimeText" name="clientTimeText"/>

        <fieldset>
            <legend>X (выберите одно значение или кликните на график)</legend>
            <div class="options-row">
                <c:forTokens var="vx" items="-3,-2,-1,0,1,2,3,4,5" delims=",">
                    <label class="opt">
                        <input type="checkbox" name="xChoice" value="${vx}"/>
                        <span>${vx}</span>
                    </label>
                </c:forTokens>
            </div>
            <div class="field-error" id="xError"></div>
        </fieldset>

        <fieldset>
            <legend>Y (-5 < Y < 5)</legend>
            <div class="form-group">
                <input id="yInput" name="y" type="text" placeholder="Например: 1.25" autocomplete="off"/>
            </div>
            <div class="hint">Дробная часть через точку или запятую</div>
            <div class="field-error" id="yError"></div>
        </fieldset>

        <fieldset>
            <legend>R (радиус)</legend>
            <div class="options-row">
                <c:forTokens var="vr" items="1,1.5,2,2.5,3" delims=",">
                    <label class="opt">
                        <input type="checkbox" name="r" value="${vr}"/>
                        <span>${vr}</span>
                    </label>
                </c:forTokens>
            </div>
            <div class="field-error" id="rError"></div>
        </fieldset>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Проверить</button>
            <a class="btn btn-danger" href="${pageContext.request.contextPath}/app/clear" onclick="return confirm('Очистить всю историю?');">Очистить историю</a>
        </div>
    </form>
</main>

<section class="card">
    <h2>График области</h2>
    <div class="graph-container">
        <canvas id="areaCanvas" width="360" height="360"></canvas>
    </div>
</section>

<section class="card">
    <h2>История проверок</h2>

    <c:set var="history" value="${applicationScope.hitResultRepository.history}" />
    <c:if test="${empty history}">
        <div class="placeholder">История пуста.</div>
    </c:if>

    <c:if test="${not empty history}">
        <c:set var="n" value="${fn:length(history)}"/>
        <div class="table-container">
            <table class="table" id="results-table">
                <thead>
                <tr>
                    <th>X</th><th>Y</th><th>R</th><th>Попадание</th><th>Время пользователя</th><th>Вычисление (нс)</th>
                </tr>
                </thead>
                <tbody>
                <c:if test="${n gt 0}">
                    <c:forEach var="i" begin="0" end="${n - 1}">
                        <c:set var="row" value="${history[n - 1 - i]}"/>
                        <tr class="${row.hit() ? 'hit-row' : 'miss-row'}">
                            <td>${row.request().x()}</td>
                            <td>${row.request().y()}</td>
                            <td>${row.request().r()}</td>
                            <td><c:out value="${row.hit() ? 'hit' : 'miss'}"/></td>
                            <td>
                                <c:if test="${not empty row.request().clientTimeText()}">${row.request().clientTimeText()}</c:if>
                                <c:if test="${empty row.request().clientTimeText()}">${row.request().clientTimeMillis()}</c:if>
                            </td>
                            <td>${row.calcNanos()}</td>
                        </tr>
                    </c:forEach>
                </c:if>
                </tbody>
            </table>
        </div>
    </c:if>
</section>

<footer class="site-footer">
    <small>&copy; 2025 Учебный проект</small>
</footer>

<script defer src="${pageContext.request.contextPath}/JS/app.js"></script>
</body>
</html>