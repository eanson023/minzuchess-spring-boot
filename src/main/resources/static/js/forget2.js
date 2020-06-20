var forget2Form = new Validator('forget2-form', [
    {
        name: "password",
        regexp_num: /^[a-zA-Z0-9]{6,20}$/,
        rules: 'required|regexp_num'
    }, {
        name: 'repassword',
        rules: 'required|same(password)'
    }
]);
$(function () {
    $("#password").keyup(function () {
        validateOne(this, forget2Form);
    });
    $("#repassword").keyup(function () {
        validateOne(this, forget2Form);
    });
    $("#submit").click(function () {
        if (!submitValidate(forget2Form.validate())) {
            return;
        }
        var $this = $(this);
        var loading = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>';
        $this.prop('disabled', true);
        const original = $this.html();
        $this.html(loading);
        var form = $("#forget2-form");
        $.post('/user/forget2', form.serialize(), function (data) {
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
        }, 'json')
    })
});