$(document).ready(function () {
    $.get('/token/check', function (data) {
        var name=data.nickName;
        if(!name){
            name=data.email;
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
