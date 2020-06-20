$(function () {
    toWhere();
    $(".one-radio").click(function () {
        toWhere();
    });
});

function toWhere() {
    var radioVal = $(".one-radio:checked").val();
    radioVal = parseInt(radioVal);
    const box = $('#login-box2');
    switch (radioVal) {
        case 1:
            box.load('/jsp/search_team');
            break;
        case 2:
            box.load('/jsp/create_team');
    }
}
