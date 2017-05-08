/**
 * Created by blanke on 2017/4/4.
 */
var task_expire = false;//任务是否失效,即超过了截止时间
$(function () {
    $('#back').click(function () {
        $('#menuFrame', parent.document.body).attr('src', 'task_list.html')
    });
    var task_id = localStorage.getItem('click_task_id');
    $('#content_row_template').hide();//hide template
    //点击修改按钮,跳转到修改表单页面 task_add.html
    $('#update').click(function () {
        $('#menuFrame', parent.document.body).attr('src', 'task_add.html')
    });
    if (task_id) {
        //获得任务详情
        $.get('/task/' + task_id, function (data) {
            $('#task_title').text(data.title);
            $('#task_desc').text(data.description);
            $('#task_start_time').text($.formatDate(data.publishTime));
            $('#task_end_time').text($.formatDate(data.deadlineTime));
            $('#user_count').text(data.userCount);
            $('#content_count').text(data.contentCount);
            $('#wait_content_count').text(data.waitContentCount);
            $('#pass_content_count').text(data.passContentCount);
            task_expire = new Date() > new Date(data.deadlineTime);
            if (task_expire) {
                $('#update').hide();
                // $('#delete').hide();//隐藏修改 删除按钮
                $.msg_waring("任务已经超过截止时间,只能查看数据")
            }
            //导出数据
            $('#export_data').click(function () {
                window.open('/tasks/' + task_id + '/contents/export');
            });
            loadContntData();
        });
        //获得收藏状态
        initLikeState();
    }
    //初始化收藏状态
    function initLikeState() {
        $.get('/tasks/' + task_id + '/is-like', function () {
            $('#is_like').text("取消收藏");
            $('#is_like').unbind("click");
            $('#is_like').click(function () {
                $.delete('/tasks/' + task_id + '/likes/', function () {
                    initLikeState();
                });
            });
        }, function () {
            $('#is_like').text("收藏");
            $('#is_like').unbind("click");
            $('#is_like').click(function () {//点击收藏
                $.post('/tasks/' + task_id + '/likes/', null, function () {
                    initLikeState();
                    $.msg_success("收藏成功");
                });
            });
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
            //获得任务内容
            $.get('/tasks/' + task_id + '/contents', function (data) {
                data = data.content;
                row_count = data.length;
                for (var i in data) {
                    content_row = data[i];
                    content_row_html = getContentRow(task_id, content_row, i);
                    div_contents.append(content_row_html);
                    content_row_html.show();
                }
            });
        });
    }

    //获得 tag 的 html
    function getTagHtml() {
        return $('<div class="label content_tag"></div>');
    }


    //根据后端的 field id 生成前端的 dom id
    function getFieldId(fieldId) {
        return 'field-' + fieldId;
    }

    //返回 field 对应 content 的 id
    function getContentId(contetnId) {
        return 'content-' + contetnId;
    }

    // 每条 content 的 html
    function getContentRow(task_id, content, row_index) {
        row_html = $('#content_row_template').clone();
        row_html.attr('id', getContentRowId(row_index));
        row_html.find('.div_user').text(content.user.nickName);
        row_html.find('.div_time').text($.formatDate(content.updatedAt));
        var verify_bus = row_html.find('.verify_bus');//审核按钮组
        var content_state = row_html.find('.content_state');//状态 tag 的 div
        if (content.state != 0 || task_expire) {//已经通过或拒绝
            verify_bus.hide();//已经审核过了,不需要显示审核按钮了
        }
        //显示状态信息,未审核  已通过,已驳回
        var submit_tag = getTagHtml();
        if (content.state == 1) {
            submit_tag.text("已通过");
            submit_tag.addClass('label-info');
        } else if (content.state == -1) {
            submit_tag.text("已驳回");
            submit_tag.addClass('label-warning');
        } else {
            submit_tag.text("未审核");
            submit_tag.addClass('label-info');
        }
        content_state.append(submit_tag);

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
            input_value.text(item.value);
            input_value.attr('reg', config.expression);
            input_value.attr('title', config.name + '(' + config.description + ')');
            input_value.attr('field_id', field.id);
            item_html.show();
            item_html.attr("display", "inline-block");
            div_contents.append(item_html);
        }
        var bu_pass = row_html.find('.content_pass');//通过
        var bu_dismiss = row_html.find('.content_dismiss');//驳回
        var verifyListener = function () {
            var isPass = $(this).hasClass('content_pass');
            $.get("/contents/" + content.id + "?pass=" + isPass, function () {
                $.msg_success("操作成功");
            });
        };
        bu_pass.click(verifyListener);
        bu_dismiss.click(verifyListener);
        return row_html;
    }

    function getContentRowId(row_index) {
        return 'content-row-' + row_index;
    }

    //删除任务
    $('#delete').click(function () {
        var r = confirm("确定删除?");
        if (r == false) {
            return;
        }
        $.delete('/tasks/' + task_id, function () {
            $.msg_success("删除成功");
            $('#back').click();
        })
    });
});