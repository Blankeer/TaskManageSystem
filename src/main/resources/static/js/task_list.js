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
            // columnDefs: [
            //     {
            //         targets: 5,
            //         data: "updated_at",
            //         title: "",
            //         render: function (data, type, row, meta) {
            //             return new Date(Date.parse(data)).Format("yyyy-MM-dd hh:mm:ss");
            //         }
            //     }],
            // //加载完的Init
            // initComplete: function () {
            // },
            columns: [{
                data: 'id'
            }, {
                data: 'title'
            }, {
                data: 'description'
            }]
        })
        ;
});