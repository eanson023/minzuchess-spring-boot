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
var table;
var columns = [
    {"data": "trick", "orderable": false},
    {
        "orderable": false,
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
    {"data": "typeStr"},
    {"data": "createTime"},
    {"data": "statusStr"},
    {"data": "message", "orderable": false}
];
$(function () {
    reLoad();
    $('#begin-date').datepicker({
        language: "zh-CN",
        autoclose: true,
        todayHighlight: true
    }).on('hide', function (e) {
        e.stopPropagation();
        beginTime.clockpicker('show')
    });
    $('#end-date').datepicker({
        language: "zh-CN",
        autoclose: true,
        todayHighlight: true
    }).on('hide', function (e) {
        e.stopPropagation();
        endTime.clockpicker('show')
    });
    $('#search').click(function () {
        var $this = $(this);
        $this.popover('hide');
        if (!isValid()) {
            $this.popover('toggle');
            return;
        }
        table.destroy();
        var fd = new FormData($("#time-form")[0]);
        table = $('#dataTable').DataTable({
            "ordering": false,
            "searching": false,
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
                    url: '/cheep/search/' + fd.get("start-date") + '/' + fd.get("start-time") + '/' + fd.get("end-date") + '/' + fd.get('end-time') + '/' + getSelectedCode(),
                    data: JSON.stringify(data),
                    success: function (data) {
                        data = data.data;
                        var returnData = {};
                        returnData.recordsTotal = data.recordsTotal;//总记录数
                        returnData.recordsFiltered = data.recordsFiltered;//后台不实现过滤功能，每次查询均视作全部结果
                        returnData.data = data.data;
                        callback(returnData);
                    }
                });
            },
            "columns": columns
        });
    });
    $("#output").click(function () {
        var $this = $(this);
        $this.popover('hide');
        if (!isValid()) {
            $this.popover('toggle');
            return;
        }
        var uri ='/cheep/output?' + $("#time-form").serialize();
        window.open(uri, '_blank');
    });
    $('#set-public').click(function () {
        var $this = $(this);
        if (!isValid()) {
            $this.popover('toggle');
            return;
        }
        $("#set-public-model").modal('show');
    });
    $("#code-select").change(function () {
        var $this = $(this);
        var code = $this.children('option:selected').val();
        var uri = '?code=' + code;
        chageUri(uri);
        table.destroy();
        reLoad();
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
        var params = 'name=' + val + '&' + $("#time-form").serialize();
        $.post('/cheep/set_public', params, function (data) {
            $this.html(original);
            $this.prop('disabled', false);
            if (!data.success) {
                var html = '<div class="alert alert-dismissible alert-danger">\n' +
                    '<button type="button" class="close" data-dismiss="alert">&times;</button>\n' +
                    '<strong>' + data.msg + '</strong>\n' +
                    '</div>';
                $("#modal-body").prepend(html);
            } else {
                location.reload();
            }
        }, 'json');
    })
});

function reLoad() {
    table = $('#dataTable').DataTable({
        "autoFill": true,
        "searching": false,
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
                url: '/cheep/pagination/cheep_info.json/' + getSelectedCode(),
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
        "order": [[3, 'desc']],
        "columns": columns
    });
}

function getSelectedCode() {
    return $("#code-select").children("option:selected").val();

}

function isValid() {
    var flag = true;
    $(".input-daterange>input").each(function () {
        if ($(this).val().trim().length === 0)
            flag = false;
    });
    return flag;
}

function chageUri(uri) {
    var url = window.location.href;
    window.history.pushState({}, 0, uri);
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]);
    return null;
}

function getData(page) {
    var code = getQueryString('checkcode');
    $.get('/cheep/pagination/cheep', {code: code, page: page}, function (data) {
        parseJson(data);
    }, 'json')
}

function getData2(page) {
    $.get('/cheep/search', $("#time-form").serialize() + '&pageNum=' + page, function (data) {
        parseJson(data);
    }, 'json')
}

function parseJson(data) {
    if (data.success) {
        var box = $("#qipu-box");
        $("#total").text(data.data.total);
        $("#pages").text(data.data.pages);
        box.empty();
        var index = data.data.startRow;
        $.each(data.data.list, function (i, log) {
            var content = '';
            if (log.isFalse) {
                content += '<tr class="table-danger">';
            } else {
                content += '<tr>'
            }
            content += '<td scope="row">' + (index++) + '</td>';
            content += '<td>' + log.trick + '</td>';
            if (log.color == 'z')
                content += '<td><img src="/img/r_zu.png" width="20" height="20"></td>';
            else
                content += ' <td><img src="/img/b_zu.png" width="20" height="20"></td>';
            if (log.type == 1)
                content += '<td>AI</td>';
            else
                content += '<td>人类</td>';
            content += '<td>' + log.createTime + '</td><td>' + log.message + '</td></tr>';
            box.append(content);
        })
    }
};
