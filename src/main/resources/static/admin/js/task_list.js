var size = 10;
var key = '';
$(document).ready(function () {
    $('#bu_task_add').click(function () {
        localStorage.removeItem('click_task_id');//新增
        $('#menuFrame', parent.document.body).attr('src', 'task_add.html')
    });
    initPagination();
    $('#search_text').change(function () {
        key = $(this).val();
        initPagination();
    });
    //点击我的收藏
    $('#my_like').click(function () {
        $('#menuFrame', parent.document.body).attr('src', 'task_like.html')
    })
});
//初始化分页
function initPagination() {
    $('#pagination').twbsPagination('destroy');
    getTask(0, size, key, function (data) {
        $('#pagination').twbsPagination({
            totalPages: data.totalPages,
            visiblePages: 5,
            first: "首页",
            last: "尾页",
            prev: "上一页",
            next: "下一页",
            hideOnlyOnePage: true,
            onPageClick: function (event, page) {
                getTask(page - 1, size, key, function (data) {
                    $('#table_body').empty();
                    for (var i in data.content) {
                        addTaskRowHtml(data.content[i]);
                    }
                });
            }
        });
    });
}
//ajax 之后,把 每行的task 数据转换成 html, 添加到页面
function addTaskRowHtml(item_data) {
    $('#table_body').append(getTaskRowItem(item_data));
}
//解析 ajax ,返回每行数据
function getTaskRowItem(data) {
    var item_html = $("#task_item_template").clone();
    item_html.attr('id', getTaskViewId(data.id));
    var item_title = item_html.find(".task_title");
    var task_submit = item_html.find(".task_submit");
    var task_verify = item_html.find(".task_verify");
    var task_pubtime = item_html.find(".task_pubtime");
    var task_endtime = item_html.find(".task_endtime");
    item_title.text(data.title);
    task_pubtime.text($.formatDate(data.publishTime));
    task_endtime.text($.formatDate(data.deadlineTime));
    item_html.show();
    //跳转到任务详情
    item_html.click(function () {
        localStorage.setItem('click_task_id', data.id);
        $('#menuFrame', parent.document.body).attr('src', 'task_detail.html')
    });
    return item_html
}
//获取 task 控件 id
function getTaskViewId(task_id) {
    return "task_" + task_id
}
//上一页下一页 ajax,分页
function getTask(page, size, key, callback) {
    $.get('/tasks?page=' + page + "&size=" + size + "&key=" + key, callback);
}

