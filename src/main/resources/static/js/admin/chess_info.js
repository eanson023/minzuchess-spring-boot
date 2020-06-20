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
                url: '/admin/chess/chess_info.json',
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
            {"data": "code"},
            {"data": "clock", "orderable": false, "searchable": false},
            {"data": "alias"},
            {"data": "isPublicStr", "searchable": false},
            {"data": "value", "searchable": false},
            {"data": "userId", "orderable": false},
            {"data": "realName"}
        ]
    });
});