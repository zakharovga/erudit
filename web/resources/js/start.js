/**
 * Created by zakhar on 19.04.2016.
 */

$(document).ready(function () {

    var player = { };

    var playerOpponents = [];

    var $modalError = $("#modal-error");
    var $modalErrorBody = $("#modal-error-body");
    var $modalWaiting = $("#modal-waiting");
    var $modalPlayers = $("#modal-players");

    var $pendingGameDivs = $('.pending-game-div');

    var webSocket;

    if (!("WebSocket" in window)) {
        $modalErrorBody.text('Вебсокеты не поддерживаются этим браузером. ' +
            'Попробуйте Internet Explorer 10 ' +
            'или последние версии Mozilla Firefox или Google Chrome.');
        $modalError.modal('show');
        return;
    }

    var setPlayerReady = function(ready) {
        if(ready) {
            $('#player-status').removeClass('status-not-ready glyphicon-remove').addClass('status-ready glyphicon-ok');
            $('#player-modal').addClass('selected');
        }
        else {
            $('#player-status').removeClass('status-ready glyphicon-ok').addClass('status-not-ready glyphicon-remove');
            $('#player-modal').removeClass('selected');
        }
    };

    var setOpponentReady = function(index, ready) {
        if(ready) {
            $('#opponent' + index + '-status').removeClass('status-not-ready glyphicon-remove').addClass('status-ready glyphicon-ok');
            $('#opponent' + index + '-modal').addClass('selected');
        }
        else {
            $('#opponent' + index + '-status').removeClass('status-ready glyphicon-ok').addClass('status-not-ready glyphicon-remove');
            $('#opponent' + index + '-modal').removeClass('selected');
        }
    };

    var gameCreated = function(user) {
        $modalWaiting.modal('hide');
        $modalPlayers.modal({keyboard: false, backdrop: 'static', show: true});

        player.email = user.email;
        player.guest = user.guest;
        player.raiting = user.raiting;
        player.username = user.username;

        $('#player-name-modal').text(player.username);
        $('#player-raiting').text(player.raiting);
        $('#player-modal').fadeIn('slow');
    };

    var joinPlayer = function (user, opponents) {
        $modalWaiting.modal('hide');
        $modalPlayers.modal({keyboard: false, backdrop: 'static', show: true});

        player.email = user.email;
        player.guest = user.guest;
        player.raiting = user.raiting;
        player.username = user.username;

        $('#player-name-modal').text(player.username);
        $('#player-raiting').text(player.raiting);
        $('#player-modal').fadeIn('slow');

        for (var i = 0; i < opponents.length; i++) {
            playerOpponents[i] = opponents[i];

            $('#opponent' + i + '-name-modal').text(opponents[i].user.username);
            $('#opponent' + i + '-raiting').text(opponents[i].user.raiting);
            var $opponent = $('#opponent' + i + '-modal');
            setOpponentReady(i, opponents[i].ready);
            $opponent.fadeIn('slow');
        }
    };

    var joinOpponent = function (player) {

        var opponent = {user: player, ready: false};
        playerOpponents.push(opponent);
        var lastIndex = playerOpponents.length - 1;

        $('#opponent' + lastIndex + '-raiting').text(player.raiting);
        $('#opponent' + lastIndex + '-name-modal').text(player.username);
        $('#opponent' + lastIndex + '-modal').fadeIn('slow');
    };

    var createWebSocket = function (action, gameId) {
        try {
            if (action == 'CREATE') {
                webSocket = new WebSocket('ws://' + window.location.host + '/start?action=' + action);
                return webSocket;
            }
            else if (action == 'JOIN') {
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
                $('#opponent' + i + '-status').removeClass('status-ready glyphicon-ok').addClass('status-not-ready glyphicon-remove');
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
            if (message.action == 'PLAYER_CREATED') {
                gameCreated(message.player);
                return;
            }
            if (message.action == 'PLAYER_JOINED') {
                joinPlayer(message.player, message.opponents);
                return;
            }
            if (message.action == 'OPPONENT_JOINED') {
                joinOpponent(message.player);
                return;
            }
            if (message.action == 'OPPONENT_QUIT') {
                deleteOpponent(message.opponent);
                return;
            }
            if (message.action == 'OPPONENT_READY') {
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
                    $('#opponent' + i + '-status').removeClass('status-not-ready glyphicon-remove').addClass('status-ready glyphicon-ok');
                }
                else {
                    $('#opponent' + i + '-modal').removeClass('selected');
                    $('#opponent' + i + '-status').removeClass('status-ready glyphicon-ok').addClass('status-not-ready glyphicon-remove');
                }
            }
            if (message.action == 'PLAYER_REDIRECTED') {
                redirectToGame(message.gameId);
            }
        };

        window.onbeforeunload = function () {
            webSocket.onclose = function(event) {};
            webSocket.close();
        };

        webSocket.onclose = function (event) {
            playerOpponents = [];
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
            playerOpponents = [];
            $modalWaiting.modal('hide');
            $modalErrorBody.text('Произошла ошибка ' + event.data);
            $modalError.modal('show');
        };
    };

    var createGame = function(event) {

        event.preventDefault();
        try {
            $modalWaiting.modal('show');

            webSocket = createWebSocket('CREATE');

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

            webSocket = createWebSocket('JOIN', gameId);
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

    $pendingGameDivs.each(function() {
        var $this = $(this);
        var numPlayers = $this.attr('numPlayers');
        for(var i = 0; i < numPlayers; i++) {
            $this.find('span').eq(i).removeClass('free-icon').addClass('busy-icon');
        }
    });

    $('#pager').find('li a').click(function(e) {
        e.preventDefault();
    });

    var pageList = function($list, n) {
        var beginRow = 0;

        var $rows = $list.children();
        var numRows = $rows.size();
        var numPages = Math.ceil(numRows/n);

        var $pager = $('#pager');
        var $previous = $pager.children().eq(0);
        var $next = $pager.children().eq(1);

        $rows.hide();
        $rows.slice(0, n).show();

        if(numRows > n) {
            $next.removeClass('disabled');
        }

        var next = function() {
            $previous.removeClass('disabled');
            beginRow += n;
            var endRow = 0;
            if(beginRow + n < numRows) {
                endRow = beginRow + n - 1;
                $rows.css('display','none').slice(beginRow, endRow + 1).show();
            }
            else {
                endRow = numRows - 1;
                $rows.css('display','none').slice(beginRow).show();
                $next.addClass('disabled');
            }
        };

        var previous = function() {
            $next.removeClass('disabled');
            beginRow -= n;
            var endRow = 0;
            if(beginRow > 0) {
                endRow = beginRow + n - 1;
                $rows.css('display','none').slice(beginRow, endRow + 1).show();
            }
            else {
                endRow = beginRow + n - 1;
                $rows.css('display','none').slice(beginRow, endRow + 1).show();
                $previous.addClass('disabled');
            }
        };

        $next.click(function(){
            if($(this).hasClass('disabled')) {
                return false;
            }
            next();
            return false;
        });

        $previous.click(function(){
            if($(this).hasClass('disabled')) {
                return false;
            }
            previous();
            return false;
        });
    };

    pageList($('#game-list'), 10);

    $('#create-game-btn').click(createGame);

    $pendingGameDivs.click(function(event) {
        joinGame($(this).attr('gameId'));
        return false;
    });

    $('#ready-radio').find(':input').change(function () {
        if ($(this).is('#ready-input')) {
            setPlayerReady(true);
            webSocket.send(JSON.stringify({action: 'OPPONENT_READY', readyOpponent: player.username, ready: true}));
        }
        else {
            setPlayerReady(false);
            webSocket.send(JSON.stringify({action: 'OPPONENT_READY', readyOpponent: player.username, ready: false}));
        }
    });

    $('#cancel').click(function() {
        playerOpponents = [];
        $('#ready-label').removeClass('active');
        $('#not-ready-label').addClass('active');
        $('#not-ready-input').attr('checked', 'checked');
        $('#ready-input').removeAttr('checked');
        setPlayerReady(false);
        for(var i = 0; i < playerOpponents.length; i++) {
            setOpponentReady(i, false);
        }
        $('#player-modal, .opponent-modal').hide();
        webSocket.onclose = function(event) {};
        webSocket.close();
        $modalPlayers.modal('hide');
    });
});