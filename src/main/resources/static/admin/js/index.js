$(document).ready(function () {
    //进入管理员之后,校验 token, 并检查是不是管理员权限
    $.get('/token/check', function (data) {
        var name = data.nickName;
        if (!name) {
            name = data.email;
        }
        if (!data.isAdmin) {//不是管理员直接跳转到 login
            logout();
        }
        $('#username').text(name);
    }, function () {
        logout();
    });

    $('#logout').click(logout);

    function logout() {
        $.clearToken();
        location.href = "/login.html";
    }
});
