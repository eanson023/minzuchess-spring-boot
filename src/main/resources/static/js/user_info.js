$(function () {
    const loading = '<b class="px-3"><span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span></b>';
    $("[data-toggle='popover']").popover();
    //棋盘公开
    $(".switch").click(function () {
        const $this = $(this);
        const code = $this.attr('target');
        const flag = $this.prop('checked');
        const id = $this.prop('id');
        const $switch = $('label[for=' + id + ']');
        if (flag)
            $switch.text('已公开');
        else
            $switch.text('未公开');
        $.post("/user_main/public_chess/" + code, {})
    });
    //打开模态框
    $('.change-team-btn').click(function () {
        var data = $(this).attr('data');
        $("#teamId").val(data);
        $.get('/team/find/' + data, {}, function (data) {
            var team = data.data;
            $("#team-name").val(team.teamName);
            $("#introduction").val(team.introduction);
        }, 'json')
    });
    //提交
    $("#change-team-submit-btn").click(function () {
        const upbtn = $(this);
        const $fileElement = $("#avatar")[0].files[0];
        if ($fileElement !== undefined) {
            if ($fileElement.size > 2048 * 1024) {
                $('#file-alert2').attr('hidden', false);
                return;
            } else
                $('#file-alert2').attr('hidden', true);

        }
        if (!submitValidate(changeTeamValidator.validate()))
            return;
        upbtn.prop('disabled', true);
        const val = upbtn.html();
        upbtn.html(loading);
        // formData.append("avatar", $fileElement);
        // language=JQuery-CSS
        var formData = new FormData($("#change-team-form")[0]);
        $.ajax({
            type: 'post',
            contentType: false,
            processData: false,
            url: '/team/update',
            data: formData,
            dataType: 'json',
            success: function (data) {
                upbtn.prop('disabled', false);
                upbtn.html(val);
                if (data.success) {
                    location.reload();
                }
            }
        });
    });
    $(".del-team").click(function () {
        var $this = $(this);
        $("#del-input").val($this.attr('data'))
    });
    $(".quit-team").click(function () {
        var $this = $(this);
        $("#quit-input").val($this.attr('data'))
    });

});
var changeTeamValidator = new Validator('change-team-form', [
    {
        name: "teamName",
        rules: "required"
    }, {
        name: "avatar",
        rules: "required"
    }, {
        name: "introduction",
        rules: "required|min_length(4)|max_length(200)"
    }
]);