/**
 * Created by blanke on 2017/4/4.
 */
var size = 10;
$(function () {
    $('#back').click(function () {
        $('#menuFrame', parent.document.body).attr('src', 'task_list.html')
    });
    //点击从模板添加
    $('#addFromTemplate').on('show.bs.modal', function () {
        initPagination();
    });
    //收藏列表 初始化分页
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
                        $('#table_task_template').empty();
                        for (var i in data.content) {
                            addTaskRowHtml(data.content[i]);
                        }
                    });
                }
            });
            $('#table_task_template').empty();
            for (var i in data.content) {
                addTaskRowHtml(data.content[i]);
            }
        });
    }

    //ajax 之后,把 每行的task 数据转换成 html, 添加到页面
    function addTaskRowHtml(item_data) {
        $('#table_task_template').append(getTaskRowItem(item_data));
    }

    //解析 ajax ,返回每行数据
    function getTaskRowItem(data) {
        var item_html = $("<div></div>");
        item_html.text(data.title);
        item_html.click(function () {
            task_id = data.id;
            $('#addFromTemplate').modal('hide');
            initTaskData();
        });
        return item_html;
    }

    // task list 上一页下一页 ajax,分页
    function getTask(page, size, callback) {
        $.get('/tasks/likes?page=' + page + "&size=" + size, callback);
    }

    $('#content_row_template').hide();//hide template
    //获取添加的 task 模板数据
    function initTaskData() {
        if (task_id) {
            //获得任务详情
            $.get('/task/' + task_id, function (data) {
                $('#task_title').val(data.title);
                $('#task_desc').val(data.description);
                $('#task_start_time').val(data.publishTime);
                $('#task_end_time').val(data.deadlineTime);
                //获得任务所有的字段
                $.get('/tasks/' + task_id + '/fields', function (data) {
                    if (data.length > 0) {
                        $('#div_fields').empty();
                    }
                    for (var i in data) {
                        addFieldRowItem(data[i]);
                    }
                });
            });
        }
    }

    //添加字段 div
    function addFieldRowItem(data) {
        var template = $('#div_field_template');
        var item_html = template.clone();
        item_html.find('.field_title').text(data.name);
        item_html.find('.field_config_name').text(data.config.name);
        item_html.find('.field_config_name').attr("title", data.config.description);
        item_html.attr('data', JSON.stringify(data));
        item_html.find('button').click(function () {
            $(this).parent().remove();
        });
        item_html.show();
        $('#div_fields').append(item_html);
    }

    //点击添加字段
    $('#addFieldDialog').on('show.bs.modal', function () {
        $('#dialog_field_name').val('');
        $('#dialog_field_desc').val('');
        $('#dialog_config_target_name').text('选择规则');
        $('#dialog_config_target_name').removeAttr('config_id');
        $('#dialog_config_target_name').removeAttr('title');
        $.get('/configs', function (data) {
            $('#ul_configs').empty();
            for (var i in data.content) {
                var item_data = data.content[i];
                var item_html = $('#dialog_config_item_template').clone();
                item_html.find("a").text(item_data.name);
                item_html.attr('title', item_data.description);
                item_html.attr('text', item_data.name);
                item_html.attr('config_id', item_data.id);
                item_html.attr('config_name', item_data.name);
                item_html.attr('id', 'config_' + item_data.id);
                item_html.click(function () {
                    $('#dialog_config_target_name').text($(this).attr('text'));
                    $('#dialog_config_target_name').attr('config_id', $(this).attr('config_id'));
                    $('#dialog_config_target_name').attr('config_name', $(this).attr('config_name'));
                    $('#dialog_config_target_name').attr('title', $(this).attr('title'));
                });
                item_html.show();
                $('#ul_configs').append(item_html);
            }
        });
    });
    //添加字段 dialog 点击确定
    $('#dialog_config_submit').click(function () {
        var config_id = $('#dialog_config_target_name').attr('config_id');
        if (config_id > 0) {
            $('#addFieldDialog').modal('hide');
            var field_name = $('#dialog_field_name').val();
            var field_desc = $('#dialog_field_desc').val();
            var config_name = $('#dialog_config_target_name').attr('config_name');
            addFieldRowItem({
                'name': field_name,
                'description': field_desc,
                'config': {
                    'name': config_name,
                    'id': config_id
                }
            })
        }
    });

    //保存
    $('#save').click(function () {
        var task_title = $('#task_title').val();
        var task_desc = $('#task_desc').val();
        var task_start_time = $('#task_start_time').val();
        var task_end_time = $('#task_end_time').val();
        var fields = $('#div_fields div');
        var data_json = {
            'title': task_title,
            'description': task_desc,
            'publishTime': task_start_time,
            'deadlineTime': task_end_time
        };
        var field_json = [];
        for (var i in fields) {
            var item_data = $(fields[i]).attr('data');
            var item_json = eval('('+item_data+')');
            console.log(item_json);
            field_json.push({
                'name': item_json.name,
                'description': item_json.description,
                'config_id': item_json.config.id
            });
        }
        data_json['configs'] = field_json;
        console.log(JSON.stringify(data_json));

    })
});