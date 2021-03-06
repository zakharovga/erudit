<!DOCTYPE html>
<html>
<head>
    <title>Erudit :: Online</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.custom.min.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/erudit.css" />"/>

    <script src="<c:url value="/resources/js/jquery-2.1.4.min.js" />"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script src="<c:url value="/resources/js/start.js" />"></script>
</head>

<body>
<nav class="navbar navbar-default">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                    data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<c:url value="/" />">Online-erudit.ru</a>
        </div>

        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li><a href="#">Правила</a></li>
            </ul>
            <form class="navbar-form navbar-left" role="search">
                <button type="submit" class="btn btn-default">Топ игроков</button>
            </form>
            <ul class="nav navbar-nav navbar-right">
                <c:choose>
                    <c:when test="${user.guest}">
                        <li><p class="navbar-text"><span class="glyphicon glyphicon-user"></span> ${user.username}</p></li>
                    </c:when>
                    <c:otherwise>
                        <li><p class="nav navbar-text"><span class="glyphicon glyphicon-user"></span> ${user.username}</p></li>
                        <li><a href="<c:url value="/login"><c:param name="logout" /></c:url>"><span class="glyphicon glyphicon-log-out"></span> Выйти</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

<div class="container">
    <div class="jumbotron">
        <h1>Online-erudit.ru</h1>
        <p>Создайте игру или присоединитесь к уже созданной.</p>
        <div>
            <a href="#" id="create-game-btn" class="btn btn-primary btn-lg btn-block">Создать игру</a>
            <br/>
            <div class="game-list">
                <c:choose>
                    <c:when test="${fn:length(pendingGames) == 0}">
                        <i>В настоящий момент не создано ни одной игры.</i>
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
            <nav>
                <ul id="pager" class="pager">
                    <li class="previous disabled"><a href="#"><span aria-hidden="true">&larr;</span> Предыдущие</a></li>
                    <li class="next disabled"><a href="#">Следующие <span aria-hidden="true">&rarr;</span></a></li>
                </ul>
            </nav>
        </div>
    </div>
    <div id="modal-error" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h3>Соединение закрыто</h3>
                </div>
                <div class="modal-body" id="modal-error-body"></div>
                <div class="modal-footer">
                    <button class="btn btn-primary" data-dismiss="modal">OK</button>
                </div>
            </div>
        </div>
    </div>

    <div id="modal-waiting" class="modal" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h3><span class="glyphicon glyphicon-time"></span>Загрузка...</h3>
                </div>
                <div class="modal-body" id="modal-waiting-body">
                    <div class="progress progress-striped active">
                        <div class="progress-bar" style="width: 100%"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="modal-players" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header"><h3>Ожидание игроков...</h3></div>
                <div class="modal-body" id="modal-players-body">
                    <div id="players-container-modal" class="panel-group">
                        <div id="players-bg">
                            <div class="player-bg">ИГРОК 1</div>
                            <div class="player-bg">ИГРОК 2</div>
                            <div class="player-bg">ИГРОК 3</div>
                            <div class="player-bg">ИГРОК 4</div>
                        </div>
                        <div id="player-modal" class="panel panel-default">
                            <div class="panel-body empty-player-place">
                                <div class="row vertical-align">
                                    <div class="col-lg-3">
                                        <div class="user-icon">
                                            <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                        </div>
                                        <div class="rating">
                                            <span class="glyphicon glyphicon-star" aria-hidden="true"></span><span id="player-rating"></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-10">
                                        <span id="player-name-modal" class="player-name"></span>
                                    </div>
                                    <div class="col-lg-2">
                                        <div>
                                            <span id="player-status" class="status status-not-ready glyphicon glyphicon-remove" aria-hidden="true"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div id="opponent0-modal" class="opponent-modal panel panel-default">
                            <div class="panel-body empty-player-place">
                                <div class="row vertical-align">
                                    <div class="col-lg-3">
                                        <div class="user-icon">
                                            <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                        </div>
                                        <div class="rating">
                                            <span class="glyphicon glyphicon-star" aria-hidden="true"></span><span id="opponent0-rating"></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-10">
                                        <span id="opponent0-name-modal" class="player-name"></span>
                                    </div>
                                    <div class="col-lg-2">
                                        <div>
                                            <span id="opponent0-status" class="status status-not-ready glyphicon glyphicon-remove" aria-hidden="true"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div id="opponent1-modal" class="opponent-modal panel panel-default">
                            <div class="panel-body empty-player-place">
                                <div class="row vertical-align">
                                    <div class="col-lg-3">
                                        <div class="user-icon">
                                            <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                        </div>
                                        <div class="rating">
                                            <span class="glyphicon glyphicon-star" aria-hidden="true"></span><span id="opponent1-rating"></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-10">
                                        <span id="opponent1-name-modal" class="player-name"></span>
                                    </div>
                                    <div class="col-lg-2">
                                        <div>
                                            <span id="opponent1-status" class="status status-not-ready glyphicon glyphicon-remove" aria-hidden="true"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div id="opponent2-modal" class="opponent-modal panel panel-default">
                            <div class="panel-body empty-player-place">
                                <div class="row vertical-align">
                                    <div class="col-lg-3">
                                        <div class="user-icon">
                                            <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                        </div>
                                        <div class="rating">
                                            <span class="glyphicon glyphicon-star" aria-hidden="true"></span><span id="opponent2-rating"></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-10">
                                        <span id="opponent2-name-modal" class="player-name"></span>
                                    </div>
                                    <div class="col-lg-2">
                                        <div>
                                            <span id="opponent2-status" class="status status-not-ready glyphicon glyphicon-remove" aria-hidden="true"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">

                    <div id="ready-radio" class="btn-group" data-toggle="buttons">
                        <label id="ready-label" class="btn btn-primary">
                            <input type="radio" name="options" id="ready-input" autocomplete="off"><span class="glyphicon glyphicon-ok"></span> Готов
                        </label>
                        <label id="not-ready-label" class="btn btn-primary active">
                            <input type="radio" name="options" id="not-ready-input" autocomplete="off" checked><span class="glyphicon glyphicon-remove"></span> Не готов
                        </label>
                    </div>

                    <button id="cancel" type="button" class="btn btn-default">Отмена</button>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>