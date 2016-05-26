<!DOCTYPE html>
<html>
<head>
    <title>Erudit :: Online</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.custom.min.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/erudit.css" />"/>

    <script src="<c:url value="/resources/js/jquery-2.1.4.min.js" />"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>

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
        <h1>Erudit.ru</h1>
        <p>Интернет-портал для игры в эрудит. Авторизируйтесь или начните игру как гость.</p>
        <div class="btn-group btn-group-justified">
            <a href="<c:url value="/register" />" class="btn btn-primary btn-lg"><strong>Зарегистрироваться</strong></a>
            <a href="<c:url value="/start" />" class="btn btn-default btn-lg"><strong>Играть как гость</strong></a>
        </div>
        <form method="post" action="login" class="form-signin">
            <h2 class="form-signin-heading">Вход</h2>

            <label for="email-input" class="sr-only">Email</label>
            <input name="email" type="email" id="email-input" class="form-control" placeholder="Email" required autofocus>
            <label for="password-input" class="sr-only">Password</label>
            <input name="password" type="password" id="password-input" class="form-control" placeholder="Пароль" required>
            <c:if test="${not empty requestScope.loginError}">
                <label id="login-error-msg" class="error-msg" for="email-input">Ошибка в email или пароле</label>
            </c:if>
            <div class="checkbox">
                <label>
                    <input type="checkbox" value="remember-me"> Запомнить
                </label>
            </div>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Войти</button>
        </form>
    </div>
</div>
</body>
</html>