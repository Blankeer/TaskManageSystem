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
            // order: [[4, "desc"]],
            //行被创建回调
            // createdRow: function (row, data, dataIndex) {
            //
            // },
            //每行的显示调整
            columnDefs: [
                {
                    targets: 2,
                    data: "publishTime",
                    render: function (data, type, row, meta) {
                        return formatDate(data);
                    }
                }, {
                    targets: 3,
                    data: "deadlineTime",
                    render: function (data, type, row, meta) {
                        return formatDate(data);
                    }
                }],
            // //加载完的Init
            // initComplete: function () {
            // },
            columns: [{
                data: 'id'
            }, {
                data: 'title'
            }, {
                data: 'publishTime'
            }, {
                data: 'deadlineTime'
            }]
        })
        ;
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
