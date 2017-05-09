$(document).ready(function () {
    //进入该页面,加载完后调用 API 获取用户信息
    $.get('/user/profile', function (data) {
        $('#user_nickname').val(data.nickName);//设置昵称
    });
    //下面两个是 tab 导航栏切换按钮点击事件,都是 Jquery 调用 css
    $('#nav_info').click(function () {
        $(this).addClass('active');
        $('#nav_pwd').removeClass('active');
        $('#div_info').show();
        $('#div_pwd').hide();
    });
    $('#nav_pwd').click(function () {
        $(this).addClass('active');
        $('#nav_info').removeClass('active');
        $('#div_info').hide();
        $('#div_pwd').show();
    });
    //点击修改昵称按钮
    $('#update_nickname').click(function () {
        var nickName = $('#user_nickname').val();
        $.put('/change_nickname/?nickname=' + nickName, null, function () {
            $.msg_success("修改昵称成功");
        })
    });
    //点击修改密码按钮
    $('#dialog_update_pwd_submit').click(function () {
        var newPwd1 = $('#dialog_new_pwd').val();
        var newPwd2 = $('#dialog_new_pwd2').val();
        if (newPwd1 != newPwd2) {
            $.msg_waring("两次输入新密码不一致");
            return;
        }
        var data = {
            'oldPwd': $('#dialog_old_pwd').val(),
            'newPwd': newPwd1
        };
        $.put('/change_pwd', data, function () {
            $.msg_success("修改密码成功");
        })
    });
});