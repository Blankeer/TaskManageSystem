var size = 10;
var click_user_id = -1;
$(document).ready(function () {

    initPagination();

    //user 详情
    $('#addUserDialog').on('show.bs.modal', function () {
        if (click_user_id > 0) {
            $.get('/users/' + click_user_id, function (data) {
                $('#dialog_user_email').val(data.email);
                $('#dialog_user_nickname').val(data.nickName);
            });
            //更新
            $('#dialog_user_submit').click(function () {
                var data = {
                    'email': $('#dialog_user_email').val(),
                    'nickName': $('#dialog_user_nickname').val(),
                };
                $.put('/users/' + click_user_id, data, function () {
                    $.msg_success("修改成功");
                    reLoad();
                });
            });
            //删除
            $('#user_delete').click(function () {
                $.delete('/users/' + click_user_id, function () {
                    $.msg_success("删除成功");
                    reLoad();
                })
            })
        }
    });
});
//重新加载该页面
function reLoad() {
    $('#addUserDialog').modal('hide');
    initPagination();
}
//初始化分页
function initPagination() {
    $('#pagination').twbsPagination('destroy');
    getUsers(0, size, function (data) {
        $('#pagination').twbsPagination({
            totalPages: data.totalPages,
            visiblePages: 5,
            first: "首页",
            last: "尾页",
            prev: "上一页",
            next: "下一页",
            hideOnlyOnePage: true,
            onPageClick: function (event, page) {
                getUsers(page - 1, size, function (data) {
                    $('#table_body').empty();
                    for (var i in data.content) {
                        addUserRowHtml(data.content[i]);
                    }
                });
            }
        });
    });
}
//ajax 之后,把 数据转换成 html, 添加到页面
function addUserRowHtml(item_data) {
    $('#table_body').append(getUserRowItem(item_data));
}
//解析 ajax ,返回每行数据
function getUserRowItem(data) {
    var item_html = $("#user_item_template").clone();
    item_html.attr('id', getUserViewId(data.id));
    item_html.addClass('users_item');
    var user_name = item_html.find('.user_name');
    var user_email = item_html.find('.user_email');
    user_name.text(data.nickName);
    user_email.text(data.email);
    item_html.show();
    //点击每行
    item_html.click(function () {
        click_user_id = data.id;
        $('#addUserDialog').modal('show');
    });
    return item_html
}
//获取 控件 id
function getUserViewId(id) {
    return "user_" + id
}
//上一页下一页 ajax,分页
function getUsers(page, size, callback) {
    $.get('/users?page=' + page + "&size=" + size, callback);
}
