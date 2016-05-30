<%--
  Created by IntelliJ IDEA.
  User: zakharov_ga
  Date: 30.05.2016
  Time: 13:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
    <b>Ожидающие игры:</b>
    <div class="game-list">
        <c:choose>
            <c:when test="${fn:length(pendingGames) == 0}">
                <i>В настоящий момент нет ни одной ожидающей игры.</i>
            </c:when>
            <c:otherwise>
                <c:forEach items="${pendingGames}" var="e">
                    <div gameId="${e.key}" numPlayers="${e.value.size()}" class="pending-game-div panel panel-default">
                        <div class="panel-body empty-player-place">
                            <div class="row vertical-align">
                                <div class="col-lg-3">
                                    <div class="pending-game-user-icon">
                                        <span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span><span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span><span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span><span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span>
                                    </div>
                                </div>
                                <div class="col-lg-12">
                                    <i>Создал:</i><span class="player-name">${e.value.creator}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <br>
    <b>Активные игры:</b>
    <div class="game-list">
        <c:choose>
            <c:when test="${fn:length(activeGames) == 0}">
                <i>В настоящий момент нет ни одной активной игры.</i>
            </c:when>
            <c:otherwise>
                <c:forEach items="${activeGames}" var="e">
                    <div gameId="${e.key}" numPlayers="${e.value.size()}" class="pending-game-div panel panel-default">
                        <div class="panel-body empty-player-place">
                            <div class="row vertical-align">
                                <div class="col-lg-3">
                                    <div class="pending-game-user-icon">
                                        <span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span><span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span><span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span><span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span>
                                    </div>
                                </div>
                                <div class="col-lg-12">
                                    <i>Создал:</i><span class="player-name">${e.value.creator}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <br>
    <b>Стартующие игры:</b>
    <div class="game-list">
        <c:choose>
            <c:when test="${fn:length(redirectingGames) == 0}">
                <i>В настоящий момент нет ни одной стартующей игры.</i>
            </c:when>
            <c:otherwise>
                <c:forEach items="${redirectingGames}" var="e">
                    <div gameId="${e.key}" numPlayers="${e.value.size()}" class="pending-game-div panel panel-default">
                        <div class="panel-body empty-player-place">
                            <div class="row vertical-align">
                                <div class="col-lg-3">
                                    <div class="pending-game-user-icon">
                                        <span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span><span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span><span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span><span class="glyphicon glyphicon-user free-icon" aria-hidden="true"></span>
                                    </div>
                                </div>
                                <div class="col-lg-12">
                                    <i>Создал:</i><span class="player-name">${e.value.creator}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <br>
    <b>Соответствие Вебсокет-сессия - игра</b>
    <div class="sessions-div">
        <c:choose>
            <c:when test="${fn:length(allSessions) == 0}">
                <i>В настоящий нет ни одной Вебсокет-сессии</i>
            </c:when>
            <c:otherwise>
                <c:forEach items="${allSessions}" var="e">
                    <div class="panel-body empty-player-place">
                        <div class="row vertical-align">
                            <div class="col-lg-12">
                                <i>WsSession ID: ${e.key.id}</i><span> Game ID: ${e.value.gameId}</span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <br>
    <b>Соответствие Вебсокет-сессия - Http-сессия</b>
    <div class="sessions-div">
        <c:choose>
            <c:when test="${fn:length(sessions) == 0}">
                <i>В настоящий нет ни одной Вебсокет-сессии</i>
            </c:when>
            <c:otherwise>
                <c:forEach items="${sessions}" var="e">
                    <div class="panel-body empty-player-place">
                        <div class="row vertical-align">
                            <div class="col-lg-12">
                                <i>WsSession ID: ${e.key.id}</i><span> HTTP Session ID: ${e.value.id}</span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
