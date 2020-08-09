function jcheckFinish(clr) {
    /*
        if ( clr == 'b' ) {
            if ( matrix[0][5] != null &&
                 matrix[0][5].color == 'b' )
                msgalert( "黑方（蓝）胜，抵达红方老巢。", 0 );
        } else
        if ( clr == 'r') {
            if ( matrix[8][5] != null &&
                matrix[8][5].color == 'r' )
                msgalert( "红方（黄）胜，抵达黑方老巢。", 0 );
        }*/
    /*
        if ( isGo ) {
            updateRedBlack( true );
        }

        var sz = getBoardSize();
        if ( clr == 'b' ) {
            searchpathsuccessblue( sz );
        } else
        if ( clr == 'r' ) {
            searchpathsuccessred( sz );
        }*/
}

function matrix2str() {
    var i, j;
    var str = "";
    for (j = 0; j < g_row; j++)
        for (i = 0; i < g_col; i++) {
            var p = matrix[j][i];
            if (p == null)
                str += '0';
            else
                str += p.ch;
        }
    return str;
}

function afterMoveChessTo(pos, clr) {

    prepairChessPort();

    var str = matrix2str();
    RecordChessMove(str);

    return 0;   // to switch to next color.
}

function beforeMoveChessTo(pos, clr) {

    return 0;   // to switch to next color.
}

function afterCheckMoveErr(pos, curcolor) {

    return 1;   // if err not increase click number.
}


function fcscript_logo_onclick(idx) {
    alertclear();
//    msgalert( "logo#" + idx.toString() + "被按下！");
    var msg = "北京市青少年智力大会'海克斯棋'HEX专用棋纸。"
    if (idx == 3) {
        // hidden the hexpaper.
        var p = document.getElementById("hexpaper");
        if (p.style.display == 'none') {
            p.style.display = 'block';
            msgalert( /*"显示" +*/ msg, 0);
        } else {
            p.style.display = 'none';
            msgalert( /*"隐藏" +*/ msg, 0);
        }
    }
}

goRedChess = null;
goBlackChess = null;

function updateRedBlack(rev) {
    if (isGo) {

        if (!rev) {

            if (curcolor == 'r')
                if (goRedChess == null || goRedChess.c != -2 || goRedChess.r != 5)
                    goRedChess = createChessOne(5, -2, goRed);

            if (curcolor == 'b')
                if (goBlackChess == null || goBlackChess.c != 12 || goBlackChess.r != 5)
                    goBlackChess = createChessOne(5, 12, goBlack);
        } else {
            if (curcolor == 'b')
                if (goRedChess == null || goRedChess.c != -2 || goRedChess.r != 5)
                    goRedChess = createChessOne(5, -2, goRed);

            if (curcolor == 'r')
                if (goBlackChess == null || goBlackChess.c != 12 || goBlackChess.r != 5)
                    goBlackChess = createChessOne(5, 12, goBlack);

        }
    }
}

function createGoS(ch) {
    if (ch >= 'a' && ch <= 'z') {
        goCurChess = goRedChess;
    } else
        goCurChess = goBlackChess;
    return goCurChess;
}

function getBoardSize() {
    var s = document.getElementById("size").innerHTML;
    return parseInt(s.substr(19, 2));
}

function initjscript() {
    if (isGo) {
        updateRedBlack(false);
    }

    var size = document.getElementById("size");
    if (!gHasPC)
        size.style.display = 'none';
    else {
        size.innerHTML = "<img src='/img/bs11.png'>";
        size.onclick = function (e) {
            var s = document.getElementById("size").innerHTML;
            s = s.substr(19, 2);
            var n;
            switch (s) {
                case '11':
                    n = '9';
                    break;
                case '9.':
                    n = '7';
                    break;
                case '7.':
                    n = '6';
                    break;
                case '6.':
                    n = '5';
                    break;
                case '5.':
                    n = '4';
                    break;
                case '4.':
                    n = '11';
                    break;
                default:
                    msgalert("err size url:" + s);
            }
            s = "<img src='/img/bs" + n.toString() + ".png'>";
            document.getElementById("size").innerHTML = s;
            setsize(n);
            if (n != '6') setpcgray();
            else setpcgray('blue');

        };
    }

    var pc = document.getElementById("pc");
    if (!gHasPC)
        pc.style.display = 'none';
    else {
        pc.innerHTML = "<img src='/img/pcgray.png'>";
        pc.onclick = function (e) {
            if (getBoardSize() != 6)
                return;
            var s = document.getElementById("pc").innerHTML;
            s = s.substr(19, 4);
            switch (s) {
                case 'gray':
                    s = 'yellow';
                    break;
                case 'yell':
                    s = 'blue';
                    break;
                case 'blue':
                    s = 'both';
                    break;
                case 'both':
                    if (e.x > 172 && e.x < 172 + 38 &&
                        e.y > 430 && e.y < 430 + 40) {
                        setTimeout(movenodes(), 10);
                        s = 'play';
                    } else
                        s = 'gray';
                    break;
                case 'play':
                    s = 'gray';
                    break;
                default:
                    msgalert("err pccolor url:" + s);
            }
            s = "<img src='/img/pc" + s + ".png'>";
            document.getElementById("pc").innerHTML = s;
            /*
             var play = document.getElementById( "play");
             //        play.innerHTML = "<img src='/img/play.png'>";
             */
            // check the yellow.
            if (getpcgray() == 'yell') //{
                setTimeout(movenodes(), 10);
            /*            play.style.display = 'none';
             } else if ( getpcgray() == 'both' ) {
             play.style.display = 'block';
             } else
             play.style.display = 'none';
             */
        }
    }
}

function getpcgray() {
    var s = document.getElementById("pc").innerHTML;
    return s.substr(19, 4);
}

function setpcgray(clr = 'gray') {
    var pc = document.getElementById("pc");
    pc.innerHTML = "<img src='/img/pc" + clr + ".png'>";
}

var boardrects11 =
    "00000000000" +
    "00000000000" +
    "00000000000" +
    "00000000000" +
    "00000000000" +
    "00000000000" +
    "00000000000" +
    "00000000000" +
    "00000000000" +
    "00000000000" +
    "00000000000";

var boardrects9 =
    "-----------" +
    "-000000000-" +
    "-000000000-" +
    "-000000000-" +
    "-000000000-" +
    "-000000000-" +
    "-000000000-" +
    "-000000000-" +
    "-000000000-" +
    "-000000000-" +
    "-----------";

var boardrects7 =
    "-----------" +
    "-----------" +
    "--0000000--" +
    "--0000000--" +
    "--0000000--" +
    "--0000000--" +
    "--0000000--" +
    "--0000000--" +
    "--0000000--" +
    "-----------" +
    "-----------";

var boardrects6 =
    "-----------" +
    "-----------" +
    "--000000---" +
    "--000000---" +
    "--000000---" +
    "--000000---" +
    "--000000---" +
    "--000000---" +
    "-----------" +
    "-----------" +
    "-----------";

var boardrects5 =
    "-----------" +
    "-----------" +
    "--00000----" +
    "--00000----" +
    "--00000----" +
    "--00000----" +
    "--00000----" +
    "-----------" +
    "-----------" +
    "-----------" +
    "-----------";

var boardrects4 =
    "-----------" +
    "-----------" +
    "--0000-----" +
    "--0000-----" +
    "--0000-----" +
    "--0000-----" +
    "-----------" +
    "-----------" +
    "-----------" +
    "-----------" +
    "-----------";

function setsize(n) {
    switch (n) {
        case '11':
            boardrects = boardrects11;
            break;
        case '9':
            boardrects = boardrects9;
            break;
        case '7':
            boardrects = boardrects7;
            break;
        case '6':
            boardrects = boardrects6;
            break;
        case '5':
            boardrects = boardrects5;
            break;
        case '4':
            boardrects = boardrects4;
            break;
        default:
            return;
    }
    reload();
}

function reload() {
    mouseoverrectinit(boardrects);
    clearChess();
    createChess(false);
    clicknum = 0
    prepos = null
    curcolor = 'r'
}

function deleteChess(r, c, delback) {
    var chess = matrix[r][c];
    if (chess != undefined && chess != null) {
        board.removeChild(chess.img);
        delete matrix[r][c];
        matrix[r][c] == null;
    }

    if (!delback) return;

    var bimg = matrixback[r * g_col + c];
    if (bimg != undefined && bimg != null) {
        board.removeChild(bimg.b);
        board.removeChild(bimg.n1);
        board.removeChild(bimg.n2);
        delete bimg;
        matrixback[r * g_col + c] = null;
    }
}

function clearChess() {
    for (var r = 0; r < g_row; r++) {
        for (var c = 0; c < g_col; c++) {
            deleteChess(r, c, true);
        }
    }
}

function copymatrixarray2checkfinish(sz) {
    // search from up to down.
    for (var r = 0; r < g_row; r++) {
        for (var c = 0; c < g_col; c++) {
            if (boardrects[r * g_col + c] == '0') {
                break; // find the 1st line and 1st x.
            }
        }
        if (c < g_col) break;
    }
    var nodes = new Array(sz * sz);
    for (var i = 0; i < sz; i++) {
        for (var j = 0; j < sz; j++) {   // see path.gif.
            var node = {}
            node.r = i;
            node.c = j;
            var x = node.c - 1;
            var y = node.r;
            node._ = new Array(6);
            node._[0] = x < 0 ? -1 : (x + y * sz);
            x = node.c;
            y = node.r - 1;
            node._[1] = y < 0 ? -1 : (x + y * sz);
            x = node.c + 1;
            y = node.r - 1;
            node._[2] = (y < 0 || x >= sz) ? -1 : (x + y * sz);
            x = node.c + 1;
            y = node.r;
            node._[3] = x >= sz ? -1 : (x + y * sz);
            x = node.c;
            y = node.r + 1;
            node._[4] = y >= sz ? -1 : (x + y * sz);
            x = node.c - 1;
            y = node.r + 1;
            node._[5] = (x < 0 || y >= sz) ? -1 : (x + y * sz);
            //
            // check matrix.
            node.ch = matrix[r + i][c + j] != undefined ? matrix[r + i][c + j].color : '-';
            node.pos = {r: r + i, c: c + j};
            //
            nodes[i * sz + j] = node;
        }
    }
    return nodes;
}

function searchpathsuccessred(sz) {
    var nodes = copymatrixarray2checkfinish(sz)
    for (var r = 0; r < sz; r++) {
        if (nodes[r].ch != 'r') continue;
        var n = getnextnode(-1, nodes, r);
        if (n != null && n.r == sz - 1) {
            msgalert("红方（黄）胜，连通棋盘上下两边。", 0);
            break;
        }
    }
    if (sz == 6 && (getpcgray() == 'blue' || getpcgray() == 'play'))
        setTimeout(movenodes(), 10);
}

function searchpathsuccessblue(sz) {
    // search from left to right.
    var nodes = copymatrixarray2checkfinish(sz)
    for (var c = 0; c < sz; c++) {
        if (nodes[c * sz].ch != 'b') continue;
        var n = getnextnode(-1, nodes, c * sz);
        if (n != null && n.c == sz - 1) {
            msgalert("黑方（蓝）胜，连通棋盘左右两边。", 0);
            break;
        }
    }
    if (sz == 6 && (getpcgray() == 'yell' || getpcgray() == 'play'))
        setTimeout(movenodes(), 10);
}

function getnextnode(last, nds, cur) {
    var clr = nds[cur].ch;
    var branchret = null;
    if (clr == '-') return null;
    for (var i = 0; i < 6; i++) {
        var nd = nds[cur]._[i];
        if (nd != -1 && nd != last && nds[nd].ch == clr) {
            var newbranchret = getnextnode(cur, nds, nd);
            if (branchret == null) {
                branchret = newbranchret;
                return branchret;   // terminate directly. by old method.
            } else {
                if (clr == 'r') {
                    // lower is the ret.
                    if (branchret.r < newbranchret.r)
                        branchret = newbranchret;
                } else if (clr == 'b') {
                    // righter is the ret.
                    if (branchret.c < newbranchret.c)
                        branchret = newbranchret;
                }
            }
        }
    }
    if (branchret != null) return branchret;
    return nds[cur];
}

function movenodes() {
    var nodes = copymatrixarray2checkfinish(6);
    move(nodes);
}

function move(nds) {
    var bds = "";
    var turns = 0;
    for (var i in nds) {
        if (nds[i].ch == '-') {
            bds += "n";
        }
        ;
        if (nds[i].ch == 'r') {
            bds += "r";
            turns++;
        }
        ;
        if (nds[i].ch == 'b') {
            bds += "b";
            turns++;
        }
    }
    ;
    const url = "http://us2.409dostastudio.pw:20001/moveChess";
    const rec = {
        "board": bds,
        "turns": turns
    };
    $.post(url,
        rec,
        function (callback) {
            console.log(callback);
            if (callback.move_info.movePosition == -1) {
                /*                if (1 - currentPlayer == redPlayer) {
                                    setTimeout("alert('红方获胜!')", 10);
                                } else {
                                    setTimeout("alert('蓝方获胜!')", 10);
                                }
                */
                if (curcolor == 'b') {
                    msgalert("红方（黄）胜，连通棋盘上下两边。", 0);
                } else {
                    msgalert("黑方（蓝）胜，连通棋盘左右两边。", 0);
                }
            } else {
                var pos = {};
                pos.r = Math.floor(callback.move_info.movePosition / 6);
                pos.c = callback.move_info.movePosition - pos.r * 6;
                pos.r += 2;
                pos.c += 2;

                fcscript_trans(null, pos);
                moveChessTo(null, pos);
                jcheckFinish(curcolor);

                if (callback.move_info.isEnd == true) {
                    //                   if (1 - currentPlayer == redPlayer) {
                    //                       setTimeout("alert('红方获胜!')", 10);
                    //                   } else {
                    //                       setTimeout("alert('蓝方获胜!')", 10);
                    //                   }
                    if (curcolor == 'r') {
                        msgalert("红方（黄）胜，连通棋盘上下两边。", 0);
                    } else {
                        msgalert("黑方（蓝）胜，连通棋盘左右两边。", 0);
                    }
                }

                if (clrcheck) {
                    if (curcolor == 'b') {
                        curcolor = 'r'
                    } else {
                        curcolor = 'b'
                    }
                }
                clicknum++;
            }
        },
        "json")
}

// record the clock.
function recordtheclock(ch, pp) {
    /*
        var xmlhttp;
        if (window.XMLHttpRequest)
        {// code for IE7+, Firefox, Chrome, Opera, Safari
    //  alert( "window.XMLHttpRequest" );
            xmlhttp=new XMLHttpRequest();
        }
        else
        {// code for IE6, IE5
    //  alert( "!window.XMLHttpRequest" );
            xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
        }
    //alert( "xmlhttp.onreadystatechange=function()" );
        xmlhttp.onreadystatechange=function()
        {
            if (xmlhttp.readyState==4 && xmlhttp.status==200)
            {
                        return;
                ;
            }
        }
    */

    var thech = ch.ch;
    if (ch.r >= 14) {
        if (pp.r >= 14)
            thech = '0' + ch.ch;
        else
            thech = '--';
    } else {
        thech += thech;
    }

    var cmdstr = // "roboth.html?10253=password," + "ABCDEFGHIJKLMN"[ch.r] + ','+ ch.c +",c," + ch.ch + ';';
        thech + ':' + getTimeStr();

    RecordChessMoveforclock(cmdstr);
}

function RecordChessMoveforclock(str, clk = true) {
//alert( "in showstr"+str );
    var xmlhttp;
    if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
//  alert( "window.XMLHttpRequest" );
        xmlhttp = new XMLHttpRequest();
    } else {// code for IE6, IE5
//  alert( "!window.XMLHttpRequest" );
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
//alert( "xmlhttp.onreadystatechange=function()" );
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            return;
        }
    }

    var cmdphpstr = "/chess/jq14/status";
    if (clk == true)
        cmdphpstr = "/chess/jq14/clock";

    // var cmdstr = "http://flamechess.cn/js/1/21/";
    var cmdstr = "";
    if (str == "")
        cmdstr += cmdphpstr;
    else
        cmdstr += cmdphpstr + "?i=" + str;

    if (str == "") cmdstr += "?";
    else cmdstr += "&";

    var idstr = getQueryString("code");

    if (idstr != null && idstr != "") {
        cmdstr += "code=" + idstr; // 10253;
        xmlhttp.open("GET", cmdstr, true);
        xmlhttp.send();
    }

}

function getTimeStr() {
    var myDate = new Date();
    // var year = myDate.getFullYear();
    // var month = myDate.getMonth() + 1;
    // var date = myDate.getDate();
    // var dateArr = ["日", "一", '二', '三', '四', '五', '六'];
    // var day = myDate.getDay();
    // var hours = myDate.getHours();
    // var minutes = formatTime(myDate.getMinutes());
    // var seconds = formatTime(myDate.getSeconds());
    // var millsec = formatTime(myDate.getMilliseconds());
    //
    // var ret = " " + year + "年" + month + "月" + date + "日" + " 星期" + dateArr[day] + " " + hours + ":" + minutes + ":" + seconds + '.' + millsec;
    return myDate.getTime();
}

//格式化时间：分秒。
function formatTime(i) {
    if (i < 10) {
        i = "0" + i;
    }
    return i;
}

var ws = null;

//之前的坐标
function initSocket() {
    if ("WebSocket" in window) {
        // 打开一个 web socket
        if (isChess()) {
            ws = new WebSocket("ws://" + location.host + "/jq/global?code=" + getQueryString("code"));
        } else
            ws = new WebSocket("ws://" + location.host + "/jq/global?alias=" + getQueryString("alias"));
        ws.onopen = function () {
            // Web Socket 已连接上，使用 send() 方法发送数据
        };
        ws.onmessage = function (evt) {
            updatePos(evt.data);
        };
        ws.onclose = function () {
            // 关闭 websocket
            alert("连接已关闭...");
            location.reload();
        };
    } else {
        // 浏览器不支持 WebSocket
        alert("您的浏览器不支持 WebSocket!");
    }
}

function RecordChessMove(str) {
    ws.send(str);
}


function updatePos(resp) {
    var str = "";
    // updateChessPos( resp );
    var i;
    for (i = 0; i < resp.length; i++)
        str += resp[i];
    var mstr = matrix2str();
    if (str != mstr) {
        // check the diff.
        var difarray = new Array(g_col * g_row);
        var j = 0;
        for (i = 0; i < str.length; i++) {
            if (str[i] != mstr[i]) {
                difarray[j++] = {i: i, db: str[i], m: mstr[i]};
            }
        }

        //移至后面
        // // check all empty mem.
        // var isallemptymem = true;
        // for (i = 0; i < j; i++) {
        //     if (difarray[i].db != '0' && difarray[i].m == '0')
        //         continue;
        //     else
        //         isallemptymem = false;
        // }
        // if (isallemptymem) {
        //     for (i = 0; i < j; i++) {
        //         var r = Math.floor(difarray[i].i / g_col)
        //         var c = difarray[i].i - r * g_col;
        //         matrix[r][c] = createChessOne(r, c, difarray[i].db);
        //     }
        // }
        // // check all empty mem.
        // var isallemptymem = true;
        // for (i = 0; i < j; i++) {
        //     if (difarray[i].db == '0' && difarray[i].m != '0')
        //         continue;
        //     else
        //         isallemptymem = false;
        // }
        // if (isallemptymem) {
        //     for (i = 0; i < j; i++) {
        //         var r = Math.floor(difarray[i].i / g_col)
        //         var c = difarray[i].i - r * g_col;
        //         deleteChess(r, c, false);
        //         matrix[r][c] = null;
        //     }
        // }

        // match 2 diff pos. and move it.
        for (i = 0; i < j; i++) {
            if (difarray[i].i != -1)
                for (var k = i + 1; k < j; k++) {
                    // search match item.
                    if (difarray[k].i != -1 &&
                        difarray[i].db == difarray[k].m &&
                        difarray[i].m == difarray[k].db) {
                        if (difarray[i].db == '0') {
                            // move m to db.
                            var r = Math.floor(difarray[i].i / g_col)
                            var c = difarray[i].i - r * g_col;
                            var prepos = {r: r, c: c};
                            r = Math.floor(difarray[k].i / g_col)
                            c = difarray[k].i - r * g_col;
                            var pos = {r: r, c: c};
                            moveChessTo(prepos, pos);
                            // mark moved.
                            difarray[i].i = -1;
                            difarray[k].i = -1;
                            // continue.
                            k = j;
                        } else if (difarray[i].m == '0') {
                            // move db to m
                            var r = Math.floor(difarray[k].i / g_col)
                            var c = difarray[k].i - r * g_col;
                            var prepos = {r: r, c: c};
                            r = Math.floor(difarray[i].i / g_col)
                            c = difarray[i].i - r * g_col;
                            var pos = {r: r, c: c};
                            moveChessTo(prepos, pos);
                            // mark moved.
                            difarray[i].i = -1;
                            difarray[k].i = -1;
                            // continue.
                            k = j;
                        } else {
                            var tmpr = 4;
                            var tmpc = 7;
                            // search empty pos.
                            var nowmstr = matrix2str();
                            for (var pempty = 0; pempty < g_col * g_row; pempty++) {
                                if (nowmstr[pempty] == '0') break;
                            }
                            if (pempty < g_col * g_row) {
                                tmpr = Math.floor(pempty / g_col);
                                tmpc = pempty - tmpr * g_col;
                            } else
                            // msgalert.
                                msgalert("// search empty pos. not find. ")

                            // xchange,
                            if (difarray[i].db == 'z') {
                                var r = Math.floor(difarray[k].i / g_col)
                                var c = difarray[k].i - r * g_col;
                                var prepos = {r: r, c: c};
                                var pos = {r: tmpr, c: tmpc}; //{r:4,c:7};
                                moveChessTo(prepos, pos);
                                //
                                r = Math.floor(difarray[i].i / g_col)
                                c = difarray[i].i - r * g_col;
                                prepos = {r: r, c: c};
                                r = Math.floor(difarray[k].i / g_col)
                                c = difarray[k].i - r * g_col;
                                var pos = {r: r, c: c};
                                moveChessTo(prepos, pos);
                                //
                                var prepos = {r: tmpr, c: tmpc}; //{r:4,c:7};
                                r = Math.floor(difarray[i].i / g_col)
                                c = difarray[i].i - r * g_col;
                                var pos = {r: r, c: c};
                                moveChessTo(prepos, pos);
                                // mark moved.
                                difarray[i].i = -1;
                                difarray[k].i = -1;
                                // continue.
                                k = j;
                            } else if (difarray[i].db == 'Z') {
                                var r = Math.floor(difarray[k].i / g_col)
                                var c = difarray[k].i - r * g_col;
                                var prepos = {r: r, c: c};
                                var pos = {r: tmpr, c: tmpc}; //{r:4,c:7};
                                moveChessTo(prepos, pos);
                                //
                                r = Math.floor(difarray[i].i / g_col)
                                c = difarray[i].i - r * g_col;
                                prepos = {r: r, c: c};
                                r = Math.floor(difarray[k].i / g_col)
                                c = difarray[k].i - r * g_col;
                                var pos = {r: r, c: c};
                                moveChessTo(prepos, pos);
                                //
                                var prepos = {r: tmpr, c: tmpc}; //{r:4,c:7};
                                r = Math.floor(difarray[i].i / g_col)
                                c = difarray[i].i - r * g_col;
                                var pos = {r: r, c: c};
                                moveChessTo(prepos, pos);
                                // mark moved.
                                difarray[i].i = -1;
                                difarray[k].i = -1;
                                // continue.
                                k = j;
                            }
                        }
                    }
                }

            // if (difarray[i].i != -1) {
            //     // sum -1 num >= 5 to continue..
            //     var sum_1 = 0;
            //     for (var k = 0; k < j; k++) {
            //         if (difarray[k].i != -1) sum_1++;
            //     }
            //     // if ( sum_1 >= 5 ) continue;
            //     if (sum_1 - 3 * Math.floor(sum_1 / 3) != 0) continue;
            //
            //     var sstart = i;
            //     sstart = 0; // if less 5, search from 0.
            //     // not found the match.
            //     var emptyidx = -1;
            //     // 1st search empty one.
            //     for (var k = sstart; k < j; k++) {
            //         if (difarray[k].i != -1 &&
            //             '0' == difarray[k].m) {
            //             emptyidx = k;
            //             break;
            //         }
            //     }
            //
            //     var emptydb = -1;
            //     if (emptyidx != -1)
            //     // 2nd search empty db.
            //         for (var k = sstart; k < j; k++) {
            //             if (difarray[k].i != -1 &&
            //                 difarray[emptyidx].db == difarray[k].m) {
            //                 emptydb = k;
            //                 break;
            //             }
            //         }
            //
            //     var thirdidx = -1;
            //     if (emptyidx != -1 &&
            //         emptydb != -1)
            //     // search other idx;
            //         for (var k = sstart; k < j; k++) {
            //             if (difarray[k].i != -1 &&
            //                 '0' != difarray[k].m &&
            //                 difarray[emptyidx].db != difarray[k].m) {
            //                 thirdidx = k;
            //                 break;
            //             }
            //         }
            //
            //     if (emptyidx != -1 &&
            //         emptydb != -1 &&
            //         thirdidx != -1) {
            //         if ('0' == difarray[thirdidx].db) {
            //             // move from empty.
            //             var r = Math.floor(difarray[emptyidx].i / g_col)
            //             var c = difarray[emptyidx].i - r * g_col;
            //             var pos = {r: r, c: c};
            //             r = Math.floor(difarray[emptydb].i / g_col)
            //             c = difarray[emptydb].i - r * g_col;
            //             var prepos = {r: r, c: c};
            //             moveChessTo(prepos, pos);
            //             // mark moved.
            //             r = Math.floor(difarray[thirdidx].i / g_col)
            //             c = difarray[thirdidx].i - r * g_col;
            //             prepos = {r: r, c: c};
            //             r = Math.floor(difarray[emptydb].i / g_col)
            //             c = difarray[emptydb].i - r * g_col;
            //             pos = {r: r, c: c};
            //             moveChessTo(prepos, pos);
            //             // mark moved.
            //             difarray[emptyidx].i = -1;
            //             difarray[emptydb].i = -1;
            //             difarray[thirdidx].i = -1;
            //         } else
            //             msgalert("( '0' != difarray[thirdidx].db )", -1);
            //     } else {
            //         msgalert("emptyidx:" + emptyidx +
            //             "; emptydb:" + emptydb +
            //             "; thirdidx:" + thirdidx +
            //             ",not found match blocks.", -1);
            //     }
            // }
        }

        //改708-739行
        {
            var r,c;
            for (i = 0; i < j; i++) {
                if (difarray[i].db != '0' && difarray[i].m == '0') {
                    if (difarray[i].i == -1)
                        continue;
                    r = Math.floor(difarray[i].i / g_col);
                    c = difarray[i].i - r * g_col;
                    matrix[r][c] = createChessOne(r, c, difarray[i].db);
                }
                if (difarray[i].db == '0' && difarray[i].m != '0') {
                    if (difarray[i].i == -1)
                        continue;
                    r = Math.floor(difarray[i].i / g_col)
                    c = difarray[i].i - r * g_col;
                    deleteChess(r, c, false);
                    matrix[r][c] = null;
                }
            }
        }

    }
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]);
    return null;
}

function getFirstPathName() {
    var pathname = location.pathname;
    return pathname.substring(pathname.lastIndexOf('/') + 1);
}

function isChess() {
    var firstPathName = getFirstPathName();
    return firstPathName === 'chess';
}

function prepairChessPort() {
    deleteChess(14, 0, false);
    deleteChess(14, 1, false);
    deleteChess(14, 2, false);
    if (matrix[14][3] == null)
        matrix[14][3] = createChessOne(14, 3, 'z');
    if (matrix[14][4] == null)
        matrix[14][4] = createChessOne(14, 4, 'z');
    if (matrix[14][5] == null)
        matrix[14][5] = createChessOne(14, 5, 'z');
    if (matrix[14][6] == null)
        matrix[14][6] = createChessOne(14, 6, 'z');

    deleteChess(14, 13, false);
    deleteChess(14, 12, false);
    deleteChess(14, 11, false);
    if (matrix[14][10] == null)
        matrix[14][10] = createChessOne(14, 10, 'Z');
    if (matrix[14][9] == null)
        matrix[14][9] = createChessOne(14, 9, 'Z');
    if (matrix[14][8] == null)
        matrix[14][8] = createChessOne(14, 8, 'Z');
    if (matrix[14][7] == null)
        matrix[14][7] = createChessOne(14, 7, 'Z');
}
