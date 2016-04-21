<!DOCTYPE html>
<html>
<head>
    <title>Erudit::Регистрация</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/erudit.css" />"/>

    <script src="<c:url value="/resources/js/jquery-2.1.4.min.js" />"></script>
</head>

<body>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Brand</a>
        </div>
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Link <span class="sr-only">(current)</span></a></li>
                <li><a href="#">Link</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Dropdown <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="#">Action</a></li>
                        <li><a href="#">Another action</a></li>
                        <li><a href="#">Something else here</a></li>
                        <li class="divider"></li>
                        <li><a href="#">Separated link</a></li>
                        <li class="divider"></li>
                        <li><a href="#">One more separated link</a></li>
                    </ul>
                </li>
            </ul>
            <form class="navbar-form navbar-left" role="search">
                <div class="form-group">
                    <input class="form-control" placeholder="Search" type="text">
                </div>
                <button type="submit" class="btn btn-default">Submit</button>
            </form>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="#">Link</a></li>
            </ul>
        </div>
    </div>
</nav>
<div class="container">
    <div class="jumbotron">
        <form method="post" action="register" class="form-horizontal">
            <fieldset>
                <legend>Регистрация на портале Erudit.ru</legend>
                <div class="form-group">
                    <label for="inputEmail" class="col-lg-2 control-label">Email</label>
                    <div class="col-lg-10">
                        <input name="email" class="form-control" id="inputEmail" placeholder="Email" type="text">
                    </div>
                </div>
                <div class="form-group">
                    <label for="inputUsername" class="col-lg-2 control-label">Имя</label>
                    <div class="col-lg-10">
                        <input name="username" class="form-control" id="inputUsername" placeholder="Имя" type="text">
                    </div>
                </div>
                <br>
                <div class="form-group">
                    <label for="inputPassword" class="col-lg-2 control-label">Пароль</label>
                    <div class="col-lg-10">
                        <input name="password" class="form-control" id="inputPassword" placeholder="Пароль" type="password">
                    </div>
                </div>
                <div class="form-group">
                    <label for="repeatPassword" class="col-lg-2 control-label">Повторите пароль</label>
                    <div class="col-lg-10">
                        <input class="form-control" id="repeatPassword" placeholder="Пароль" type="password">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-lg-10 col-lg-offset-2">
                        <button type="submit" class="btn btn-primary">Готово</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>

</body>
</html>