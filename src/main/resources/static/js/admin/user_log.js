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
                url: '/admin/user/user_log.json',
                data: JSON.stringify(data),
                success: function (data) {
                    var returnData = {};
                    returnData.recordsTotal = data.recordsTotal;//总记录数
                    returnData.recordsFiltered = data.recordsFiltered;//后台不实现过滤功能，每次查询均视作全部结果
                    returnData.data = data.data;
                    callback(returnData);
                }
            });
            // var param = getQueryCondition(data);
            // var param = JSON.stringify(data);
            // $.post('/admin/user/user_log2.json', param, function (data) {
            //     //封装返回数据  如果参数相同，可以直接返回result ，此处作为学习，先这么写了。
            //     var returnData = {};
            //     // returnData.draw = data.draw;//这里直接自行返回了draw计数器,应该由后台返回
            //     data = data.data;
            //     returnData.recordsTotal = data.total;//总记录数
            //     returnData.recordsFiltered = data.total;//后台不实现过滤功能，每次查询均视作全部结果
            //     returnData.data = data.list;
            //     //调用DataTables提供的callback方法，代表数据已封装完成并传回DataTables进行渲染
            //     //此时的数据需确保正确无误，异常判断应在执行此回调前自行处理完毕
            //     callback(returnData);
            // }, 'json');
        },
        // ajax: {
        //     'url': '/admin/user/user_log2.json',
        //     'type': 'POST'
        // },
        //默认排序
        "order": [[4, 'desc']],
        "columns": [
            {"data": "realName", "orderable": false},
            {"data": "userId"},
            {"data": "ip"},
            {"data": "message", "orderable": false, "searchable": false},
            {"data": "createTime", "searchable": false}
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