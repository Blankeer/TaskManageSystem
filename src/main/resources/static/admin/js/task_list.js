var page = -1;
var size = 10;
$(document).ready(function () {
    $('#bu_task_add').click(function () {
        localStorage.removeItem('click_task_id');//新增
        $('#menuFrame', parent.document.body).attr('src', 'task_detail.html')
    });
    //配置分页
    $("#next").click(function () {
        if ($(this).hasClass("disabled")) {
            return;
        }
        page++;
        enablePrevious()
        getTask(page, size, function (data) {
            $('#table_body').empty()
            for (var i in data.content) {
                $('#table_body').append(getTaskRowItem(data.content[i]));
                getTaskState(data.content[i].id);
            }
            if (data.content.length < size) {
                disableNext()
                if (page == 0) {
                    disablePrevious()
                }
            }
        });
    });
    $("#previous").click(function () {
        if ($(this).hasClass("disabled")) {
            return;
        }
        page--;
        if (page <= 0) {
            disablePrevious()
        }
        enableNext()
        getTask(page, size, function (data) {
            $('#table_body').empty()
            for (var i in data.content) {
                $('#table_body').append(getTaskRowItem(data.content[i]));
                getTaskState(data.content[i].id);
            }
        });
    });
    $("#next").click()
});
//ajax,获得任务状态
function getTaskState(task_id) {
    $.get("/tasks/" + task_id + "/contents", function (data) {
        //todo 根据是否提交，通过css改变颜色等
        var task_row = $("#" + getTaskViewId(task_id));
        task_row.append(data.submit);
        task_row.append(data.verify);
    });
}
function enablePrevious() {
    $('#previous').removeClass('disabled')
    $('#previous').parent().removeClass('disabled')
}
function disablePrevious() {
    $('#previous').addClass("disabled")
    $('#previous').parent().addClass("disabled")

}
function enableNext() {
    $('#next').removeClass('disabled')
    $('#next').parent().removeClass('disabled')
}
function disableNext() {
    $('#next').addClass("disabled")
    $('#next').parent().addClass("disabled")

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
