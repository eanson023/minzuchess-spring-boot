/*
var __encode = 'sojson.com',
    _0xb483 = ["_decode",
        "http://www.sojson.com/javascriptobfuscator.html"];
(function(_0xd642x1) {
    _0xd642x1[_0xb483[0]] = _0xb483[1]
})(window);
var __Ox3ad4e = [

    "myC",     // 0x0
    "getElementById",       // 0x1
    "fill",                  // 0x2
    "click",                 // 0x3
    "concat",               // 0x4
    "x",                     // 0x5
    "pow",                  // 0x6
    "y",                    // 0x7
    "move()",              // 0x8
    "addEventListener",  // 0x9
    "2d",                  // 0xa
    "getContext",        // 0xb
    "fillStyle",         // 0xc
    "#FF0000",           // 0xd
    "beginPath",         // 0xe
    "PI",                 // 0xf
    "arc",                // 0x10
    "closePath",         // 0x11
    "#0000FF",           // 0x12
    "layerX",            // 0x13
    "layerY",            // 0x14
    "offsetX",           // 0x15
    "offsetY",           // 0x16
    "floor",             // 0x17
    "cheep",               // 0x18
    "",                  // 0x19
    "n",                 // 0x1a
    "r",                 // 0x1b
    "b",                 // 0x1c
    "http://us2.409dostastudio.pw:20001/moveChess",     // 0x1d

    "movePosition",    // 0x1e
    "move_info",       // 0x1f

    "alert('红方获胜!')",   // 0x20
    "alert('蓝方获胜!')",   // 0x21

    "isEnd",            // 0x22

    "json",             // 0x23
    "post"            // 0x24

];*/
var myC = document.getElementById("myC");
var startFlag = 0;
var redPlayer = 0;
var redChess = 0;
var bluePlayer = 1;
var blueChess = 1;
var currentPlayer = redPlayer;
var selectPlayer = redPlayer;
var chessNum = ChessboardSize * ChessboardSize;
var turns = 0;
var board = new Array(121).fill(-1);

myC.addEventListener('click',
    function(e) {
        var pos = getPosition(e);
        var rad = CanvasWidth * 2 / (7 * ChessboardSize);
        var _0x6495xf = rad;
        var _0x6495x10 = rad * 3 / 4;
        var _0x6495x11 = CanvasWidth / 12,
            _0x6495x12 = CanvasWidth / 12;
        var x = _0x6495x11,
            y = _0x6495x12;
        var _0x6495x15 = [].concat(board);
        for (var i = 0; i < ChessboardSize; i++) {
            for (var j = 0; j < ChessboardSize; j++) {
                var x = _0x6495x11 + rad * i + rad * 2 * j + rad,
                    y = _0x6495x12 + (_0x6495xf + _0x6495x10) * i + _0x6495xf / 2;
                if (Math.pow(pos.x - x, 2) +
                    Math.pow(pos.y - y, 2) <=
                    Math.pow(rad * 9 / 10, 2))
                {
                    if (board[i * ChessboardSize + j] != -1) {
                        return
                    };
                    chessMove(i * ChessboardSize + j);
                    setTimeout(move(), 10);
                    break
                };
                x += rad * 2
            };
            if (j < ChessboardSize) {
                break
            }
        }
    },
    false);

function draw(row,
              col) {
    let canvas = document.getElementById("myC");
    let context = canvas.getContext("2d");
    var rad = CanvasWidth * 2 / (7 * ChessboardSize);
    var _0x6495xf = rad;
    var _0x6495x10 = rad * 3 / 4;
    var _0x6495x11 = CanvasWidth / 12,
        _0x6495x12 = CanvasWidth / 12;
    var x = _0x6495x11 + rad * row + rad * 2 * col + rad,
        y = _0x6495x12 + (_0x6495xf + _0x6495x10) * row + _0x6495xf / 2;
    if (currentPlayer == redPlayer) {
        context.fillStyle = "#FF0000";
        context.beginPath();
        context.arc(x,
            y,
            rad * 6 / 7,
            0,
            Math.PI * 2,
            true);
        context.closePath();
        context.fill()
    } else {
        context.fillStyle = "#0000FF";
        context.beginPath();
        context.arc(x,
            y,
            rad * 6 / 7,
            0,
            Math.PI * 2,
            true);
        context.closePath();
        context.fill()
    }
}

function getPosition(e) {
    var x,
        y;
    if (e.layerX || e.layerY == 0) {
        x = e.layerX;
        y = e.layerY;
    } else {
        if (e.offsetX || e.offsetY == 0) {
            x = e.offsetX;
            y = e.offsetY;
        }
    };
    return {
        x: x,
        y: y
    }
}

function chessMove(i) {
    board[i] = currentPlayer;
    turns = turns + 1;
    draw(Math.floor(i / ChessboardSize), i % ChessboardSize);
    currentPlayer = 1 - currentPlayer
}

function startGame() {
    if (startFlag == 0) {
        startFlag = 1;
        turns = 0;
        console.log(selectPlayer);
        currentPlayer = redPlayer;
        if (selectPlayer == 1 - currentPlayer) {
            move()
        }
    }
}

function restartGame() {
    startFlag = 0;
    load(CanvasWidth,
        CanvasHeight,
        ChessboardSize)
}

function SetBoardSize(size) {
    ChessboardSize = size;
    restartGame();
}

function selectRed() {
    if (startFlag == 0) {
        selectPlayer = redPlayer
    }
}

function selectBlue() {
    if (startFlag == 0) {
        selectPlayer = bluePlayer
    }
}

function move() {
    var bds = "";
    for (var i in board) {
        if (board[i] == -1) {
            bds += "n";
        };
        if (board[i] == 0) {
            bds += "r";
        };
        if (board[i] == 1) {
            bds += "b";
        }
    };
    const url = "http://us2.409dostastudio.pw:20001/moveChess";
    const rec = {
        "board": bds,
        "turns": turns
    };
    $.post(url,
        rec,
        function(callback) {
            console.log(callback);
            if (callback.move_info.movePosition == -1) {
                if (1 - currentPlayer == redPlayer) {
                    setTimeout("alert('红方获胜!')", 10);
                } else {
                    setTimeout("alert('蓝方获胜!')", 10);
                }
            } else {
                chessMove(callback.move_info.movePosition);
                if (callback.move_info.isEnd == true) {
                    if (1 - currentPlayer == redPlayer) {
                        setTimeout("alert('红方获胜!')", 10);
                    } else {
                        setTimeout("alert('蓝方获胜!')", 10);
                    }
                }
            }
        },
        "json")
}