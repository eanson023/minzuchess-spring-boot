$(function () {
    loadProvince();
    $("#teamNameC").keyup(function () {
        validateOne(this, addTeamValidator);
    });
    $("#introductionC").keyup(function () {
        validateOne(this, addTeamValidator);
    });
    $("#submitC").click(function () {
        if (!submitValidate(addTeamValidator.validate())) {
            return;
        }
        var load = '<div class="spinner-border" role="status">\n' +
            '                <span class="sr-only">Loading...</span>\n' +
            '            </div>';
        var msgBox = $("#add-msg-box");
        var form = $("#addForm");
        var $this = $(this);
        $this.prop('disabled', true);
        const original = $this.html();
        $this.html(load);
        $.post('/team/add', form.serialize(), function (data) {
            var html;
            if (data.success) {
                html = '<div class="alert alert-dismissible alert-success">\n' +
                    '  <button type="button" class="close" data-dismiss="alert">&times;</button>\n' +
                    '  <strong>网页跳转中....</strong>\n' +
                    '</div>';
                msgBox.html(html);
                location.reload();
            } else {
                html = '<div class="alert alert-dismissible alert-danger">\n' +
                    '<button type="button" class="close" data-dismiss="alert">&times;</button>\n' +
                    '<strong>' + data.msg + '</strong>\n' +
                    '</div>';
                msgBox.html(html);
                if (data.data) {
                    location.href = data.data;
                } else {
                    $this.html(original);
                    $this.prop('disabled', false);
                }
            }
        }, 'json');
    });
    var addTeamValidator = new Validator('addForm', [
        {
            name: "teamName",
            rules: 'required|max_length(20)'
        }, {
            name: "provinceId",
            rules: 'required'
        }, {
            name: "introduction",
            rules: "required|min_length(4)|max_length(200)"
        }]);
});