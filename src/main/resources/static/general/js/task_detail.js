/**
 * Created by blanke on 2017/4/4.
 */
$(function () {
    $('#back').click(function () {
        $('#menuFrame', parent.document.body).attr('src', 'task_list.html')
    });
    var task_id = localStorage.getItem('click_task_id');
    $('#content_row_template').hide();//hide template
    if (task_id) {
        //获得任务详情
        $.get('/task/' + task_id, function (data) {
            $('#task_title').val(data.title);
            $('#task_desc').val(data.description);
            $('#task_start_time').val(data.publishTime);
            $('#task_end_time').val(data.deadlineTime);
            loadContntData();
        });

    }
    //ajax 获得用户提交的数据, 可能是进入页面调用,也可能是删除或提交内容之后调用
    function loadContntData() {
        //get task fields
        var div_fields = $('#field_div');
        var div_contents = $('#contents_div');
        div_contents.empty();//删除子元素
        var row_count = 0;
        //获得任务所有的字段
        $.get('/tasks/' + task_id + '/fields', function (data) {
            var data_fields = data;
            //获得表单内容
            $.get('/tasks/' + task_id + '/contents', function (data) {
                // data=data.content;TODO
                row_count = data.length;
                for (var i in data) {
                    content_row = data[i];
                    content_row_html = getContentRow(task_id, content_row, i);
                    div_contents.append(content_row_html);
                    content_row_html.show();
                }
                addTipListener();
                //新增一条内容
                $('#add_content_row').click(function () {
                    //mock 模拟服务端返回的数据,拼凑类似的 json
                    var mock_contents = [];
                    for (var i in data_fields) {
                        var field = data_fields[i];
                        mock_contents.push({
                            "id": 0,
                            "value": "",
                            "isVerify": false,
                            "field": field
                        })
                    }
                    content_row = {
                        "id": 0,
                        "isSubmit": false,
                        "isVerify": false,
                        "items": mock_contents
                    };
                    content_row_html = getContentRow(task_id, content_row, row_count);
                    row_count = row_count + 1;
                    div_contents.append(content_row_html);
                    content_row_html.show();
                    addTipListener();
                });
            });
        });
    }

    //为输入框添加提示监听器,和检测合法监听器
    function addTipListener() {
        //鼠标接触会有小提示
        $('[data-toggle="tooltip"]').mouseenter(function () {
            $(this).tooltip("show");
        });
        //输入文字改变检测是否合法
        $('input.content_value').bind("input", function () {
            var text = $(this).val();
            var verify = new RegExp($(this).attr('reg')).test(text);
            var cls_succ = 'has-success';
            var cls_error = 'has-error';
            var cls = cls_succ;
            $(this).parent('div').removeClass(cls_succ);
            $(this).parent('div').removeClass(cls_error);
            if (verify == false) {
                cls = 'has-error';
            }
            $(this).parent('div').addClass(cls);
        });
    }

    //根据后端的 field id 生成前端的 dom id
    function getFieldId(fieldId) {
        return 'field-' + fieldId;
    }

    //返回 field 对应 content 的 id
    function getContentId(contetnId) {
        return 'content-' + contetnId;
    }

    //返回 field 对应的 item html
    function getFieldItem(field) {
        field_html = $("<span>" + field.name + "</span>");
        field_html.attr('id', getFieldId(field.id));
        field_html.attr('field_id', field.id);
        field_html.attr('config_id', field.config.id);
        field_html.attr('config_expression', field.config.expression);
        return field_html;
    }

    //row_index是提交内容第几行
    function getContentItem(contentItem, row_index) {
        content_html = $('<input class="form-control input_text attr_value">');
        content_html.attr('id', getContentId(contentItem.id));
        content_html.attr('field_id', contentItem.field.id);
        content_html.attr('content_id', contentItem.id);
        content_html.attr('row_index', row_index);
        content_html.val(contentItem.value);
        return content_html;
    }

    // 每条 content 的 html
    function getContentRow(task_id, content, row_index) {
        row_html = $('#content_row_template').clone();
        row_html.attr('id', getContentRowId(row_index));
        row_html.find('.content_row_save').text(content.id > 0);
        row_html.find('.content_row_submit').text(content.isSubmit);
        row_html.find('.content_row_verify').text(content.state);
        template = row_html.find('.content_item');
        template_item = template.clone();
        template.hide();
        div_contents = row_html.find('.div_content');
        for (j in content.items) {
            item = content.items[j];
            field = item.field;
            config = field.config;
            item_html = template_item.clone();
            var div_field = item_html.find('.div_content_field');
            div_field.text(field.name);
            div_field.attr('title', field.description);
            var input_value = item_html.find('.content_value');
            input_value.val(item.value);
            input_value.attr('reg', config.expression);
            input_value.attr('title', config.name + '(' + config.description + ')');
            input_value.attr('field_id', field.id);
            item_html.show();
            item_html.attr("display", "inline-block");
            div_contents.append(item_html);
        }
        var bu_save = row_html.find('.content_save');
        var bu_submit = row_html.find('.content_submit');
        var bu_delete = row_html.find('.content_delete');
        var saveSubmitListener = function (event) {
            var content_id = getContentRowId(row_index);
            if (isJsVerify(content_id)) {
                data = getContentData(content_id);
                data['submit'] = $(this).hasClass('content_submit');
                var url = '/tasks/' + task_id + '/contents/';
                if (content.id > 0) {//update
                    $.put(url + content.id, data, function (res) {
                        loadContntData();
                    });
                } else {
                    $.post(url, data, function (res) {
                        loadContntData();
                    });
                }
            }
        };
        bu_save.click(saveSubmitListener);
        bu_submit.click(saveSubmitListener);
        bu_delete.click(function () {
            if (content.id > 0) {
                var r = confirm("确定删除?");
                if (r == false) {
                    return;
                }
                $.delete('/tasks/' + task_id + '/contents/' + content.id, function () {
                    loadContntData();
                });
            } else {
                var div_contents = $('#contents_div')

                var content_id = getContentRowId(row_index);
                $('#' + content_id).hide();
            }
        });
        return row_html;
    }

    //检查所有输入是否合法,不合法弹出提示
    function isJsVerify(row_id) {
        var error_inputs = $('#' + row_id).find('div.has-error');
        if (error_inputs.length == 0) {
            return true;
        }
        error_inputs.each(function (i) {
            $(this).children('input').tooltip("show");//
        });
        return false;
    }

    //获得 content 用户输入的内容,发送给服务端
    function getContentData(row_id) {
        var input_contents = $('#' + row_id).find('input[field_id]');
        var data = [];
        input_contents.each(function (i) {
            var field_id = $(this).attr('field_id');
            var val = $(this).val();
            data.push({
                'fieldId': field_id,
                'value': val
            });
        });
        return {'data': data};
    }

    function getContentRowId(row_index) {
        return 'content-row-' + row_index;
    }
});