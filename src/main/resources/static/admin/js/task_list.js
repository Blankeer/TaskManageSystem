var size = 10;
$(document).ready(function () {
    $('#bu_task_add').click(function () {
        localStorage.removeItem('click_task_id');//新增
        $('#menuFrame', parent.document.body).attr('src', 'task_add.html')
    });

    initPagination();
});
//初始化分页
function initPagination() {
    getTask(0, size, function (data) {
        $('#pagination').twbsPagination({
            totalPages: data.totalPages,
            visiblePages: 5,
            first: "首页",
            last: "尾页",
            prev: "上一页",
            next: "下一页",
            hideOnlyOnePage: true,
            onPageClick: function (event, page) {
                getTask(page - 1, size, function (data) {
                    $('#table_body').empty();
                    for (var i in data.content) {
                        addTaskRowHtml(data.content[i]);
                    }
                });
            }
        });
        for (var i in data.content) {
            addTaskRowHtml(data.content[i]);
        }
    });
}
//ajax 之后,把 每行的task 数据转换成 html, 添加到页面
function addTaskRowHtml(item_data) {
    $('#table_body').append(getTaskRowItem(item_data));
    getTaskState(item_data.id);
}
//ajax,获得任务状态
function getTaskState(task_id) {
    $.get("/tasks/" + task_id + "/contents", function (data) {
        //todo 根据是否提交，通过css改变颜色等
        var task_row = $("#" + getTaskViewId(task_id));
        task_row.append(data.submit);
        task_row.append(data.verify);
    });
}
//解析 ajax ,返回每行数据
function getTaskRowItem(data) {
    var item_html = $("<div></div>");
    item_html.attr('id', getTaskViewId(data.id));
    var item = $("<div></div>");
    item.addClass('task_item');
    var item_title = item.clone();
    item_title.text(data.title);
    item_html.append(item_title);
    var item_pubtime = item.clone();
    var item_endtime = item.clone();
    item_pubtime.text(formatDate(data.publishTime));
    item_endtime.text(formatDate(data.deadlineTime));
    item_html.append(item_pubtime);
    item_html.append(item_endtime);
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
function getTask(page, size, callback) {
    $.get('/tasks?page=' + page + "&size=" + size, callback);
}
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
