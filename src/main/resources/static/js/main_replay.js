/**
 * Created by Administrator on 2019/1/24.
 */

var board = document.getElementById("board")
//var g_row = 9, g_col = 10;
/*
var chesspos =
    "0000000000" +
    "0JMXSKXMJ0" +
    "0ZPZZZZPZ0" +
    "0000000000" +
    "0000000000" +
    "0000000000" +
    "0000000000" +
    "0zpzzzzpz0" +
    "0jmxksxmj0";
 */
var matrix = new Array(g_row)
var matrixback = new Array(g_row * g_col)
for (var i = 0; i < g_row; i++) {
    matrix[i] = new Array(g_col)
}
var box2 = createBox('box2.png');
box2.style.zIndex = 0; // -9998;
createChess(true);
var box = createBox('box.png');
box.style.zIndex = 99;
var clicknum = 0;
var prepos = null;
var curcolor = 'r';
var clockleftpos;
var clockrightpos;
var curclock;
// 打开回放页
layer.ready(function () {
    layer.open({
        type: 2,
        title: '棋局回放',
        shade: false,
        area: ['15%', '50%'],
        offset: ['330px', '10px'],
        content: '/jsp/replay',
        success: function (layero, index) {
            var cheepId = layer.getChildFrame('#bridge-cheep-id', index);
            cheepId.val($.cookie("record"));
            var inddex = layer.getChildFrame('#bridge-index', index);
            inddex.val($.cookie("index"));
            var max = layer.getChildFrame('#bridge-max', index);
            max.val($.cookie('max'));
            //调用iframe里的函数
            parent.window.frames[0].frameElement.contentWindow.firstLoad();
        }
    });
});
//事件监听 更新坐标
$("#bridge").change(function () {
    updatePos($(this).val());
});

function clickEvent(e) {
    alertclear();
    // clear msg area.

    var x = e.clientX - board.offsetLeft
    var y = e.clientY - board.offsetTop

    var vportsize = getViewPortSize();
    var vScrolloff = getScrollOffset();

    var pos = getChessPos(x + vScrolloff.x, y + vScrolloff.y)

    if (mouseover.charAt(pos.r * g_col + pos.c) != '0')
        return;

    if (!isGo &&
        clicknum % 2 == 0) {
        moveBox(box2, pos)
        prepos = pos

        // alert the chesspiece.
        var s = matrix[prepos.r][prepos.c];
        var fsc = getChessTypeScript(s.ch);
        if (fsc.n != null && fsc.c != null)
            msgalert(fsc.c, 0);
    } else {
        //移动棋子
        var res = isGo ? fcscript_trans(null, pos)
            : checkMove(prepos, pos);
        if (res == 0) {

            if (beforeMoveChessTo(pos, curcolor)) {
                clicknum++;
                return;
            }

            moveChessTo(prepos, pos);
            checkFinish(curcolor);

            if (afterMoveChessTo(pos, curcolor)) {
                return;
            }

            if (clrcheck) {
                if (curcolor == 'b') {
                    curcolor = 'r'
                } else {
                    curcolor = 'b'
                }
            }
        } else if (res == -1) {
            msgalert("没有选中棋子！")
        } else if (res == -2) {
            msgalert("不是你下，是对方下！")
        } else if (res == -3) {
            msgalert("不能吃自己的棋子！")
        } else if (res == -4) {
            msgalert("在原地下子！")
        } else if (res == -5) {
            msgalert("不符合行棋规则！")
        }

        if (res != 0) {
            if (afterCheckMoveErr(pos, curcolor))
                return;
        }

        box.style.display = 'none'
        box2.style.display = 'none'
    }
    clicknum++
}

function checkMove(prepos, pos) {
    var s = matrix[prepos.r][prepos.c]
    var t = matrix[pos.r][pos.c]
    //没有选中棋子
    if (s == null) return -1

    if (clrcheck) {
        //不是你下，是对方下
        if (s.color != curcolor) return -2
    }

    //不能吃自己的棋子
    if (t != null && s.color == t.color) return -3
    //位置没有变化
    if (s == t) return -4

    return fcscript_trans(prepos, pos);
    /*
        if (s.type == 'zu') {
            return zumove(prepos, pos)
        }
        else if (s.type == 'pao') {
            return paomove(prepos, pos)
        }
        else if (s.type == 'ju') {
            return jumove(prepos, pos)
        }
        else if (s.type == 'ma') {
            return mamove(prepos, pos)
        }
        else if (s.type == 'xiang') {
            return xiangmove(prepos, pos)
        }
        else if (s.type == 'shi') {
            return shimove(prepos, pos)
        }
        else if (s.type == 'jiang') {
            return jiangmove(prepos, pos)
        }
    */
}

/*
function jiangmove(prepos, pos){
    var s = matrix[prepos.r][prepos.c]
    var t  = matrix[pos.r][pos.c]
    var dr = pos.r - prepos.r
    var dc = pos.c - prepos.c
    var ar = (dr > 0) ? dr : -dr
    var ac = (dc > 0) ? dc : -dc
    // 距离是1，八个方向。
    if ( ar <= 1 && ac <=1 && (ar==1||ac==1) ) {
        if (t != undefined && s.color == t.color) return -3;
        return 0;
    }
    return -5;
}

function shimove(prepos, pos) {
    if (xiangmove(prepos, pos) == 0 ||           // 等于相
        jumove(prepos, pos) == 0)  return 0;    // + 车。
    return -5;
}

function xiangmove(prepos, pos){
    var dr = pos.r - prepos.r
    var dc = pos.c - prepos.c
    var ar = (dr > 0) ? dr : -dr
    var ac = (dc > 0) ? dc : -dc
//    var mr = (pos.r + prepos.r) / 2
//    var mc = (pos.c + prepos.c) / 2
    if ( ar != ac ) return -5;
    if ( countXLineChess(prepos,pos) == 1 ) return 0
    return -5
}
*/
function countXLineChess(prepos, pos) {
    var r = prepos.r
    var c = prepos.c
    var dr = pos.r - prepos.r
    var dc = pos.c - prepos.c
    var ar = (dr > 0) ? dr : -dr
    var ac = (dc > 0) ? dc : -dc
    if (ar != ac) return -1;

    var ret = 0, cnt = 0;
    for (cnt = 0; cnt < ar; cnt++) {
        if (dr > 0 && dc > 0) {
            if (matrix[r + cnt][c + cnt] != null) ret++
        } else if (dr > 0 && dc < 0) {
            if (matrix[r + cnt][c - cnt] != null) ret++
        } else if (dr < 0 && dc > 0) {
            if (matrix[r - cnt][c + cnt] != null) ret++
        } else if (dr < 0 && dc < 0) {
            if (matrix[r - cnt][c - cnt] != null) ret++
        }
    }
    return ret
}

/*
function mamove(prepos, pos){
    var dr = pos.r - prepos.r
    var dc = pos.c - prepos.c
    var ar = (dr > 0) ? dr : -dr
    var ac = (dc > 0) ? dc : -dc
    var mr = (pos.r + prepos.r) / 2
    var mc = (pos.c + prepos.c) / 2
    if (ar == 2 && ac == 1) return 0
    if (ac == 2 && ar == 1) return 0
    return -5
}

function jumove(prepos, pos){
    var dr = pos.r - prepos.r
    var dc = pos.c - prepos.c
    if (dr != 0 && dc != 0) {
        return -5
    }
    else {
        var cnt = countLineChess(prepos, pos)
        if (cnt == 1 ) return 0
        else return -5
    }
}
function paomove(prepos, pos) {
    return zumove(prepos, pos);
}
*/
function countLineChess(prepos, pos) {
    var r = prepos.r
    var c = prepos.c
    var cnt = 0
    if (r != pos.r) {
        while (r != pos.r) {
            if (matrix[r][c] != null) cnt++
            if (r < pos.r) r++
            if (r > pos.r) r--
        }
//        if (matrix[r][c] != null) cnt++ // 不检测目标是否有棋子。
    } else {
        while (c != pos.c) {
            if (matrix[r][c] != null) cnt++
            if (c < pos.c) c++
            if (c > pos.c) c--
        }
//        if (matrix[r][c] != null) cnt++   // 不检测目标是否有棋子。
    }
    return cnt
}

/*
function zumove(prepos, pos){
    var s = matrix[prepos.r][prepos.c]
    var t  = matrix[pos.r][pos.c]
    var dr = pos.r - prepos.r
    var dc = pos.c - prepos.c
    var ar = (dr > 0) ? dr : -dr
    var ac = (dc > 0) ? dc : -dc
    //一次只能一步，不能后退
    if (ar > 1 || ac > 1)
    {   // 如果第一步，则可以是两步
        if (s.color == 'b' && prepos.r == 2 && dr == 2 ) return 0
        if (s.color == 'r' && prepos.r == 7 && dr == -2 ) return 0
        return -5;
    }
    // 红棋只能向右，黑棋只能向左。
    if (s.color == 'r' && dr >= 0 ) return -5
    if (s.color == 'b' && dr <= 0 ) return -5
    // 斜45度。
    if (ar == 1 && ac == 1)
    {   // 目标点必须有棋子，
        if ( t != undefined )
        {   // 而且必须不同。
            if (s.color == 'b' && t.color == 'r' ) return 0;
            if (s.color == 'r' && t.color == 'b' ) return 0;
        }
        return -5;
    }
    // 向前一步，前方不能有子。
    if (t!=undefined) return -5;
    return 0
}
*/
function moveChessTo(prepos, pos) {
    var chess = isGo ? goCurChess : matrix[prepos.r][prepos.c];
    if (chess == null) return
    var chessto = matrix[pos.r][pos.c]
    if (chessto != null) {
        chessto.img.style.display = 'none'
    }
    matrix[pos.r][pos.c] = chess
    if (!isGo) matrix[prepos.r][prepos.c] = null

    if (gIsRectBoard) {
//        chess.x = c * g_rsize ;// + g_rsize/2 - g_row*15 - ((50-20)/2);
//        chess.y = r * ( g_rsize );// + 30
        chess.x = (pos.c + 1/*2*(11-g_col + 1 + 1)*/) * g_rsize; //  + r * 30;// + 30 - g_row*15 - ((50-20)/2);
        chess.y = (pos.r + /*(g_col-g_row)/2 -*/ 1 / 2) * g_rsize; // ( 60 - 7 );// + 30
    } else {
        chess.x = pos.c * 60 + pos.r * 30 - 1; // - g_row*15 + 30 - ((50-20)/2);
        chess.y = pos.r * (60 - 7) - 1;
    }
    chess.img.style.left = chess.x + "px"
    chess.img.style.top = chess.y + "px"

    chess.c = pos.c;
    chess.r = pos.r;
    //
//    if ( isGo ) updateRedBlack();
    // move toe check finish.

    // record the clock.
    recordtheclock(chess, prepos);
}


function mouseoverEvent(e) {
    var x = e.clientX - board.offsetLeft
    var y = e.clientY - board.offsetTop

    var vportsize = getViewPortSize();
    var vScrolloff = getScrollOffset();

    mouseovereventscriput(x + vScrolloff.x, y + vScrolloff.y);
    /*
        if ( x>=30 && x<=30+60*9 &&
            y>=60 && y<=60*8 ) {

            var pos = getChessPos(x, y)
            if (clicknum % 2 == 0) {
                moveBox(box2, pos)
            }
            else {
                moveBox(box, pos)
            }
        }
    */
}


function getChessPos(x, y) {
    var ty, r, tx, c;
    if (gIsRectBoard) {
        /*        ty = Math.abs(y+1)
                r = Math.floor(ty / g_rsize)
                tx = Math.abs(x - g_rsize/2 + g_row*15 + (g_rsize/2)/2) )
                c = Math.floor(tx / g_rsize)
        */
        x -= (/* 2*(11-g_col+1+1)*/1 - 1 / 2) * g_rsize;
        y -= (/*(g_col-g_row)/2 - */1 / 2) * g_rsize;

        var tx = Math.abs(x - g_rsize / 2)
        var c = Math.floor(tx / g_rsize)
        var ty = Math.abs(y)
        var r = Math.floor(ty / g_rsize)
    } else {
        ty = Math.abs(y + 1)
        r = Math.floor(ty / (60 - 7))
        tx = Math.abs(x - r * 30 + 1/* - 30 + g_row*15 + ((50-20)/2)*/)
        c = Math.floor(tx / 60)
    }
    r = (r > g_row - 1) ? g_row - 1 : r
    c = (c > g_col - 1) ? g_col - 1 : c
    return {r: r, c: c}
}

function moveBox(box, pos) {
    var x, y;
    if (gIsRectBoard) {
        x = pos.c * g_rsize - 1;// + 30 - g_row*15 - ((50-20)/2);
        y = pos.r * (g_rsize) - 1;
        x += (/* 2*(11-g_col + 1+ 1) */1) * g_rsize;
        y += (/*(g_col-g_row)/2 - */1 / 2) * g_rsize;
    } else {

        x = pos.c * 60 + pos.r * 30 - 1;// + 30 - g_row*15 - ((50-20)/2);
        y = pos.r * (60 - 7) - 1;
    }
    box.style.top = y + 'px'
    box.style.left = x + 'px'
    box.style.display = 'block'
//    box.style.zIndex = 9999;
}

function createBox(name) {
    var img = new Image()
    img.src = '/img/' + name
    board.appendChild(img)
    img.style.display = 'none'
    img.style.position = 'absolute'
    return img
}

function createChessOne(r, c, ch) {
    var color = 'b';
    if (ch >= 'a' && ch <= 'z') {
        color = 'r';
    }
    var type = getChessType(ch);
    var img_url = "/img/" + color + "_" + type + ".png";
    var img = new Image();
    img.src = img_url;
    img.className = 'chess';
    board.appendChild(img);
    var chess = {};
    chess.ch = ch;
    chess.r = r;
    chess.c = c;
    chess.color = color;
    chess.img = img;
    chess.type = type;
    if (gIsRectBoard) {
//        chess.x = c * g_rsize ;// + g_rsize/2 - g_row*15 - ((50-20)/2);
//        chess.y = r * ( g_rsize );// + 30
        chess.x = (c + 1/*2*(11-g_col + 1 + 1) */) * g_rsize; //  + r * 30;// + 30 - g_row*15 - ((50-20)/2);
        chess.y = (r + /*(g_col-g_row)/2 - */1 / 2) * g_rsize; // ( 60 - 7 );// + 30
    } else {
        chess.x = c * g_rsize + r * g_rsize / 2;// + g_rsize/2 - g_row*15 - ((50-20)/2);
        chess.y = r * (g_rsize - 7);// + 30
    }

    img.style.position = 'absolute';
    img.style.top = chess.y + "px";
    img.style.left = chess.x + "px";
    return chess;
}

function createChess(first) {
    for (var r = 0; r < g_row; r++) {
        for (var c = 0; c < g_col; c++) {

            var chess = {}

            if (gIsRectBoard) {
                chess.x = (c + 1/*2 * (11 - g_col + 1 + 1)*/) * g_rsize; //  + r * 30;// + 30 - g_row*15 - ((50-20)/2);
                chess.y = (r + /*(g_col - g_row) / 2 - */1 / 2) * g_rsize; // ( 60 - 7 );// + 30
            } else {
                chess.x = c * 60 + r * 30;// + 30 - g_row*15 - ((50-20)/2);
                chess.y = r * (60 - 7);// + 30
            }
            var i = g_col * r + c

            if (c == 0 && r == 0 && first) {
                img = new Image();
                if (gIsRectBoard) {
                    img.src = "/img/RECTpaper.png";
                } else
                    img.src = "/img/HEXpaper.png";
                img.style.position = 'absolute'
                if (gIsRectBoard) {
                    img.style.top = (chess.y - g_rsize * 2 - (595 - g_rsize * (g_row - 1)) / 2) + "px"
                } else
                    img.style.top = (chess.y - 1 - 3) + "px"
                if (gIsRectBoard) {
                    img.style.left = (chess.x - (842 - g_rsize * (g_col + 1 - 1)) / 2) + "px"
                } else
                    img.style.left = (chess.x - 1 + 80 + 2) + "px"
                img.style.zIndex = -97;
                img.setAttribute("id", "hexpaper");
                board.appendChild(img);
            }

            if (boardrects[i] == '0' || gIsRectBoard) {
                var bimg = {};
                // addboard.
                img = new Image();
                img.src = "/img/box3_70x70.png";
                img.style.position = 'absolute'
                img.style.top = (chess.y - 1) + "px"
                img.style.left = (chess.x - 1) + "px"
                img.style.zIndex = -99;
                board.appendChild(img);
                bimg.b = img;

                var led_line_x = -30 + 3;
                var led_line_y = 8 + 12;
                if (c == 0 && r < 14) {
                    img = new Image();
                    img.src = "/img/" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"[r] + ".png";
                    img.style.position = 'absolute'
                    img.style.top = (chess.y - 1) + led_line_y + "px"
                    img.style.left = (chess.x - 1) + led_line_x + "px"
                    img.style.zIndex = -99;
                    img.width = 18;
                    board.appendChild(img);

                    if (r == 13) {
                        clockleftpos = getScrollOffset();
                        clockleftpos.y = (chess.y - 1) + led_line_y;
                        clockleftpos.x = (chess.x - 1) + led_line_x;
                    }
                }
                led_line_x = -30 + 2 + 100;
                led_line_y = 8 + 12;
                if (c == g_col - 1 && r < 14) {
                    img = new Image();
                    img.src = "/img/" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"[r] + ".png";
                    img.style.position = 'absolute'
                    img.style.top = (chess.y - 1) + led_line_y + "px"
                    img.style.left = (chess.x - 1) + led_line_x + "px"
                    img.style.zIndex = -99;
                    img.width = 18;
                    board.appendChild(img);

                    if (r == 13) {
                        clockrightpos = getScrollOffset();
                        clockrightpos.y = (chess.y - 1) + led_line_y;
                        clockrightpos.x = (chess.x - 1) + led_line_x;

                        curclock = clockrightpos;

                        setInterval(function () {
                            // read the last 0
                            // check which clock.
                            // show the clock.
                            if (curclock == clockrightpos) { // show right.
                            } else if (curclock == clockleftpos) { // show left.
                            }
                        }, 1000);
                    }
                }
                // addnum.
                img = new Image();
                img.src = "/img/" + (c > 9 ? 1 : 0).toString() + ".png";
                img.style.position = 'absolute'
                img.style.top = (chess.y - 1 + 11 + 2) + "px"
                img.style.left = (chess.x - 1 + 30 - 11 - 5 - 3) + "px"
                img.style.height = "11px";
                img.style.zIndex = -98;
                board.appendChild(img);
                bimg.n1 = img;

                var label_digi_x = 10;
                var label_digi_y = -30;
                if (r == 0) {
                    img = new Image();
                    img.src = "/img/" + (c > 9 ? 1 : 0).toString() + ".png";
                    img.style.position = 'absolute'
                    img.style.top = (chess.y - 1 + 11 + 2 + label_digi_y) + "px"
                    img.style.left = (chess.x - 1 + 30 - 11 - 5 - 3 + label_digi_x) + "px"
                    img.style.height = "11px";
                    img.style.zIndex = -98;
                    board.appendChild(img);

                    img = new Image();
                    img.src = "/img/" + (c > 9 ? c - 10 : c).toString() + ".png";
                    img.style.position = 'absolute'
                    img.style.top = (chess.y - 1 + 11 + 2 + label_digi_y) + "px"
                    img.style.left = (chess.x - 1 + 30 - 5 - 6 + label_digi_x) + "px"
                    img.style.height = "11px";
                    img.style.zIndex = -98;
                    board.appendChild(img);
                }

                img = new Image();
                img.src = "/img/" + (c > 9 ? c - 10 : c).toString() + ".png";
                img.style.position = 'absolute'
                img.style.top = (chess.y - 1 + 11 + 2) + "px"
                img.style.left = (chess.x - 1 + 30 - 5 - 6) + "px"
                img.style.height = "11px";
                img.style.zIndex = -98;
                board.appendChild(img);
                bimg.n2 = img;
                matrixback[i] = bimg;
            }

            var ch;
            var cmdi = getQueryString("i");
            if (cmdi != null && cmdi.length == g_row * g_col) {
                ch = cmdi[i];
                if (ch == '0') continue;
                matrix[r][c] = createChessOne(r, c, ch);
            } else {

                ch = chesspos[i]
                if (ch == '0') continue
                /*            var color = 'b'
                 if (ch >= 'a' && ch <= 'z') {
                 color = 'r'
                 }
                 var type = getChessType(ch)
                 var img_url = "/img/"+color+"_"+type+".png"
                 var img = new Image()
                 img.src = img_url
                 img.className = 'chess'
                 board.appendChild(img)

                 chess.ch = ch;
                 chess.r = r
                 chess.c = c
                 chess.color = color
                 chess.img = img
                 chess.type = type
                 img.style.position = 'absolute'
                 img.style.top = chess.y+"px"
                 img.style.left = chess.x + "px" */
                matrix[r][c] = createChessOne(r, c, ch); // chess
            }
        }
    }
    if (first) {
        var cmdi = getQueryString("i");
        if (cmdi != null && cmdi.length == g_row * g_col) {
            RecordChessMove(cmdi);
        }
    }
    // creating monit.
//    setTimeout(function () {

//    }, 10);

    // setInterval(function () {
    //     //     RecordChessMove("");
    //     // }, 1000);
}

function getChessType(ch) {
    var fsc = getChessTypeScript(ch);
    return fsc.n;
    /*
        if (ch == 'j' || ch == 'J') return 'ju'
        if (ch == 'm' || ch == 'M') return 'ma'
        if (ch == 'x' || ch == 'X') return 'xiang'
        if (ch == 's' || ch == 'S') return 'shi'
        if (ch == 'k' || ch == 'K') return 'jiang'
        if (ch == 'p' || ch == 'P') return 'pao'
        if (ch == 'z' || ch == 'Z') return 'zu'
    */
}

initjscript();

/**
 * 获取浏览器视口的大小（显示文档的部分）
 *
 */
function getViewPortSize() {
    // 除IE8及更早的版本以外的浏览器
    if (window.innerWidth != null) {
        return {
            w: window.innerWidth,
            h: window.innerHeight
        }
    }
    // 标准模式下的IE
    if (document.compatMode == "css1Compat") {
        return {
            w: document.documentElement.clientWidth,
            h: document.documentElement.clientHeight
        }
    }
    // 怪异模式下的浏览器
    return {
        w: document.body.clientWidth,
        h: document.body.clientHeight
    }
}

/**
 *  获取窗口滚动条的位置
 */
function getScrollOffset() {
    // 除IE8及更早版本
    if (window.pageXOffset != null) {
        return {
            x: window.pageXOffset,
            y: window.pageYOffset
        }
    }
    // 标准模式下的IE
    if (document.compatMode == "css1Compat") {
        return {
            x: document.documentElement.scrollLeft,
            y: document.documentElement.scrollTop
        }
    }
    // 怪异模式下的浏览器
    return {
        x: document.body.scrollLeft,
        y: document.body.scrollTop
    }
}
