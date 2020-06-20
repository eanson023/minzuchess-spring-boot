$(function () {
    const loading = '<b class="px-3"><span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span></b>';
    $('.form-upload').submit(function () {
        const upbtn = $('#upload');
        const formData = new FormData();
        const $fileElement = $("#file")[0].files[0];
        if ($fileElement == undefined) {
            $('#file-alert').attr('hidden', false);
            return false;
        }
        if ($fileElement.size > 2048 * 1024) {
            $('#file-alert').attr('hidden', false);
            return false;
        } else
            $('#file-alert').attr('hidden', true);
        upbtn.prop('disabled', true);
        const val = upbtn.html();
        upbtn.html(loading);
        formData.append("avatar", $fileElement);
        $.ajax({
            type: 'post',
            contentType: false,
            processData: false,
            url: '/user_info/upload',
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
        return false;
    });
    $('.file').change(function () {
        const $this = $(this);
        let val = $this.val();
        if (val)
            $(".custom-file-label").text(val);
        else
            $(".custom-file-label").text('选择文件');
    });
    $('#update-btn').click(function () {
        $("#change-alert").prop('hidden', true);
        if (!submitValidate(updateValidator.validate()))
            return;
        const upbtn = $(this);
        var form = $("#form-update");
        upbtn.prop('disabled', true);
        const val = upbtn.html();
        upbtn.html(loading);
        $.post('/user_info/update_pwd', form.serialize(), function (data) {
            upbtn.html(val);
            upbtn.prop('disabled', false);
            if (!data.success) {
                $("#change-msg").html(data.msg);
                $("#change-alert").prop('hidden', false);
            } else {
                $("#model1").modal('hide');
                $("#change-alert").prop('hidden', true);
            }
        }, 'json');
        return false;
    });
    $('#password').keyup(function () {
        validateOne(this, updateValidator);
    });
    $('#repassword').keyup(function () {
        validateOne(this, updateValidator);
    });
    $("#original").keyup(function () {
        validateOne(this, updateValidator);
    });
    $(".agree-btn").click(function () {
        var thisBtn = $(this);
        var form = thisBtn.parent().prev();
        $.post('/team/agree', form.serialize());
        form.parent().empty();
        var $msgCount = $("#msgCount");
        var val = $msgCount.html();
        $msgCount.html(parseInt(val) - 1);
    });
    $(".refuse-btn").click(function () {
        var thisBtn = $(this);
        var form = thisBtn.parent().prev();
        $.post('/team/refuse', form.serialize());
        form.parent().empty();
        var $msgCount = $(".msgCount");
        var val = $msgCount.val();
        $msgCount.val(val - 1);
    })
});

var updateValidator = new Validator('form-update', [
    {
        name: "original",
        regexp_num: /^[a-zA-Z0-9]{6,20}$/,
        rules: 'required|regexp_num'
    }, {
        name: "password",
        regexp_num: /^[a-zA-Z0-9]{6,20}$/,
        rules: 'required|regexp_num'
    }, {
        name: "repassword",
        rules: "same(password)"
    }
]);
