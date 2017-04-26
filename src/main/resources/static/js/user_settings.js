$(document).ready(function () {
    $.get('/user/profile', function (data) {
        $('#user_nickname').val(data.nickName);
    });
    $('#update_nickname').click(function () {
        var nickName = $('#user_nickname').val();
        $.put('/change_nickname/?nickname=' + nickName, null, function () {

        })
    });
    //修改密码
    $('#updatePwdDialog').on('show.bs.modal', function () {
        $('#dialog_update_pwd_submit').click(function () {
            var newPwd1 = $('#dialog_new_pwd').val();
            var newPwd2 = $('#dialog_new_pwd2').val();
            if (newPwd1 != newPwd2) {
                alert("两次输入新密码不一致");
                return;
            }
            var data = {
                'oldPwd': $('#dialog_old_pwd').val(),
                'newPwd': newPwd1
            };
            $.put('/change_pwd', data, function () {

            })
        });
    });
});