var size = 10;
var click_config_id = -1;
$(document).ready(function () {
    $('#bu_config_add').click(function () {
        click_config_id = -1;//清空
    });

    initPagination();

    //config 详情
    $('#addConfigDialog').on('show.bs.modal', function () {
        if (click_config_id > 0) {
            $('#dialog_field_title').text('修改规则');
            $('#config_delete').show();
            $.get('/configs/' + click_config_id, function (data) {
                $('#dialog_config_name').val(data.name);
                $('#dialog_config_desc').val(data.description);
                $('#dialog_config_reg').val(data.expression);
            });
            //更新
            $('#dialog_config_submit').unbind("click");//取消之前的监听
            $('#dialog_config_submit').click(function () {
                var data = {
                    'name': $('#dialog_config_name').val(),
                    'description': $('#dialog_config_desc').val(),
                    'expression': $('#dialog_config_reg').val()
                };
                $.put('/configs/' + click_config_id, data, function () {
                    $.msg_success("修改成功");
                    reLoad();
                });
            });
            //删除
            $('#config_delete').unbind("click");
            $('#config_delete').click(function () {
                $.delete('/configs/' + click_config_id, function () {
                    $.msg_success("删除成功");
                    reLoad();
                })
            })
        } else {
            $('#dialog_field_title').text('添加规则');
            $('#dialog_config_name').val('');
            $('#dialog_config_desc').val('');
            $('#dialog_config_reg').val('');
            $('#config_delete').hide();
            //add
            $('#dialog_config_submit').unbind("click");
            $('#dialog_config_submit').click(function () {
                var data = {
                    'name': $('#dialog_config_name').val(),
                    'description': $('#dialog_config_desc').val(),
                    'expression': $('#dialog_config_reg').val()
                };
                $.post('/configs/', data, function () {
                    $.msg_success("添加成功");
                    reLoad();
                });
            });
        }
    });
});
//重新加载该页面
function reLoad() {
    $('#addConfigDialog').modal('hide');
    initPagination();
}
//初始化分页
function initPagination() {
    $('#pagination').twbsPagination('destroy');
    getConfig(0, size, function (data) {
        $('#pagination').twbsPagination({
            totalPages: data.totalPages,
            visiblePages: 5,
            first: "首页",
            last: "尾页",
            prev: "上一页",
            next: "下一页",
            hideOnlyOnePage: true,
            onPageClick: function (event, page) {
                getConfig(page - 1, size, function (data) {
                    $('#table_body').empty();
                    for (var i in data.content) {
                        addConfigRowHtml(data.content[i]);
                    }
                });
            }
        });
    });
}
//ajax 之后,把 数据转换成 html, 添加到页面
function addConfigRowHtml(item_data) {
    $('#table_body').append(getConfigRowItem(item_data));
}
//解析 ajax ,返回每行数据
function getConfigRowItem(data) {
    var item_html = $("#config_item_template").clone();
    item_html.attr('id', getConfigViewId(data.id));
    item_html.addClass('config_item');
    var config_name = item_html.find('.config_name');
    var config_desc = item_html.find('.config_desc');
    config_name.text(data.name);
    config_desc.text(data.description);
    item_html.show();
    //跳转到详情
    item_html.click(function () {
        click_config_id = data.id;
        $('#addConfigDialog').modal('show');
    });
    return item_html;
}
//获取 控件 id
function getConfigViewId(id) {
    return "config_" + id
}
//上一页下一页 ajax,分页
function getConfig(page, size, callback) {
    $.get('/configs?page=' + page + "&size=" + size, callback);
}
