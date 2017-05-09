$(document).ready(function () {
    //普通用户进入主页,加载完后首先进行权限校验,也就是判断这个 token 存不存在
    $.get('/token/check', function (data) {
        var name=data.nickName;
        if(!name){
            name=data.email;
        }
        $('#username').text(name);//设置用户昵称
    }, function () {
        logout();//token 验证识别,直接执行退出逻辑,也就是跳转到登录页面
    });

    $('#logout').click(logout);

    function logout() {
        $.clearToken();
        location.href = "/login.html";
    }
});
