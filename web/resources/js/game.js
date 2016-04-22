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
    var $modalWaiting = $('#modal-waiting');

    var $gameCells = $('.game-cell');
    var $makeMoveBtn = $('#make-move-btn');
    var $changeLettersBtn = $('#change-letters-btn');
    var $changeLettersDiv = $('#change-letters-div');
    var $sendChangedLettersBtn = $('#send-changed-letters-btn');
    var $cancelChangingLettersBtn = $('#cancel-changing-letters-btn');

    var myTurn = false;
    var $status = $("#next-turn");
    var username = player.username;

    var gameId = $("#gameId").text();

    var moveType;

    var move = [];
    var abortMove = [];
    var changedLetters = [];

    var timer = (function () {

        var $timer = $('#timer');
        var timer;
        var timePerMove = '01:00';

        var changeTime = function () {
            var tempArr = $timer.text().split(':');
            var min = tempArr[0];
            var sec = tempArr[1];
            if (sec === '00') {
                sec = '59';
                min = '' + (parseInt(min) - 1);
                if (min.length === 1) {
                    min = '0' + min;
                }
            }
            else {
                sec = '' + (parseInt(sec) - 1);
                if (sec.length === 1) {
                    sec = '0' + sec;
                }
            }
            $timer.text(min + ':' + sec);
            if ($timer.text() === '00:00') {
                $timer.css('color', '#ccc');
                clearInterval(timer);
            }
        };

        return {
            start: function () {
                $timer.css('color', '#fff');
                $timer.text(timePerMove);
                timer = setInterval(changeTime, 1000);
            },
            stop: function () {
                clearInterval(timer);
            }
        }
    }());

    if (!("WebSocket" in window)) {
        $modalErrorBody.text('Вебсокеты не поддерживаются этим браузером. ' +
            'Попробуйте Internet Explorer 10 ' +
            'или последние версии Mozilla Firefox или Google Chrome.');
        $modalError.modal('show');
        return;
    }

    $modalWaiting.modal('show');

    var webSocket;
    try {
        webSocket = new WebSocket('ws://' + window.location.host + '/game/' + gameId);
    } catch (error) {
        $modalWaiting.modal('hide');
        $modalErrorBody.text(error);
        $modalError.modal('show');
        return;
    }

    var toggleTurn = function (player) {
        if (username == player) {
            myTurn = true;
            $status.text('Ваш ход!');
        }
        else {
            myTurn = false;
            $status.text('Ходит ' + player + '!');
        }
    };

    var moveAnimate = function ($element, $newParent) {

        setTimeout(function () {
            var oldOffset = $element.offset();
            $element.css({top: 0, left: 0}).appendTo($newParent);
            var newOffset = $element.offset();

            var $temp = $element.clone().appendTo('body');
            $temp.css({
                'position': 'absolute',
                'left': oldOffset.left,
                'top': oldOffset.top,
                'z-index': 1000
            });
            $element.hide();
            $temp.animate({'top': newOffset.top, 'left': newOffset.left}, 'fast', function () {
                $element.show();
                $temp.remove();
            });
        }, 0);

    };

    webSocket.onopen = function (event) {

    };

    window.onbeforeunload = function () {
        webSocket.close();
    };

    webSocket.onclose = function (event) {
        if (!event.wasClean) {
            $modalWaiting.modal('hide');
            $modalErrorBody.text('Соединение с сервером разорвано. Код ' + event.code + ': ' + event.reason);
            $modalError.modal('show');
        }
        else {
            $modalWaiting.modal('hide');
            $modalErrorBody.text(event.reason);
            $modalError.modal('show');
        }
    };

    webSocket.onerror = function (event) {
        $modalErrorBody.text(event.data);
        $modalError.modal('show');
    };

    webSocket.onmessage = function (event) {
        var message = JSON.parse(event.data);

        if (message.action == 'gameStarted') {

            timer.start();

            $('.letter-container>div').droppable('enable');
            $modalWaiting.modal('hide');
            fillLetterContainer(message.givenLetters);
            fillPlayerInfo(message.opponents);
            toggleTurn(message.nextMove);
            if (myTurn) {
                $gameCells.droppable('enable');
                $changeLettersBtn.removeClass('disabled');
            }
            return;
        }
        if (message.action == 'playerMadeMove') {

            var $moveMade = $('.move-made');
            $moveMade.draggable('destroy');
            $moveMade.removeClass('move-made letter-line');
            abortMove = [];
            moveType = '';

            toggleTurn(message.nextMove);

            timer.stop();
            timer.start();

            fillLetterContainer(message.letters);
            fillWordList(message.words, username);
            return;
        }
        if (message.action == 'opponentMadeMove') {
            for (var i = message.moves.length - 1; i >= 0; i--) {
                var id = "r" + message.moves[i].row + "c" + message.moves[i].column;
                var letter = message.moves[i].letter.letter;
                var value = message.moves[i].letter.value;

                var letterWrapper = $('<div/>', {class: 'letter-div', css: {'display': 'none'}});
                letterWrapper.appendTo($('#' + id));
                $('<span/>', {class: 'letter'}).text(letter).appendTo(letterWrapper);
                $('<span/>', {class: 'value'}).text(value).appendTo(letterWrapper);
                letterWrapper.fadeIn('slow');
            }
            fillWordList(message.words, message.previousMove);
            toggleTurn(message.nextMove);

            timer.stop();
            timer.start();

            if (myTurn) {
                $changeLettersBtn.removeClass('disabled');
                $gameCells.droppable('enable');
            }
            return;
        }
        if (message.action == 'playerChangedLetters') {
            $('.letter-line').draggable('enable');
            toggleTurn(message.nextMove);

            timer.stop();
            timer.start();

            replaceChangedLetters(message.changedLetters);
            return;
        }
        if (message.action == 'opponentChangedLetters') {
            toggleTurn(message.nextMove);

            timer.stop();
            timer.start();

            if (myTurn) {
                $changeLettersBtn.removeClass('disabled');
                $gameCells.droppable('enable');
            }
            return;
        }
        if (message.action == 'timeOver') {

            var beforeToggle = myTurn;

            toggleTurn(message.nextMove);

            timer.stop();
            timer.start();

            if (myTurn) {
                $changeLettersBtn.removeClass('disabled');
                $gameCells.droppable('enable');
            }
            else {
                if (beforeToggle) {
                    $changeLettersBtn.addClass('disabled');
                    $gameCells.droppable('disable');
                    if (moveType === 'CHANGE_LETTERS') {
                        $('#letter-line').tooltip('destroy');
                        for (var i = 0; i < changedLetters.length; i++) {
                            changedLetters[i].removeClass('selected');
                        }
                        $changeLettersDiv.hide('slow');
                        var $letterLine = $('.letter-line');
                        $letterLine.unbind('click');
                        $letterLine.draggable('enable');
                    }
                    else if (moveType === 'MAKE_WORDS') {
                        $('.letter-line').draggable('enable');
                        for (var j = 0; j < abortMove.length; j++) {
                            moveAnimate(abortMove[j].$letter, abortMove[j].$initialParent);
                        }
                        $makeMoveBtn.addClass('disabled');
                        move = [];
                        abortMove = [];
                        $('.move-made').removeClass('move-made');
                    }
                }
            }
            return;
        }
        if(message.action === 'gameOver') {
            var gameResult = message.gameResult;

            var $h4 = $('<h4/>');
            if(gameResult[0].points === gameResult[1].points) {
                $h4.text('Ничья').appendTo($('#gameover-info'));
            }
            else {
                $h4.text('Победитель: ' + gameResult[0].player).appendTo($('#gameover-info'));
            }

            for(var i = 0; i < gameResult.length; i++) {
                var $li = $('<li/>', {class: 'list-group-item'});
                var $spanPoints = $('<span/>', {class: 'badge'}).text(gameResult[i].points);
                var $spanPlayer = $('<span/>').text(gameResult[i].player);
                $spanPoints.appendTo($li);
                $spanPlayer.appendTo($li);
                $li.appendTo($('#gameover-ul'));
            }

            $('#modal-gameover').modal('show');
        }
        if(message.action === 'WRONG_FIRST_MOVE') {
            console.log(message);
        }
    };

    var fillPlayerInfo = function (opponents) {
        $("#player-name").text(username);
        $("#player").show('slow');
        for (var i = 0; i < opponents.length; i++) {
            $("#opponent" + i + "-name").text(opponents[i].username);
            $("#opponent" + i).show('slow');
        }
    };

    var fillWordList = function (words, player) {
        var $players = $(".player-name");
        $players.each(function () {
            var $player = $(this);
            if ($player.children().eq(0).text() === player) {
                var $points = $player.next().children().eq(1);
                $points.text(function () {
                    var addedPoints = 0;
                    for (var word in words) {
                        if (words.hasOwnProperty(word)) {
                            var $li = $('<li/>', {class: 'list-group-item', css: {'display': 'none'}});
                            var $spanValue = $('<span/>', {class: 'badge'}).text(words[word]);
                            var $spanWord = $('<span/>').text(word);
                            $spanValue.appendTo($li);
                            $spanWord.appendTo($li);
                            $li.appendTo($('#words'));
                            $li.show('slow');
                            addedPoints = addedPoints + parseInt(words[word]);
                        }
                    }
                    return parseInt($points.text()) + addedPoints;
                })
            }
        });
    };

    var replaceChangedLetters = function (newLetters) {
        for (var i = 0; i < changedLetters.length; i++) {
            var $letterWrapper = $('<div/>', {class: 'letter-div letter-line', css: {'display': 'none'}});
            changedLetters[i].replaceWith($letterWrapper);
            $('<span/>', {class: 'letter'}).text(newLetters[i].letter).appendTo($letterWrapper);
            $('<span/>', {class: 'value'}).text(newLetters[i].value).appendTo($letterWrapper);
            $letterWrapper.fadeIn('slow');
            $letterWrapper.draggable({
                stack: ".letter-line",
                revert: 'invalid'
            });
        }
    };

    var fillLetterContainer = function (givenLetters) {
        var i = 0;
        $("div.letter-container>div:empty").each(function () {
            var $letterWrapper = $('<div/>', {class: 'letter-div letter-line', css: {'display': 'none'}});
            $letterWrapper.appendTo($(this));
            $('<span/>', {class: 'letter'}).text(givenLetters[i].letter).appendTo($letterWrapper);
            $('<span/>', {class: 'value'}).text(givenLetters[i].value).appendTo($letterWrapper);
            $letterWrapper.fadeIn('slow');
            $letterWrapper.draggable({
                stack: ".letter-line",
                revert: 'invalid'
            });
            i++;
        });
    };

    var makeMove = function () {
        if (webSocket != null) {
            $makeMoveBtn.addClass('disabled');

            webSocket.send(JSON.stringify({action: "playerMadeMove", move: move}));
            move = [];

            var $moveMade = $('.move-made');
            $moveMade.draggable('disable');

            $gameCells.droppable('disable');
        }
    };

    var selectChangedLetter = function ($selectedLetter) {

        $selectedLetter.addClass('selected');
        $sendChangedLettersBtn.removeClass('disabled');

        changedLetters.push($selectedLetter);
    };

    var changeLetters = function () {
        moveType = 'CHANGE_LETTERS';

        changedLetters = [];

        $makeMoveBtn.addClass('disabled');
        $changeLettersBtn.addClass('disabled');

        var $letterLine = $('.letter-line');
        $letterLine.draggable('disable');

        $changeLettersDiv.show('slow');
        $letterLine.click(function (event) {
            var $selectedLetter = $(this);
            selectChangedLetter($selectedLetter);
            $selectedLetter.unbind('click');
        });
        $('#letter-line').tooltip('show');
    };

    var getRow = function (cell) {
        return parseInt(cell.substring(1, cell.indexOf("c")));
    };

    var getColumn = function (cell) {
        return parseInt(cell.substring(cell.indexOf("c") + 1));
    };

    var sendSelectedLetters = function () {
        if (webSocket != null) {
            var sentLetters = [];
            $sendChangedLettersBtn.addClass('disabled');
            $('#letter-line').tooltip('destroy');
            $changeLettersDiv.hide('slow');
            $gameCells.droppable('disable');
            $('.letter-line').unbind('click');

            for (var i = 0; i < changedLetters.length; i++) {
                var $letter = changedLetters[i].children(':first');
                var $value = $letter.next();

                sentLetters.push({letter: $letter.text(), value: $value.text()});
            }
            webSocket.send(JSON.stringify({action: 'playerMadeMove', letters: sentLetters}));
        }
    };

    var cancelSelectingLetters = function () {
        for (var i = 0; i < changedLetters.length; i++) {
            changedLetters[i].removeClass('selected');
        }
        changedLetters = [];

        var $letterLine = $('.letter-line');
        $letterLine.unbind('click');
        $letterLine.draggable('enable');

        $sendChangedLettersBtn.addClass('disabled');
        $changeLettersDiv.hide('slow');
        $changeLettersBtn.removeClass('disabled');
    };

    $makeMoveBtn.click(makeMove);
    $changeLettersBtn.click(changeLetters);

    $sendChangedLettersBtn.click(sendSelectedLetters);
    $cancelChangingLettersBtn.click(cancelSelectingLetters);

    $('.game-cell, .letter-container>div').droppable({
        accept: function (el) {
            return $(this).children('.letter-div').length === 0 && el.hasClass('letter-div');
        },
        hoverClass: "hovered",
        disabled: true,
        drop: function (event, ui) {

            var $droppable = $(this);
            var $draggable = ui.draggable;

            var $parent = ui.draggable.parent();

            moveAnimate($draggable, $droppable);

            // если буква была перемещена с клетки доски (на другую клетку или назад в лоток игрока)
            if ($parent.hasClass('game-cell')) {
                if (!$droppable.hasClass('game-cell')) {
                    $draggable.removeClass('move-made');
                }
                for (var i = 0; i < move.length; i++) {
                    var cell = "r" + move[i].row + "c" + move[i].column;
                    if (cell === $parent.attr('id')) {
                        move.splice(i, 1);
                        break;
                    }
                }
            }

            // если буква была перемещена на клетку доски (с другой клетки или из лотка)
            if ($droppable.hasClass('game-cell')) {
                moveType = 'MAKE_WORDS';

                $draggable.addClass('move-made');

                var stringCell = $droppable.attr('id');
                var row = getRow(stringCell);
                var column = getColumn(stringCell);

                var $letterSpan = $draggable.children(':first');
                var $valueSpan = $letterSpan.next();

                var cellWithLetter = {
                    row: row,
                    column: column,
                    letter: {
                        letter: $letterSpan.text(),
                        value: $valueSpan.text()
                    }
                };
                move.push(cellWithLetter);
            }
            var addToAbortMove;
            for (var i = 0; i < abortMove.length; i++) {
                if (abortMove[i].$letter === $draggable) {
                    addToAbortMove = false;
                    if (abortMove[i].$initialParent === $droppable) {
                        abortMove.splice(i, 1);
                        break;
                    }
                    break;
                }
            }
            if (!addToAbortMove) {
                abortMove.push({
                    $letter: $draggable,
                    $initialParent: $parent
                });
            }
            if (move.length > 0) {
                $makeMoveBtn.removeClass('disabled');
                $changeLettersBtn.addClass('disabled');
            }
            else {
                $makeMoveBtn.addClass('disabled');
                $changeLettersBtn.removeClass('disabled');
            }
        }
    });
});