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
                url: '/admin/user/user_info.json',
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
        // ajax: {
        //     'url': '/admin/user/user_log2.json',
        //     'type': 'POST'
        // },
        //默认排序
        "order": [[3, 'desc']],
        "columns": [
            {
                "sClass": "text-center",
                "data": "avatar",
                "orderable": false,
                "searchable": false,
                "render": function (data, type, row, meta) {
                    var content = '';
                    if (data == null || data == '') {
                        content = '<img src="/img/avatar/default.jpg" width="20" height="20"/>';
                    } else {
                        content = '<img src="/img/avatar/' + data + ' "width="20" height="20" />';
                    }
                    return content;
                }
            },
            {"data": "realName"},
            {"data": "telephone"},
            {"data": "username"},
            {"data": "joinDay", "searchable": false},
            {"data": "isAdminAdd", "searchable": false},
            {"data": "isFinishReg", "searchable": false}
        ]
    });
});

//封装查询参数
function getQueryCondition(data) {
    var param = {};
    //组装排序参数
    // param.id = $("#id-search").val();//查询条件
    // param.username = $("#name-search").val();//查询条件
    // param.enable = $("#status-search").val();//查询条件
    //组装分页参数
    param.start = data.start;
    param.length = data.length;
    param.draw = data.draw;
    return param;
}