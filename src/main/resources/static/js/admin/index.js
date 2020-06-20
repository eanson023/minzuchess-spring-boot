var validator = new Validator('user-form', [
    {
        name: "realName",
        rules: 'required|is_chinese'
    }, {
        name: "password",
        regexp_num: /^[a-zA-Z0-9]{6,20}$/,
        rules: 'required|regexp_num'
    }, {
        name: "telephone",
        rules: 'required|is_phone'
    }
], function (obj, evt) {
    validParse(obj);
});
var validator2 = new Validator('code-form', [
    {
        name: "code",
        rules: 'required|max_length(4)|min_length(4)'
    }, {
        name: "category",
        rules: 'required'
    }
], function (obj, evt) {
    validParse(obj);
});
function validParse(obj) {
    if (obj.errors.length > 0) {
        // 判断是否错误
        var i;
        for (i = 0; i < obj.errors.length; i++) {
            var $2 = $('#' + obj.errors[i].id + '');
            $2.addClass('is-invalid');
            $2.removeClass('is-valid');
        }
    }
    var fields = obj.fields;
    for (let field in fields) {
        var id = fields[field].id;
        var flag = true;
        for (i = 0; i < obj.errors.length; i++) {
            if (obj.errors[i].id == id) {
                flag = false;
                break;
            }
        }
        if (flag) {
            var $1 = $('#' + id + '');
            $1.addClass('is-valid');
            $1.removeClass('is-invalid');
        }
    }
}