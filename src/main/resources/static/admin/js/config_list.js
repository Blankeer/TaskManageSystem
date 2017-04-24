var size = 10;
var click_config_id = -1;
$(document).ready(function () {
    $('#bu_config_add').click(function () {
        click_config_id = -1;//清空
        // localStorage.removeItem('click_task_id');//新增
        // $('#menuFrame', parent.document.body).attr('src', 'task_add.html')
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
            $('#dialog_config_submit').click(function () {
                var data = {
                    'name': $('#dialog_config_name').val(),
                    'description': $('#dialog_config_desc').val(),
                    'expression': $('#dialog_config_reg').val()
                };
                $.put('/configs/' + click_config_id, data, function () {
                    //TODO
                });
            });
            //删除
            $('#config_delete').click(function () {
                $.delete('/configs/' + click_config_id, function () {
                    //TODO
                })
            })
        } else {
            $('#dialog_field_title').text('添加规则');
            $('#config_delete').hide();
            //add
            $('#dialog_config_submit').click(function () {
                var data = {
                    'name': $('#dialog_config_name').val(),
                    'description': $('#dialog_config_desc').val(),
                    'expression': $('#dialog_config_reg').val()
                };
                $.push('/configs/', data, function () {
                    //TODO
                });
            });
        }
    });
});
//初始化分页
function initPagination() {
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
        for (var i in data.content) {
            addConfigRowHtml(data.content[i]);
        }
    });
}
//ajax 之后,把 数据转换成 html, 添加到页面
function addConfigRowHtml(item_data) {
    $('#table_body').append(getConfigRowItem(item_data));
}
//解析 ajax ,返回每行数据
function getConfigRowItem(data) {
    var item_html = $("<div></div>");
    item_html.attr('id', getConfigViewId(data.id));
    var item = $("<div></div>");
    item.addClass('config_item');
    var item_title = item.clone();
    item_title.text(data.name);
    item_html.append(item_title);
    //跳转到详情
    item_html.click(function () {
        click_config_id = data.id;
        $('#addConfigDialog').modal('show');
    });
    return item_html
}
//获取 控件 id
function getConfigViewId(id) {
    return "config_" + id
}
//上一页下一页 ajax,分页
function getConfig(page, size, callback) {
    $.get('/configs?page=' + page + "&size=" + size, callback);
}
