$(document).ready(function () {
    $('#dataTable').DataTable({
        "searching": true,
        "pagesType": "full_numbers",
        "language": {
            //国际化
            "url": "/js/Chinese.json"
        },
        "serverSide": true,//开启服务器模式，使用服务器端处理配置datatable
        "processing": true,//开启读取服务器数据时显示正在加载中……特别是大数据量的时候，开启此功能比较好
        ajax: function (data, callback, settings) {
            $.ajax({
                type: 'post',
                dataType: 'json',
                contentType: 'application/json',
                url: '/admin/cheep/cheep_info.json',
                data: JSON.stringify(data),
                success: function (data) {
                    var returnData = {};
                    returnData.recordsTotal = data.recordsTotal;//总记录数
                    returnData.recordsFiltered = data.recordsFiltered;//后台不实现过滤功能，每次查询均视作全部结果
                    returnData.data = data.data;
                    callback(returnData);
                }
            });
        },
        //默认排序
        "order": [[2, 'desc']],
        "columns": [
            {"data": "trick", "orderable": false, "searchable": false},
            {
                "searchable": false, "orderable": false,
                "sClass": "text-center",
                "data": "color", "render": function (data, type, row, meta) {
                    var content = '';
                    if (data == 'z') {
                        content = '<img src="/img/r_zu.png" width="20" height="20">';
                    } else if (data == 'Z') {
                        content = '<img src="/img/b_zu.png" width="20" height="20">';
                    }
                    return content;
                }
            },
            {"data": "createTime"},
            {"data": "typeStr", "searchable": false},
            {"data": "statusStr", "searchable": false},
            {"data": "message", "searchable": false, "orderable": false},
            {"data": "cbCode", "orderable": false},
            {"data": "value", "orderable": false},
            {"data": "realName"}

        ]
    });
    var beginTime = $('#begin-time').clockpicker({
        placement: 'bottom',
        align: 'left',
        autoclose: true,
    });
    var endTime = $('#end-time').clockpicker({
        placement: 'bottom',
        align: 'left',
        autoclose: true,
    });
    $('#begin-date').datepicker({
        language: "zh-CN",
        autoclose: true,
        todayHighlight: true
    }).on('hide', function (e) {
        e.stopPropagation();
        beginTime.clockpicker('show')
    });
    $('#finish-date').datepicker({
        language: "zh-CN",
        autoclose: true,
        todayHighlight: true
    }).on('hide', function (e) {
        e.stopPropagation();
        endTime.clockpicker('show')
    });
    //加上code
    $("#output").click(function () {
        var $this = $(this);
        $this.popover('hide');
        var val = cheepValidator.validate();
        if (!submitValidate(val)) {
            $this.popover('toggle');
            return;
        }
        var uri = '/cheep/output?' + $("#cheep-form").serialize();
        window.open(uri, '_blank');
    });
    $('#set-public').click(function () {
        var $this = $(this);
        var val = cheepValidator.validate();
        if (!submitValidate(val)) {
            $this.popover('toggle');
            return;
        }
        $("#set-public-model").modal('show');
    });
    $("#set-public-submit-btn").click(function () {
        var $cn = $("#cheep-name");
        var val = $cn.val().trim();
        var regex = /^.{2,20}/;
        var flag = regex.test(val);
        if (!flag) {
            $cn.addClass('is-invalid');
            $cn.removeClass('is-valid');
            return;
        } else {
            $cn.addClass('is-valid');
            $cn.removeClass('is-invalid');
        }
        var $this = $(this);
        var loading = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>';
        $this.prop('disabled', true);
        const original = $this.html();
        $this.html(loading);
        var params = 'name=' + val + '&' + $("#cheep-form").serialize();
        $.post('/cheep/set_public', params, function (data) {
            $this.html(original);
            $this.prop('disabled', false);
            var html;
            if (!data.success) {
                html = '<div class="alert alert-dismissible alert-danger">\n' +
                    '<button type="button" class="close" data-dismiss="alert">&times;</button>\n' +
                    '<strong>' + data.msg + '</strong>\n' +
                    '</div>';
                $("#modal-body").prepend(html);
            } else {
                html = '<div class="alert alert-dismissible alert-success">\n' +
                    '<button type="button" class="close" data-dismiss="alert">&times;</button>\n' +
                    '<strong>设置成功</strong>\n' +
                    '</div>';
                $("#modal-body").prepend(html);
            }
        }, 'json');
    })
});
var cheepValidator = new Validator('cheep-form', [{
    name: "code",
    rules: "required"
}, {
    name: "start-date",
    rules: "required"
}, {
    name: "start-time",
    rules: "required"
}, {
    name: "end-date",
    rules: "required"
}, {
    name: "end-time",
    rules: "required"
}]);

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