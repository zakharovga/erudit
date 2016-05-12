<!DOCTYPE html>
<html>
<head>
    <title>Erudit</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/erudit.css" />"/>

    <script src="<c:url value="/resources/js/jquery-2.1.4.min.js" />"></script>
    <script src="<c:url value="/resources/js/jquery-ui.min.js" />"></script>
    <script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
    <script src="<c:url value="/resources/js/game.js" />"></script>
</head>
<body id="game-body">
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                    data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<c:url value="/" />">Erudit.ru</a>
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
    <div id="game-container" class="row">
        <div id="left-side" class="col-lg-3">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div id="status">
						<span id="timer" class="badge">01:00</span>
						<span id="next-turn"></span>
					</div>
                </div>
            </div>
            <div id="words-panel" class="panel panel-default">
                <div>
                    <ul id="words" class="list-group">
                    </ul>
                </div>
                <nav>
                    <ul id="pager" class="pager">
                        <li class="previous disabled"><a href="#"><span aria-hidden="true">&larr;</span></a></li>
                        <li class="next disabled"><a href="#"><span aria-hidden="true">&rarr;</span></a></li>
                    </ul>
                </nav>
            </div>
        </div>
        <div id="game-col" class="col-lg-9">
            <div id="board">
                <div class="board-row">
                    <div id="r0c0" class="game-cell red-cell"></div>
                    <div id="r0c1" class="game-cell"></div>
                    <div id="r0c2" class="game-cell"></div>
                    <div id="r0c3" class="game-cell green-cell"></div>
                    <div id="r0c4" class="game-cell"></div>
                    <div id="r0c5" class="game-cell"></div>
                    <div id="r0c6" class="game-cell"></div>
                    <div id="r0c7" class="game-cell red-cell"></div>
                    <div id="r0c8" class="game-cell"></div>
                    <div id="r0c9" class="game-cell"></div>
                    <div id="r0c10" class="game-cell"></div>
                    <div id="r0c11" class="game-cell green-cell"></div>
                    <div id="r0c12" class="game-cell"></div>
                    <div id="r0c13" class="game-cell"></div>
                    <div id="r0c14" class="game-cell red-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r1c0" class="game-cell"></div>
                    <div id="r1c1" class="game-cell blue-cell"></div>
                    <div id="r1c2" class="game-cell"></div>
                    <div id="r1c3" class="game-cell"></div>
                    <div id="r1c4" class="game-cell"></div>
                    <div id="r1c5" class="game-cell yellow-cell"></div>
                    <div id="r1c6" class="game-cell"></div>
                    <div id="r1c7" class="game-cell"></div>
                    <div id="r1c8" class="game-cell"></div>
                    <div id="r1c9" class="game-cell yellow-cell"></div>
                    <div id="r1c10" class="game-cell"></div>
                    <div id="r1c11" class="game-cell"></div>
                    <div id="r1c12" class="game-cell"></div>
                    <div id="r1c13" class="game-cell blue-cell"></div>
                    <div id="r1c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r2c0" class="game-cell"></div>
                    <div id="r2c1" class="game-cell"></div>
                    <div id="r2c2" class="game-cell blue-cell"></div>
                    <div id="r2c3" class="game-cell"></div>
                    <div id="r2c4" class="game-cell"></div>
                    <div id="r2c5" class="game-cell"></div>
                    <div id="r2c6" class="game-cell green-cell"></div>
                    <div id="r2c7" class="game-cell"></div>
                    <div id="r2c8" class="game-cell green-cell"></div>
                    <div id="r2c9" class="game-cell"></div>
                    <div id="r2c10" class="game-cell"></div>
                    <div id="r2c11" class="game-cell"></div>
                    <div id="r2c12" class="game-cell blue-cell"></div>
                    <div id="r2c13" class="game-cell"></div>
                    <div id="r2c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r3c0" class="game-cell green-cell"></div>
                    <div id="r3c1" class="game-cell"></div>
                    <div id="r3c2" class="game-cell"></div>
                    <div id="r3c3" class="game-cell blue-cell"></div>
                    <div id="r3c4" class="game-cell"></div>
                    <div id="r3c5" class="game-cell"></div>
                    <div id="r3c6" class="game-cell"></div>
                    <div id="r3c7" class="game-cell green-cell"></div>
                    <div id="r3c8" class="game-cell"></div>
                    <div id="r3c9" class="game-cell"></div>
                    <div id="r3c10" class="game-cell"></div>
                    <div id="r3c11" class="game-cell blue-cell"></div>
                    <div id="r3c12" class="game-cell"></div>
                    <div id="r3c13" class="game-cell"></div>
                    <div id="r3c14" class="game-cell green-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r4c0" class="game-cell"></div>
                    <div id="r4c1" class="game-cell"></div>
                    <div id="r4c2" class="game-cell"></div>
                    <div id="r4c3" class="game-cell"></div>
                    <div id="r4c4" class="game-cell blue-cell"></div>
                    <div id="r4c5" class="game-cell"></div>
                    <div id="r4c6" class="game-cell"></div>
                    <div id="r4c7" class="game-cell"></div>
                    <div id="r4c8" class="game-cell"></div>
                    <div id="r4c9" class="game-cell"></div>
                    <div id="r4c10" class="game-cell blue-cell"></div>
                    <div id="r4c11" class="game-cell"></div>
                    <div id="r4c12" class="game-cell"></div>
                    <div id="r4c13" class="game-cell"></div>
                    <div id="r4c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r5c0" class="game-cell"></div>
                    <div id="r5c1" class="game-cell yellow-cell"></div>
                    <div id="r5c2" class="game-cell"></div>
                    <div id="r5c3" class="game-cell"></div>
                    <div id="r5c4" class="game-cell"></div>
                    <div id="r5c5" class="game-cell yellow-cell"></div>
                    <div id="r5c6" class="game-cell"></div>
                    <div id="r5c7" class="game-cell"></div>
                    <div id="r5c8" class="game-cell"></div>
                    <div id="r5c9" class="game-cell yellow-cell"></div>
                    <div id="r5c10" class="game-cell"></div>
                    <div id="r5c11" class="game-cell"></div>
                    <div id="r5c12" class="game-cell"></div>
                    <div id="r5c13" class="game-cell yellow-cell"></div>
                    <div id="r5c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r6c0" class="game-cell"></div>
                    <div id="r6c1" class="game-cell"></div>
                    <div id="r6c2" class="game-cell green-cell"></div>
                    <div id="r6c3" class="game-cell"></div>
                    <div id="r6c4" class="game-cell"></div>
                    <div id="r6c5" class="game-cell"></div>
                    <div id="r6c6" class="game-cell green-cell"></div>
                    <div id="r6c7" class="game-cell"></div>
                    <div id="r6c8" class="game-cell green-cell"></div>
                    <div id="r6c9" class="game-cell"></div>
                    <div id="r6c10" class="game-cell"></div>
                    <div id="r6c11" class="game-cell"></div>
                    <div id="r6c12" class="game-cell green-cell"></div>
                    <div id="r6c13" class="game-cell"></div>
                    <div id="r6c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r7c0" class="game-cell red-cell"></div>
                    <div id="r7c1" class="game-cell"></div>
                    <div id="r7c2" class="game-cell"></div>
                    <div id="r7c3" class="game-cell green-cell"></div>
                    <div id="r7c4" class="game-cell"></div>
                    <div id="r7c5" class="game-cell"></div>
                    <div id="r7c6" class="game-cell"></div>
                    <div id="r7c7" class="game-cell center-cell"></div>
                    <div id="r7c8" class="game-cell"></div>
                    <div id="r7c9" class="game-cell"></div>
                    <div id="r7c10" class="game-cell"></div>
                    <div id="r7c11" class="game-cell green-cell"></div>
                    <div id="r7c12" class="game-cell"></div>
                    <div id="r7c13" class="game-cell"></div>
                    <div id="r7c14" class="game-cell red-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r8c0" class="game-cell"></div>
                    <div id="r8c1" class="game-cell"></div>
                    <div id="r8c2" class="game-cell green-cell"></div>
                    <div id="r8c3" class="game-cell"></div>
                    <div id="r8c4" class="game-cell"></div>
                    <div id="r8c5" class="game-cell"></div>
                    <div id="r8c6" class="game-cell green-cell"></div>
                    <div id="r8c7" class="game-cell"></div>
                    <div id="r8c8" class="game-cell green-cell"></div>
                    <div id="r8c9" class="game-cell"></div>
                    <div id="r8c10" class="game-cell"></div>
                    <div id="r8c11" class="game-cell"></div>
                    <div id="r8c12" class="game-cell green-cell"></div>
                    <div id="r8c13" class="game-cell"></div>
                    <div id="r8c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r9c0" class="game-cell"></div>
                    <div id="r9c1" class="game-cell yellow-cell"></div>
                    <div id="r9c2" class="game-cell"></div>
                    <div id="r9c3" class="game-cell"></div>
                    <div id="r9c4" class="game-cell"></div>
                    <div id="r9c5" class="game-cell yellow-cell"></div>
                    <div id="r9c6" class="game-cell"></div>
                    <div id="r9c7" class="game-cell"></div>
                    <div id="r9c8" class="game-cell"></div>
                    <div id="r9c9" class="game-cell yellow-cell"></div>
                    <div id="r9c10" class="game-cell"></div>
                    <div id="r9c11" class="game-cell"></div>
                    <div id="r9c12" class="game-cell"></div>
                    <div id="r9c13" class="game-cell yellow-cell"></div>
                    <div id="r9c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r10c0" class="game-cell"></div>
                    <div id="r10c1" class="game-cell"></div>
                    <div id="r10c2" class="game-cell"></div>
                    <div id="r10c3" class="game-cell"></div>
                    <div id="r10c4" class="game-cell blue-cell"></div>
                    <div id="r10c5" class="game-cell"></div>
                    <div id="r10c6" class="game-cell"></div>
                    <div id="r10c7" class="game-cell"></div>
                    <div id="r10c8" class="game-cell"></div>
                    <div id="r10c9" class="game-cell"></div>
                    <div id="r10c10" class="game-cell blue-cell"></div>
                    <div id="r10c11" class="game-cell"></div>
                    <div id="r10c12" class="game-cell"></div>
                    <div id="r10c13" class="game-cell"></div>
                    <div id="r10c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r11c0" class="game-cell green-cell"></div>
                    <div id="r11c1" class="game-cell"></div>
                    <div id="r11c2" class="game-cell"></div>
                    <div id="r11c3" class="game-cell blue-cell"></div>
                    <div id="r11c4" class="game-cell"></div>
                    <div id="r11c5" class="game-cell"></div>
                    <div id="r11c6" class="game-cell"></div>
                    <div id="r11c7" class="game-cell green-cell"></div>
                    <div id="r11c8" class="game-cell"></div>
                    <div id="r11c9" class="game-cell"></div>
                    <div id="r11c10" class="game-cell"></div>
                    <div id="r11c11" class="game-cell blue-cell"></div>
                    <div id="r11c12" class="game-cell"></div>
                    <div id="r11c13" class="game-cell"></div>
                    <div id="r11c14" class="game-cell green-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r12c0" class="game-cell"></div>
                    <div id="r12c1" class="game-cell"></div>
                    <div id="r12c2" class="game-cell blue-cell"></div>
                    <div id="r12c3" class="game-cell"></div>
                    <div id="r12c4" class="game-cell"></div>
                    <div id="r12c5" class="game-cell"></div>
                    <div id="r12c6" class="game-cell green-cell"></div>
                    <div id="r12c7" class="game-cell"></div>
                    <div id="r12c8" class="game-cell green-cell"></div>
                    <div id="r12c9" class="game-cell"></div>
                    <div id="r12c10" class="game-cell"></div>
                    <div id="r12c11" class="game-cell"></div>
                    <div id="r12c12" class="game-cell blue-cell"></div>
                    <div id="r12c13" class="game-cell"></div>
                    <div id="r12c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r13c0" class="game-cell"></div>
                    <div id="r13c1" class="game-cell blue-cell"></div>
                    <div id="r13c2" class="game-cell"></div>
                    <div id="r13c3" class="game-cell"></div>
                    <div id="r13c4" class="game-cell"></div>
                    <div id="r13c5" class="game-cell yellow-cell"></div>
                    <div id="r13c6" class="game-cell"></div>
                    <div id="r13c7" class="game-cell"></div>
                    <div id="r13c8" class="game-cell"></div>
                    <div id="r13c9" class="game-cell yellow-cell"></div>
                    <div id="r13c10" class="game-cell"></div>
                    <div id="r13c11" class="game-cell"></div>
                    <div id="r13c12" class="game-cell"></div>
                    <div id="r13c13" class="game-cell blue-cell"></div>
                    <div id="r13c14" class="game-cell"></div>
                </div>
                <div class="board-row">
                    <div id="r14c0" class="game-cell red-cell"></div>
                    <div id="r14c1" class="game-cell"></div>
                    <div id="r14c2" class="game-cell"></div>
                    <div id="r14c3" class="game-cell green-cell"></div>
                    <div id="r14c4" class="game-cell"></div>
                    <div id="r14c5" class="game-cell"></div>
                    <div id="r14c6" class="game-cell"></div>
                    <div id="r14c7" class="game-cell red-cell"></div>
                    <div id="r14c8" class="game-cell"></div>
                    <div id="r14c9" class="game-cell"></div>
                    <div id="r14c10" class="game-cell"></div>
                    <div id="r14c11" class="game-cell green-cell"></div>
                    <div id="r14c12" class="game-cell"></div>
                    <div id="r14c13" class="game-cell"></div>
                    <div id="r14c14" class="game-cell red-cell"></div>
                </div>
            </div>

            <div id="send-button-div">
                <div class="btn-group btn-group-justified">
                    <a href="#" id="make-move-btn" class="btn btn-primary disabled"><span class="glyphicon glyphicon-send"
                                                                                      aria-hidden="true"></span>
                        Отправить!</a>
                    <a href="#" id="change-letters-btn" class="btn btn-default disabled"><span
                            class="glyphicon glyphicon-refresh" aria-hidden="true"></span> Поменять буквы</a>
                </div>
            </div>

            <div id="letter-line-container">
                <div id="letter-line" data-toggle="tooltip" data-placement="right" title=""
                     data-original-title="Выберите буквы, которые хотите заменить">
                    <div class="letter-container panel panel-default">
                        <div class="panel-body"></div>
                    </div>
                    <div class="letter-container panel panel-default">
                        <div class="panel-body"></div>
                    </div>
                    <div class="letter-container panel panel-default">
                        <div class="panel-body"></div>
                    </div>
                    <div class="letter-container panel panel-default">
                        <div class="panel-body"></div>
                    </div>
                    <div class="letter-container panel panel-default">
                        <div class="panel-body"></div>
                    </div>
                    <div class="letter-container panel panel-default">
                        <div class="panel-body"></div>
                    </div>
                    <div class="letter-container panel panel-default">
                        <div class="panel-body"></div>
                    </div>
                </div>
                <div id="change-letters-div">
                    <div class="btn-group">
                        <a href="#" id="send-changed-letters-btn" class="btn btn-primary disabled"><span
                                class="glyphicon glyphicon-ok" aria-hidden="true"></span></a>
                        <a href="#" id="cancel-changing-letters-btn" class="btn btn-default"><span class="glyphicon glyphicon-remove"
                                                                  aria-hidden="true"></span></a>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-3">
            <div id="players-container" class="panel-group">
                <div id="player" class="panel panel-default">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-5">
                                <div class="user-icon">
                                    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                </div>
                                <div class="rating">
                                    <span class="glyphicon glyphicon-star" aria-hidden="true"></span><span id="player-raiting"></span>
                                </div>
                            </div>
                            <div class="col-lg-10">
                                <div class="player-name">
                                    <span id="player-name"></span>
                                </div>
                                <div>
                                    <span>Очки: </span><span class="points" id="player-points">0</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="opponent0" class="panel panel-default">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-5">
                                <div class="user-icon">
                                    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                </div>
                                <div class="rating">
                                    <span class="glyphicon glyphicon-star" aria-hidden="true"></span><span id="opponent0-raiting"></span>
                                </div>
                            </div>
                            <div class="col-lg-10">
                                <div class="player-name">
                                    <span id="opponent0-name"></span>
                                </div>
                                <div>
                                    <span>Очки: </span><span id="opponent0-points" class="points">0</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="opponent1" class="panel panel-default">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-5">
                                <div class="user-icon">
                                    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                </div>
                                <div class="rating">
                                    <span class="glyphicon glyphicon-star" aria-hidden="true"></span><span id="opponent1-raiting"></span>
                                </div>
                            </div>
                            <div class="col-lg-10">
                                <div class="player-name">
                                    <span id="opponent1-name"></span>
                                </div>
                                <div>
                                    <span>Очки: </span><span id="opponent1-points" class="points">0</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="opponent2" class="panel panel-default">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-5">
                                <div class="user-icon">
                                    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                </div>
                                <div class="rating">
                                    <span class="glyphicon glyphicon-star" aria-hidden="true"></span><span id="opponent2-raiting"></span>
                                </div>
                            </div>
                            <div class="col-lg-10">
                                <div class="opponent-name">
                                    <span id="opponent2-name"></span>
                                </div>
                                <div>
                                    <span>Очки: </span><span id="opponent2-points" class="points">0</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
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
    <div id="modal-gameover" class="modal fade" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div id="modal-gameover-header" class="modal-header">
                    <h3><span class="glyphicon glyphicon-flag" aria-hidden="true"></span> Игра окончена</h3>
                </div>
                <div class="modal-body" id="modal-gameover-body">
                    <div id="gameover-info"></div>
                    <br>

                    <div>
                        <ul id="gameover-ul" class="list-group"></ul>
                    </div>
                </div>
                <div id="modal-gameover-footer" class="modal-footer">
                    <button id="gameover-btn" class="btn btn-primary">Закрыть</button>
                </div>
            </div>
        </div>
    </div>
    <div id="modal-info" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div id="modal-info-header" class="modal-header">
                    <h3>
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                        <span id="modal-info-header-span"></span>
                    </h3>
                </div>
                <div class="modal-body" id="modal-info-body">
                    <h5 id="modal-info-body-h5"></h5>
                </div>
                <div id="modal-info-footer" class="modal-footer">
                    <button id="info-btn" class="btn btn-primary" data-dismiss="modal">OK</button>
                </div>
            </div>
        </div>
    </div>

    <div id="player-info">
        <span id="username">${user.username}</span>
        <span id="raiting">${user.raiting}</span>
        <span id="email">${user.email}</span>
        <span id="is-guest">${user.guest}</span>

        <span id="gameId">${gameId}</span>
    </div>
</div>

</body>
</html>