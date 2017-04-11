$(document).ready(function () {

    //配置DataTables默认参数
    $.extend(true, $.fn.dataTable.defaults, {
        "language": {
            "url": "/assets/Jquery_Table_Chinese"
        },
        "dom": "l<'#toolbar'>frtip"
    });

    var table = $('#table_task_list').DataTable({
        'ajax': '/tasks',
        'serverSide': true,
        "ordering": false,
        // order: [[4, "desc"]],
        //行被创建回调
        createdRow: function (row, data, dataIndex) {
            //请求content状态，显示是否提交，是否审核通过
            $.get("/content", {task_id: data.id}, function (data, status) {
                // alert(dataIndex + ":" + data);
                //todo 根据是否提交，通过css改变颜色等
                $(row).children().eq(1).text(data.submit);
                $(row).children().eq(2).text(data.verify);
            });
            $(row).click(function () {
                localStorage.setItem('click_task_id', data.id);
                $('#menuFrame', parent.document.body).attr('src', 'task_detail.html')
            });
        },
        //每行的显示调整，主要做了时间戳显示转换
        columnDefs: [
            {
                targets: 0,
                data: 'title'
            }, {
                targets: 1,
                data: null,
                render: function (data, type, row, meta) {
                    return "false";
                }
            }, {
                targets: 2,
                data: null,
                render: function (data, type, row, meta) {
                    return "false";
                }
            }, {
                targets: 3,
                data: "publishTime",
                render: function (data, type, row, meta) {
                    return formatDate(data);
                }
            }, {
                targets: 4,
                data: "deadlineTime",
                render: function (data, type, row, meta) {
                    return formatDate(data);
                }
            }],
        //加载完的Init
        initComplete: function () {
            $('tr').css('background-color', 'rgba(0,0,0,0)');
        },
        // columns: [{
        //     data: 'title'
        // }, {
        //     data: null
        // }, {
        //     data: null
        // }, {
        //     data: 'publishTime'
        // }, {
        //     data: 'deadlineTime'
        // }]
    });
});
function formatDate(time) {
    if (time == null) {
        return "不知道什么时间";
    }
    var date = new Date(time);
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    var D = date.getDate() + ' ';
    var h = date.getHours() + ':';
    var m = date.getMinutes();
    return Y + M + D + h + m;
}
