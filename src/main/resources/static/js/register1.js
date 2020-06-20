$(function () {
    $("#submit").click(function () {
        var val = validator.validate();
        if (!submitValidate(val))
            return;
        var $this = $('#submit');
        var loading = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>';
        $this.prop('disabled', true);
        const original = $this.html();
        $this.html(loading);
        const form = $("#register1-form");
        $.post('/user/register1', form.serialize(), function (data) {
            let html;
            if (!data.success) {
                html = '<div class="alert alert-dismissible alert-danger">\n' +
                    '<button type="button" class="close" data-dismiss="alert">&times;</button>\n' +
                    '<strong>' + data.msg + '</strong>\n' +
                    '</div>';
                form.before(html);
                $this.html(original);
                $this.prop('disabled', false);
            } else {
                html = '<div class="alert alert-dismissible alert-success">\n' +
                    '  <button type="button" class="close" data-dismiss="alert">&times;</button>\n' +
                    '  <strong>网页跳转中....</strong>\n' +
                    '</div>';
                form.before(html);
                location.href = data.data;
            }
        }, 'json');
        return false;
    });
    $("#check-code").keyup(function () {
        validateOne(this, validator);
    });
    $("#realName").keyup(function () {
        validateOne(this, validator);
    });
    $("#telephone").keyup(function () {
        validateOne(this, validator);
    });
    $("#password").keyup(function () {
        validateOne(this, validator);
    });
    $("#repassword").keyup(function () {
        validateOne(this, validator);
    });
    $("#get-code").click(function () {
        getCode(validator, true);
    })
});

var validator = new Validator('register1-form', [
    {
        name: "realName",
        rules: 'required|is_chinese'
    }, {
        name: "password",
        regexp_num: /^[a-zA-Z0-9]{6,20}$/,
        rules: 'required|regexp_num'
    }, {
        name: "repassword",
        rules: "same(password)"
    }, {
        name: "telephone",
        rules: 'required|is_phone'
    }, {
        name: "code",
        rules: 'required'
    }, {
        name: "agree",
        rules: "required"
    }

]);

// 获取验证码
function getCode(validator, is_new) {
    var tel = $('#telephone');
    var telJs = tel[0];
    //验证手机号码
    if (validateOne(telJs, validator)) {
        resetCode(); //倒计时
        let phone = tel.val();
        $.post('/get_code', {telephone: phone, now_time: new Date().getTime(), is_new: is_new}, function (data) {
            var form = $("#register1-form");
            let html;
            if (!data.success) {
                html = '<div class="alert alert-dismissible alert-danger">\n' +
                    '<button type="button" class="close" data-dismiss="alert">&times;</button>\n' +
                    '<strong>' + data.msg + '</strong>\n' +
                    '</div>';
                form.before(html);
            } else {
                html = '<div class="alert alert-dismissible alert-success">\n' +
                    '  <button type="button" class="close" data-dismiss="alert">&times;</button>\n' +
                    '  <strong>' + data.msg + '</strong>\n' +
                    '</div>';
                form.before(html);
            }
        });
    } else {
        tel.focus();
    }
}