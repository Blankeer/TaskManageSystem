$(document).ready(function () {
    var table = $('#table_task_list').DataTable({
        'ajax' : '/tasks',
        'serverSide': true,
        columns: [{
            data: 'id'
        }, {
            data: 'title'
        }, {
            data: 'description'
        }]
    });
});