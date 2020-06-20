/*
    global vara.
 */

var divEcolor = "#037";
var globals;
var mouseover;
var clrcheck = true;
var isGo = false;
var goRed = "", goBlack = "";
var goCurChess = null,
    goRedChess, goBlackChess;
var gIsRectBoard = false;
var gHasSize = false;
var gHasPC = false;

mouseoverrectinit( boardrects );
function mouseoverrectinit( rc )
{
    mouseover = rc; // fcscript_init(rc);
}

function mouseovereventscriput( x, y )
{
    /*
    for ( var i=0; i<mouseover.length; i++ ) {

        if (mouseover[i].length == 5 &&
            mouseover[i][0] == '-') {
            if (x >= 30 + 60 * mouseover[i][1] && x <= 30 + 60 * mouseover[i][2] &&
                y >= 30 + 60 * mouseover[i][3] && y <= 30 + 60 * mouseover[i][4]) {
                return; // not show cursor.
            }
        }
    }

    for ( var i=0; i<mouseover.length; i++ ) {

        if (mouseover[i].length == 5 &&
            mouseover[i][0] == '+') {
            if (x >= 30 + 60 * mouseover[i][1] && x <= 30 + 60 * mouseover[i][2] &&
                y >= 30 + 60 * mouseover[i][3] && y <= 30 + 60 * mouseover[i][4])
            {
    */          var pos = getChessPos(x, y)
    if ( mouseover.charAt( pos.r * g_col + pos.c ) != '0' )
        return;
                if (clicknum % 2 == 0 && !isGo) {
                    moveBox(box2, pos)
                }
                else {
                    moveBox(box, pos)
                }
    /*        return; // show it.
            }
        }
    }*/
}

initglobal();
function initglobal() {

    globals = fcscript_init(globalscript);

    if (globals[0].length != 1)
        msgalert("'#037;' + // var divEcolor." + 'length != 1', -1);
    else {
        divEcolor = globals[0][0];
        alertclear();
    }

    if (globals[1].length != 1)
        msgalert("transcube.png, show length != 1", -1);
    else if (globals[1][0] == 'false') {
        // hidden.
        var div = document.getElementById("divx");
        div.style.display = 'none';
    } else if (globals[1][0] == 'true') {
        // show.
        var div = document.getElementById("divx");
        div.style.display = 'block';
    }

    if (globals[2].length != 2)
        msgalert("yu100.png, show length != 2", -1);
    else if (globals[2][0] == 'false') {
        // hidden.
        var div = document.getElementById("divd");
        div.style.display = 'none';
    } else if (globals[2][0] == 'true') {
        // show.
        var div = document.getElementById("divd");
        div.style.display = 'block';
        if ( globals[2][1] != "" ){
            // div.innerHTML = "<a href=" + '"' + globals[2][1] + '">' + "<img src=" + '' + "" + '' + "\"./xiaoyuan/yu100.png\"/></a>";
            div.innerHTML = "<img src=" + '' + "" + '' + "\"/img/xiaoyuan/yu100.png\"/>";
        }
        else
            div.innerHTML = "<img src=" + '' + "" + '' + "\"/img/xiaoyuan/yu100.png\"/>";
    }

    if (globals[3].length != 2)
        msgalert("<title>火焰棋#40.谁先走出广场2vs2</title>, show length != 2", -1);
    else {
        document.title = "火焰棋#" + globals[3][0] + "." + globals[3][1];
    }

    if (globals[4].length != 2)
        msgalert("小白③号, show length != 2", -1);
    else {
        var div = document.getElementById("divc");
        div.innerText = globals[4][0]; // "小白③号"

        // adding event click to divec.
        div.addEventListener('click', function () {
            alertclear();
            msgalert( globals[4][1], -1 );
        }, false)

    }

    if (globals[5].length > 0) {
        var div = document.getElementById("diva");
        for (var i = 0; i < globals[5].length; i++) {
            div.innerHTML += "<img src = " + '"' + "/img/xiaoyuan/" + globals[5][i] + '.png"'+ " / ><br>"
        }
        div.onclick = function(e) {
            var idx = Math.floor( e.y / 100 );
            if ( idx * 100 - 8 < e.y && e.y < (idx+1) * 100 - 8 &&
                 8 < e.x && e.x < 100-8 ) {
                // in item middle.
                fcscript_logo_onclick( idx );
            }
        };
    }

    if (globals[6].length != 1)
        msgalert("clrcheck, show length != 1", -1);
    else if (globals[6][0] == 'false')
        clrcheck = false;
    else if (globals[6][0] == 'true')
        clrcheck = true;

    if (globals[7].length != 3)
        msgalert("'go,z,Z;'   // no move, r=z, b=Z,", -1);
    else if (globals[7][0] == '')
        isGo = false;
    else if (globals[7][0] == 'go') {
        isGo = true;
        goRed = globals[7][1];
        goBlack = globals[7][2];
    }

    if (globals[8].length != 1)
        msgalert("'rect;'  // hex or rect", -1);
    else if (globals[8][0] == 'rect')
        gIsRectBoard = true;
    else if (globals[8][0] == 'hex') {
        gIsRectBoard = false;
    }

    if (globals[9].length != 1)
        msgalert("'size;'  // hex or rect", -1);
    else if (globals[9][0] == 'size')
        gHasSize = true;

    if (globals[10].length != 1)
        msgalert("'pc;'  // hex or rect", -1);
    else if (globals[10][0] == 'size')
        gHasPC = true;
}

function getChessTypeScript(ch) {
//    for ( var i=0; i<fcs.length; i++ )
//        if ( ch == fcs[i].r || ch == fcs[i].b) return fcs[i].name;
    var fsc = fcscript_init( fcscript );
    i = 0;
    while ( i < fsc.length ) {
        if ( ch == fsc[i][0] || ch == fsc[i][1] ) return { n:fsc[i][2], c:fsc[i][4] };
        i+=parseInt( fsc[i][3] ) + 1;
    }
    return { n:null, c:null }
}

function fcscript_init( script )
{
    var last = 0;
    for ( var i=0; i<script.length; i++ )
        if ( script.charAt(i) == ';' ) last ++;
    var sarray = new Array(last);
    last = 0;
    var parray = 0;
    for ( var i=0; i<script.length; i++ )
        if ( script.charAt(i) == ';' ) {
            var cmds = script.substring( last, i )+',';
            var cmdlast = 0;
            for ( var j=0; j<cmds.length; j++ )
                if ( cmds.charAt(j) == ',' ) cmdlast ++;
            // create the ln array.
            sarray[parray] = new Array(cmdlast);
            cmdlast = 0;
            var pcmd = 0;
            for ( var j=0; j<cmds.length; j++ )
                if ( cmds.charAt(j) == ',' ) {
                    sarray[parray][pcmd] = cmds.substring( cmdlast, j );
                    cmdlast = j + 1;
                    pcmd ++;
                }
            last = i+1;
            parray ++;
        }
    return sarray;
}

function alertclear()
{
    var dive = document.getElementById("dive");
    dive.innerText = "";
    dive.style.color = divEcolor;
}

function getalert()
{
    var dive = document.getElementById("dive");
    var ret = dive.innerText;
    ret = ret.replace(/[\r\n]/g," ");
    return ret;
}

function msgalert( msg, ret )
{
    var dive = document.getElementById("dive");
    var oldmsg = dive.innerHTML;
    if ( oldmsg != "" ) oldmsg += "<br>";
    dive.innerHTML = oldmsg + msg;
//    alert( msg );
//    getalert();
    return ret;
}

function condition3_getvar( local, v )
{
    switch (v) {
        case 'ar': return local.ar;
        case 'ac': return local.ac;
        case 'dc': return local.dc;
        case 'dr': return local.dr;
        case 's.color': return local.s.color;
        case 't.color': return local.t.color;
        case 't': return local.t;
        case 't.type': return local.t.type;
        case 'pos.r': return local.pos.r;
        case 'pos.c': return local.pos.c;
        case 'jump' : return jumpcheck( local.prepos, local.pos );
        case 'jumpX' : return jumpXcheck( local.prepos, local.pos );
    }
    if ( condition3_isnumber( v ) ) return parseInt( v );
    if ( v == "undefined") return undefined;
    return v; // msgalert( "invalid varable "+v, -1 );
           // return str directly.
}

function condition3_isnumber( v )
{
    var p = 0;
    if (v.charAt(0) == '-' ) p++;
    while ( p < v.length ) {
        if (v.charAt(p) < '0' || v.charAt(p) > '9' ) return false;
        p++;
    }
    return true;
}

function condition3_fcs( local, sarray3 )
{
    if ( sarray3.length != 3 ) return false;
    var A = condition3_getvar( local, sarray3[1] );
    var B = condition3_getvar( local, sarray3[2] );
    switch ( sarray3[0] ) {
        case '>':
            return A > B;
            break;
        case '>=':
            return A >= B;
            break;
        case '==':
            return A == B;
            break;
        case '!=':
            return A != B;
            break;
        case '<=':
            return A <= B;
            break;
        case '<':
            return A < B;
            break;
        default:
            return msgalert( "非正常的表达式" + sarray3[0] + "," + sarray3[1] + "," + sarray3[2], false );
    }
}

function fcscript_trans( prepos, pos )
{
    var s = ( isGo && prepos == null )
        ? createGoS( ( clicknum%2 == 0 ) ? goRed : goBlack )
        : matrix[prepos.r][prepos.c]
    var t  = matrix[pos.r][pos.c]
    var dr = pos.r - s.r;   // prepos.r
    var dc = pos.c - s.c;   // prepos.c
    var ar = (dr > 0) ? dr : -dr
    var ac = (dc > 0) ? dc : -dc

    var local = {
        prepos:prepos,
        pos:pos,
        s:s,
        t:t,
        dr:dr,
        dc:dc,
        ar:ar,
        ac:ac
    };

    // todo scrtpt.
    var sarray = fcscript_init( fcscript );
    for ( var j=0; j<sarray.length; j++ )
    {
        if ( sarray[j][2] != s.type )
        {
            j += parseInt( sarray[j][3] );

        } else {
     //       j ++;

    for ( var i=j+1; i<parseInt( sarray[j][3] )+j+1;i++ ) {
        // explain..each condition.
        if ( sarray[i].length != 4 )
        {
            // alert the script error, to break.
            var msg = "";
            for ( var j=0; j<sarray[i].length; j++ )
                msg += sarray[i][j] + ',';
            return msgalert( "脚本格式不对，请查看" + msg, -10 );
        } else {
            if ( sarray[i][0] == "||" ) {
                var b = false;
                for ( var k=0; k<parseInt(sarray[i][1]); k++ )
                    if ( condition3_fcs( local, sarray[i+k+1] ) ) b = true;
                if ( b )
                    return msgalert( sarray[i][3], sarray[i][2] );
            } else
            if ( sarray[i][0] == '&&' ) {
                var b = true;
                for ( var k=0; k<parseInt(sarray[i][1]); k++ )
                    if ( !condition3_fcs( local, sarray[i+k+1] ) ) { b = false; break; }
                if ( b )
                    return msgalert( sarray[i][3], sarray[i][2] );
            } else
                return msgalert( "非正常脚本命令" + sarray[i][0], -10 );
        }
        i += parseInt(sarray[i][1]);

    }
            j += parseInt( sarray[j][3] );
        }

    }

    // return {r:r, c:c}
    // pos.r|c == chess.r|c.
    //----------------------------------------------
    //一次只能一步，不能后退
//    if (ar > 1 || ac > 1) return -5
//    if (s.color == 'b' && dr == -1) return -5;  // 黑棋不能向上移动。
//    if (s.color == 'r' && dc == -1) return -5;  // 红棋不能向左移动。
    // 不能斜45度移动。
//    if ( ar == 1 && ac == 1 ) return -5;
    // 不能移动到对方棋子，不能吃子。
//    if ( t != undefined && t.type == "zu") return -3;
    // 不能移动到对方的目的地。
//    if (s.color == 'b' && pos.c == 6 ) return -5;
//    if (s.color == 'r' && pos.r == 3 ) return -5;
    return 0
}

/*
function fcsCheckMove(prepos,pos)
{

//    for ( var i=0; i<fcs.length; i++ )
//    if (type == fcs[i].name ) {   // "zu"
        return fcscript_trans( prepos, pos );
        // return zumove( prepos, pos );
//    }
}
*/

function jumpcheck(prepos, pos){
    return countLineChess( prepos, pos );
}

function jumpXcheck(prepos, pos){
    return countXLineChess( prepos, pos );
}

function checkFinish( clr ) {
    jcheckFinish( clr );
/*
    var count = 0;
    var msg = "";
    if ( clr == 'b' ) {
        var b1 = matrix[4][0];
        var b2 = matrix[4][1];
        var b3 = matrix[4][2];
        var b4 = matrix[4][3];
        msg = "黑";
    } else
    if ( clr == 'r') {
        var b1 = matrix[0][4];
        var b2 = matrix[1][4];
        var b3 = matrix[2][4];
        var b4 = matrix[2][4];
        msg = "红";
    }
    if ( b1 != null ) count ++;
    if ( b2 != null ) count ++;
    if ( b3 != null ) count ++;
    if ( b4 != null ) count ++;
    if ( count == 3 ) {
        msg += "方胜利，3枚棋子走出光场。"
        msgalert( msg, 0 );
    }
 */
}
