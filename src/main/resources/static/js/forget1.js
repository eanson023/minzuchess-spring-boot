var forget1Form = new Validator('forget1-form', [
    {
        name: 'telephone',
        rules: 'required|is_phone'
    }, {
        name: 'code',
        rules: 'required'
    }
]);
$(function () {
    $("#tel").keyup(function () {
        validateOne(this, forget1Form);
    });
    $("#check-code").keyup(function () {
        validateOne(this, forget1Form);
    });
    $("#submit").click(function () {
        if (!submitValidate(forget1Form.validate())) {
            return;
        }
        var $this = $(this);
        var loading = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>';
        $this.prop('disabled', true);
        const original = $this.html();
        $this.html(loading);
        var form = $("#forget1-form");
        $.post('/user/forget1', form.serialize(), function (data) {
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