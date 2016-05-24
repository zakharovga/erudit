/**
 * Created by zakhar on 19.04.2016.
 */

$(document).ready(function () {

    var player = { };

    var playerOpponents = [];

    var $modalError = $("#modal-error");
    var $modalErrorBody = $("#modal-error-body");
    var $modalWaiting = $('#modal-waiting');
    var $modalInfo = $('#modal-info');
    var $modalGameover = $('#modal-gameover');

    var $droppable = $('.letter-container>div, .game-cell');
    var $makeMoveBtn = $('#make-move-btn');
    var $changeLettersBtn = $('#change-letters-btn');
    var $changeLettersDiv = $('#change-letters-div');
    var $sendChangedLettersBtn = $('#send-changed-letters-btn');
    var $cancelChangingLettersBtn = $('#cancel-changing-letters-btn');

    var myTurn = false;
    var $status = $("#next-turn");

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

    $('#pager').find('li a').click(function(e) {
        e.preventDefault();
    });

    var wordPager = (function(){
        var n = 15;

        var $pager = $('#pager');
        var $previous = $pager.children().eq(0);
        var $next = $pager.children().eq(1);

        var $rows;
        var beginRow;
        var numRows;

        var next = function() {
            $previous.removeClass('disabled');
            beginRow = beginRow + n;
            var endRow = beginRow + n - 1;

            if(beginRow + n < numRows) {
                $rows.css('display','none').slice(beginRow, endRow + 1).show();
            }
            else {
                $rows.css('display','none').slice(beginRow).show();
                $next.addClass('disabled');
            }
        };

        var previous = function() {
            $next.removeClass('disabled');
            beginRow = beginRow - n;
            var endRow = beginRow + n - 1;

            if(beginRow > 0) {
                $rows.css('display','none').slice(beginRow, endRow + 1).show();
            }
            else {
                $rows.css('display','none').slice(beginRow, endRow + 1).show();
                $previous.addClass('disabled');
            }
        };

        return {
            page: function($list) {
                $rows = $list.children();

                numRows = $rows.size();
                var numPages = Math.ceil(numRows/n);
                var toShow = (numRows === 0) ? 0 : (numRows % n == 0) ? n : (numRows % n);
                beginRow = (numPages - 1) * n;

                $rows.hide();

                for(var i = 0; i < toShow; i++) {
                    var $row = $rows.eq((numPages - 1) * n + i);
                    if(!($row.hasClass('last-word'))) {
                        $row.show();
                    }
                    else {
                        $row.show('slow');
                    }
                }

                $next.addClass('disabled');

                if(numRows > n) {
                    $previous.removeClass('disabled');
                }

                $next.unbind('click').click(function(){
                    if($(this).hasClass('disabled')) {
                        return false;
                    }
                    next();
                    return false;
                });

                $previous.unbind('click').click(function(){
                    if($(this).hasClass('disabled')) {
                        return false;
                    }
                    previous();
                    return false;
                });
            }
        }
    }());

    var toggleTurn = function (user) {
        if (player.username == user) {
            myTurn = true;
            $status.text('Ваш ход!');
        }
        else {
            myTurn = false;
            $status.text('Ходит ' + user + '!');
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
        webSocket.onclose = function(event) {};
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

        if (message.action == 'GAME_STARTED') {

            timer.start();

            $modalWaiting.modal('hide');

            player.username = message.player.username;
            player.rating = Math.round(message.player.rating);
            player.guest = message.player.guest;

            fillLetterContainer(message.givenLetters);
            fillPlayerInfo(message.opponents);
            toggleTurn(message.nextMove);
            if (myTurn) {
                $droppable.droppable('enable');
                $changeLettersBtn.removeClass('disabled');
            }
            return;
        }
        if (message.action == 'PLAYER_MADE_MOVE') {

            var $moveMade = $('.move-made');
            $moveMade.draggable('destroy');
            $moveMade.removeClass('move-made letter-line');
            move = [];
            abortMove = [];
            moveType = '';

            toggleTurn(message.nextMove);

            timer.stop();
            timer.start();

            fillLetterContainer(message.letters);
            fillWordList(message.words, player.username);

            if(myTurn) {
                $changeLettersBtn.removeClass('disabled');
                $droppable.droppable('enable');
            }
            return;
        }
        if (message.action == 'OPPONENT_MADE_MOVE') {
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
                $droppable.droppable('enable');
            }
            return;
        }
        if (message.action == 'PLAYER_CHANGED_LETTERS') {
            $('.letter-line').draggable('enable');
            toggleTurn(message.nextMove);

            $('.last-word').removeClass('last-word');

            timer.stop();
            timer.start();

            replaceChangedLetters(message.changedLetters);

            if(myTurn) {
                $changeLettersBtn.removeClass('disabled');
                $droppable.droppable('enable');
            }
            return;
        }
        if (message.action == 'OPPONENT_CHANGED_LETTERS') {
            toggleTurn(message.nextMove);

            $('.last-word').removeClass('last-word');

            timer.stop();
            timer.start();

            if (myTurn) {
                $changeLettersBtn.removeClass('disabled');
                $droppable.droppable('enable');
            }
            return;
        }
        if (message.action == 'TIME_OVER') {

            var beforeToggle = myTurn;

            toggleTurn(message.nextMove);

            timer.stop();
            timer.start();

            $modalInfo.modal('hide');

            var $letterLine = $('.letter-line');
            $letterLine.draggable('enable');
            $makeMoveBtn.addClass('disabled');

            if (beforeToggle) {
                $droppable.droppable('disable');
                if (moveType === 'CHANGE_LETTERS') {
                    $('#letter-line').tooltip('destroy');
                    for (var i = 0; i < changedLetters.length; i++) {
                        changedLetters[i].removeClass('selected');
                    }
                    $changeLettersDiv.hide('slow');
                    $letterLine.unbind('click');
                }
                else if (moveType === 'MAKE_WORDS') {
                    move = [];
                    for (var j = 0; j < abortMove.length; j++) {
                        moveAnimate(abortMove[j].$letter, abortMove[j].$initialParent);
                    }
                    $makeMoveBtn.addClass('disabled');
                    abortMove = [];
                    $('.move-made').removeClass('move-made');
                }
            }
            if (myTurn) {
                $changeLettersBtn.removeClass('disabled');
                $droppable.droppable('enable');
            }
            return;
        }
        if(message.action === 'GAME_OVER') {
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

            $modalGameover.modal('show');
            timer.stop();
            return;
        }
        if(message.action === 'WRONG_FIRST_MOVE') {

            $droppable.droppable('enable');
            $makeMoveBtn.removeClass('disabled');

            $('#modal-info-header-span').text(' Неверный первый ход!');
            $('#modal-info-body-h5').text(message.message);
            $modalInfo.modal('show');

            return;
        }
        if(message.action === 'INCORRECT_MOVES') {

            $droppable.droppable('enable');
            $makeMoveBtn.removeClass('disabled');

            $('#modal-info-header-span').text(' Буквы выставлены некорректно!');

            var info = message.message + ' ';
            for(var i = 0; i < message.incorrectMoves.length; i++) {
                info += (message.incorrectMoves[i].letter.letter + ', ');
            }
            info = info.slice(0, -2);

            $('#modal-info-body-h5').text(info);
            $modalInfo.modal('show');

            return;
        }
        if(message.action === 'NO_SUCH_WORD') {
            $droppable.droppable('enable');
            $makeMoveBtn.removeClass('disabled');

            $('#modal-info-header-span').text('Слово отсутствует в словаре!');

            var info = message.message + ' ' + message.word;

            $('#modal-info-body-h5').text(info);
            $modalInfo.modal('show');

            return;
        }
        if(message.action === 'WORD_ALREADY_USED' || message.action === 'WORD_USED_TWICE') {
            $droppable.droppable('enable');
            $makeMoveBtn.removeClass('disabled');

            $('#modal-info-header-span').text('Повторное использование слова');

            var info = message.message + ' ' + message.word;

            $('#modal-info-body-h5').text(info);
            $modalInfo.modal('show');

            return;
        }
        if (message.action == 'OPPONENT_QUIT') {
            for(var i = 0; i < playerOpponents.length; i++) {
                if(playerOpponents[i].username === message.opponent) {
                    $("#opponent" + i).addClass('player-disconnected');
                }
            }
            if(message.nextMove !== null) {
                toggleTurn(message.nextMove);
                if(myTurn) {
                    $changeLettersBtn.removeClass('disabled');
                    $droppable.droppable('enable');
                }
                timer.stop();
                timer.start();
            }
        }
    };

    var fillPlayerInfo = function (opponents) {
        $('#player-name').text(player.username);
        $('#player-rating').text(player.rating);
        $('#player').show('slow');
        for (var i = 0; i < opponents.length; i++) {
            playerOpponents[i] = opponents[i];
            playerOpponents[i].rating = Math.round(opponents[i].rating);
            $('#opponent' + i + '-name').text(playerOpponents[i].username);
            $('#opponent' + i + '-rating').text(playerOpponents[i].rating);
            $('#opponent' + i).show('slow');
        }
    };

    var fillWordList = function (words, player) {

        $('.last-word').removeClass('last-word');

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
                            $li.addClass('last-word');
                            var $spanValue = $('<span/>', {class: 'badge'}).text(words[word]);
                            var $spanWord = $('<span/>').text(word);
                            $spanValue.appendTo($li);
                            $spanWord.appendTo($li);
                            $li.appendTo($('#words'));

                            wordPager.page($('#words'));

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

            webSocket.send(JSON.stringify({action: "PLAYER_MADE_MOVE", move: move}));

            $droppable.droppable('disable');
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
            $droppable.droppable('disable');
            $('.letter-line').unbind('click');

            for (var i = 0; i < changedLetters.length; i++) {
                var $letter = changedLetters[i].children(':first');
                var $value = $letter.next();

                sentLetters.push({letter: $letter.text(), value: $value.text()});
            }
            webSocket.send(JSON.stringify({action: 'PLAYER_CHANGED_LETTERS', letters: sentLetters}));
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
        $('#letter-line').tooltip('destroy');
        $changeLettersDiv.hide('slow');
        $changeLettersBtn.removeClass('disabled');
    };

    $makeMoveBtn.click(makeMove);
    $changeLettersBtn.click(changeLetters);

    $sendChangedLettersBtn.click(sendSelectedLetters);
    $cancelChangingLettersBtn.click(cancelSelectingLetters);

    $('#gameover-btn').click(function() {
        window.location.replace('http://' + window.location.host + '/start');
        return false;
    });

    $droppable.droppable({
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
            var addToAbortMove = true;
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
            if (addToAbortMove) {
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