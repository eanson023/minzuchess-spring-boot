$(function () {
    $("input[type='password'][data-eye]").each(function (i) {
        var $this = $(this),
            id = 'eye-password-' + i,
            el = $('#' + id);

        $this.wrap($("<div/>", {
            style: 'position:relative',
            id: id
        }));

        $this.css({
            paddingRight: 60
        });
        $this.after($("<div/>", {
            html: 'Show',
            class: 'btn btn-primary btn-sm',
            id: 'passeye-toggle-' + i,
        }).css({
            position: 'absolute',
            right: 10,
            top: ($this.outerHeight() / 2) - 12,
            padding: '2px 7px',
            fontSize: 12,
            cursor: 'pointer',
        }));

        $this.after($("<input/>", {
            type: 'hidden',
            id: 'passeye-' + i
        }));

        var invalid_feedback = $this.parent().parent().find('.invalid-feedback');

        if (invalid_feedback.length) {
            $this.after(invalid_feedback.clone());
        }

        $this.on("keyup paste", function () {
            $("#passeye-" + i).val($(this).val());
        });
        $("#passeye-toggle-" + i).on("click", function () {
            if ($this.hasClass("show")) {
                $this.attr('type', 'password');
                $this.removeClass("show");
                $(this).removeClass("btn-outline-primary");
            } else {
                $this.attr('type', 'text');
                $this.val($("#passeye-" + i).val());
                $this.addClass("show");
                $(this).addClass("btn-outline-primary");
            }
        });
    });
    //加载学校
    $("#provinceId").change(function () {
        loadSchool();
    });
});

function loadProvince() {
    $.get('/get_province', {}, function (data) {
        $.each(data.data, function (i, e) {
            var content = '<option  value="' + e.proId + '">' + e.name + '</option>';
            $('#provinceId').append(content);
        });
    }, 'json');
}

function loadSchool() {
    const pro_id = $("#provinceId option:selected").val();
    $.get('/get_school', {pro_id: pro_id}, function (data) {
        var school = $('#school');
        school.empty();
        school.append('<option selected value="-1">社会团体</option>');
        $.each(data.data, function (i, e) {
            let content = '<option value="' + e.schId + '">' + e.name + '</option>';
            school.append(content);
        })
    }, 'json');
}

function submitValidate(obj) {
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
    return !obj.errors.length > 0;
}

function validateOne(e, validator) {
    var val = validator.validate();
    var flag = true;
    for (var i = 0; i < val.errors.length; i++) {
        if (e.name == val.errors[i].name) {
            $(e).addClass('is-invalid');
            $(e).removeClass('is-valid');
            flag = false;
        }
    }
    if (flag) {
        $(e).addClass('is-valid');
        $(e).removeClass('is-invalid');
    }
    return flag;
}

//倒计时
function resetCode() {
    $('#get-code').hide();
    $('#code-second').html('60');
    $('#reset-code').show();
    var second = 60;
    var timer = null;
    timer = setInterval(function () {
        second -= 1;
        if (second > 0) {
            $('#code-second').html(second);
        } else {
            clearInterval(timer);
            $('#get-code').show();
            $('#reset-code').hide();
        }
    }, 1000);
}