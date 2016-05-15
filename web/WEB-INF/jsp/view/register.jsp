<!DOCTYPE html>
<html>
<head>
    <title>Erudit::Регистрация</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/erudit.css" />"/>

    <script src="<c:url value="/resources/js/jquery-2.1.4.min.js" />"></script>
    <script src="<c:url value="/resources/js/register.js" />"></script>
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
    <div class="jumbotron">
        <form id="register-form" method="post" action="register" class="form-horizontal">
            <fieldset>
                <legend>Регистрация на портале Erudit.ru</legend>
                <div id="email-form-group" class="form-group has-feedback">
                    <label for="email-input" class="col-lg-3 control-label">Email</label>
                    <div class="col-lg-12">
                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-envelope"></i></span>
                            <input required="required" name="email" class="form-control" id="email-input" placeholder="Email" type="text">
                        </div>
                        <span class="glyphicon form-control-feedback"></span>
                        <label id="email-error-msg" class="error-msg" for="email-input"></label>
                    </div>
                </div>
                <div id="username-form-group" class="form-group has-feedback">
                    <label for="username-input" class="col-lg-3 control-label">Имя</label>
                    <div class="col-lg-12">
                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                            <input required="required" name="username" class="form-control" id="username-input" placeholder="Имя" type="text">
                        </div>
                        <span class="glyphicon form-control-feedback"></span>
                        <label id="username-error-msg" class="error-msg" for="email-input"></label>
                    </div>
                </div>
                <br>
                <br>
                <div id="password-form-group" class="form-group has-feedback">
                    <label for="password-input" class="col-lg-3 control-label">Пароль</label>
                    <div class="col-lg-12">
                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-pencil"></i></span>
                            <input required="required" name="password" class="form-control" id="password-input" placeholder="Пароль" type="password">
                        </div>
                        <span class="glyphicon form-control-feedback"></span>
                        <label id="password-error-msg" class="error-msg" for="email-input"></label>
                    </div>
                </div>
                <div id="password-repeat-form-group" class="form-group has-feedback">
                    <label for="password-repeat-input" class="col-lg-3 control-label">Повторите пароль</label>
                    <div class="col-lg-12">
                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-pencil"></i></span>
                            <input required="required" class="form-control" id="password-repeat-input" placeholder="Пароль" type="password">
                        </div>
                        <span class="glyphicon form-control-feedback"></span>
                        <label id="repeat-password-error-msg" class="error-msg" for="email-input">Пароли не совпадают!</label>
                    </div>
                </div>
                <br>
                <br>
                <div class="form-group">
                    <div class="col-lg-12 col-lg-offset-3">
                        <button id="submit-btn" type="submit" class="btn btn-primary">Зарегистрироваться</button>
                    </div>
                </div>
            </fieldset>
        </form>
        <div id="register-success">
            <h3>Поздравляем!</h3>
            <h4><span id="registration-username"></span>,</h4>
            <p>Вы успешно зарегистрировались на нашем портале</p>
            <p><a class="btn btn-primary btn-lg" href="<c:url value="start" />" role="button">Приступить!</a></p>
        </div>
    </div>
</div>

</body>
</html>