/**
 * Created by blanke on 2017/4/4.
 */
$(function () {
    $('#back').click(function () {
        $('#menuFrame', parent.document.body).attr('src', 'task_list.html')
    });
    var task_id = localStorage.getItem('click_task_id');
    if (task_id) {
        $.get('/task/' + task_id, function (data) {
            $('#task_title').val(data.title);
            $('#task_desc').val(data.description);
            $('#task_start_time').val(data.publishTime);
            $('#task_end_time').val(data.deadlineTime);
            //get task fields
            $.get('/tasks/' + task_id + '/fields', function (data) {
                console.log(data)

            })
        });
    }
})