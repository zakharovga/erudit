/**
 * Created by zakhar on 19.04.2016.
 */

$(document).ready(function () {

    var player = {
        username: $('#username').text(),
        raiting: $('#raiting').text(),
        email: $('#email').text(),
        guest: $('#is-guest').text()
    };

    var playerOpponents = [];

    var $modalError = $("#modal-error");
    var $modalErrorBody = $("#modal-error-body");
    var $modalWaiting = $("#modal-waiting");
    var $modalPlayers = $("#modal-players");

    var webSocket;

    if (!("WebSocket" in window)) {
        $modalErrorBody.text('Вебсокеты не поддерживаются этим браузером. ' +
            'Попробуйте Internet Explorer 10 ' +
            'или последние версии Mozilla Firefox или Google Chrome.');
        $modalError.modal('show');
        return;
    }

    var joinPlayer = function (opponents) {
        playerOpponents = opponents;

        for (var i = 0; i < opponents.length; i++) {
            $("#opponent" + i + "-name-modal").text(opponents[i].user.username);
            var $opponent = $("#opponent" + i + "-modal");
            if (opponents[i].ready == true) {
                $opponent.addClass('selected');
            }
            $opponent.fadeIn('slow');
        }
    };

    var joinOpponent = function (player) {

        var opponent = {user: player, ready: false};
        playerOpponents.push(opponent);
        var lastIndex = playerOpponents.length - 1;

        $("#opponent" + lastIndex + "-name-modal").text(player.username);
        $("#opponent" + lastIndex + "-modal").fadeIn('slow');
    };

    var createWebSocket = function (action, gameId) {
        try {
            if (action == 'create') {
                webSocket = new WebSocket('ws://' + window.location.host + '/start?action=' + action);
                return webSocket;
            }
            else if (action == 'join') {
                webSocket = new WebSocket('ws://' + window.location.host + '/start?action=' + action +
                    '&' + 'gameid=' + gameId);
                return webSocket;
            }
        } catch (error) {
            throw error;
        }
    };

    var deleteOpponent = function (opponent) {

        var deletedIndex;
        for (var i = 0; i < playerOpponents.length; i++) {
            $opponent = $("#opponent" + i + "-modal");
            if (playerOpponents[i].user.username == opponent) {
                deletedIndex = i;
                $opponent.removeClass('selected');
            }
            $opponent.hide();
        }
        playerOpponents.splice(deletedIndex, 1);

        for (var j = 0; j < playerOpponents.length; j++) {
            $("#opponent" + j + "-name-modal").text(playerOpponents[j].username);
            $("#opponent" + j + "-modal").fadeIn();
        }
    };

    var redirectToGame = function (gameId) {
        var url = 'start';
        var $form = $('<form id="redirectForm" method="post"></form>').attr({action: url, style: 'display: none'});
        $form.append($('<input type="hidden">').attr({name: 'gameId', value: gameId}));
        $('body').append($form);

        $form.submit();
    };

    var initWebSocket = function () {
        webSocket.onmessage = function (event) {

            var message = JSON.parse(event.data);
            if (message.action == 'playerJoined') {
                $modalWaiting.modal('hide');
                $modalPlayers.modal({keyboard: false, backdrop: 'static', show: true});
                joinPlayer(message.opponents);
                return;
            }
            if (message.action == 'opponentJoined') {
                joinOpponent(message.player);
                return;
            }
            if (message.action == 'opponentQuit') {
                deleteOpponent(message.opponent);
                return;
            }
            if (message.action == 'opponentReady') {
                var readyIndex;
                var ready = message.ready;
                for (var i = 0; i < playerOpponents.length; i++) {
                    if (playerOpponents[i].user.username == message.opponent) {
                        readyIndex = i;
                        break;
                    }
                }
                if (ready) {
                    $('#opponent' + i + '-modal').addClass('selected');
                }
                else {
                    $('#opponent' + i + '-modal').removeClass('selected');
                }
            }
            if (message.action == 'playerRedirected') {
                redirectToGame(message.gameId);
            }
        };

        window.onbeforeunload = function () {
            webSocket.close();
        };

        webSocket.onclose = function (event) {
            if (!event.wasClean) {
                $modalPlayers.modal('hide');
                $modalWaiting.modal('hide');
                $modalErrorBody.text('Соединение с сервером разорвано. Код ' + event.code + ': ' + event.reason);
                $modalError.modal('show');
            }
            else {
                $modalPlayers.modal('hide');
                $modalWaiting.modal('hide');
                $modalErrorBody.text(event.reason);
                $modalError.modal('show');
            }
        };

        webSocket.onerror = function (event) {
            $modalWaiting.modal('hide');
            $modalErrorBody.text('Произошла ошибка ' + event.data);
            $modalError.modal('show');
        };

//            $("#start").click(startGame);
//
//            var startGame = function () {
//                if (webSocket != null) {
//                    webSocket.send(JSON.stringify({action: 'start'}));
//                }
//            };
    };

    var createGame = function () {

        try {
            $modalWaiting.modal('show');

            webSocket = createWebSocket('create');

            webSocket.onopen = function (event) {
                $modalWaiting.modal('hide');
                $modalPlayers.modal({keyboard: false, backdrop: 'static', show: true});
                $("#player-modal").show();
            };

            initWebSocket();
        } catch (error) {
            $modalWaiting.modal('hide');
            $modalErrorBody.text(error);
            $modalError.modal('show');
        }
    };

    var joinGame = function (gameId) {
        try {
            $modalWaiting.modal('show');

            webSocket = createWebSocket('join', gameId);
            webSocket.onopen = function (event) {
                $("#player-modal").show();
            };

            initWebSocket();
        } catch (error) {
            $modalWaiting.modal('hide');
            $modalErrorBody.text(error);
            $modalError.modal('show');
        }
    };


    $("#create-game-btn").click(createGame);

    $(".join-game-btn").click(function () {
        joinGame($(this).attr('gameId'));
    });

    var setReady = function (ready) {
        if (ready) {
            $('#player-modal').addClass('selected');
            webSocket.send(JSON.stringify({action: 'opponentReady', readyOpponent: player.username, ready: true}));
        }
        else {
            $('#player-modal').removeClass('selected');
            webSocket.send(JSON.stringify({action: 'opponentReady', readyOpponent: player.username, ready: false}));
        }
    };

    $('#ready-radio').find(':input').change(function () {
        if ($(this).is('#option1')) {
            setReady(true);
        }
        else {
            setReady(false);
        }
    });
});