var InterValObj; //timer变量，控制时间
var count = 60; //间隔函数，1秒执行
var curCount;//当前剩余秒数
$(document).ready(function () {
    $('.a_login').click(function () {
        $(".register-form").hide("fast");
        $(".login-form").show("fast");
        $(".forget-form").hide("fast");
        $(".forget-form-2").hide("fast");
    });
    $('.a_register').click(function () {
        $(".register-form").show("fast");
        $(".login-form").hide("fast");
        $(".forget-form").hide("fast");
        $(".forget-form-2").hide("fast");
    });
    $(".label_forget").click(function () {
        $(".register-form").hide("fast");
        $(".login-form").hide("fast");
        $(".forget-form").show("fast");
        $(".forget-form-2").hide("fast");
    });
    $('#bu_login').click(login);
    $('#findpwd_submit').click(findPwd);
    $('#bu_register').click(register);
    $('#findpwd_get_capcha').click(findPwdGetChpcha);
    $('#reg_get_capcha').click(regGetChpcha);
});
function login() {
    var account = $('#login_account').val();
    var pwd = $('#login_password').val();
    if (!emailCheck(account)) {
        $.msg_error("邮箱格式不正确");
        return;
    }
    var data = {
        "email": account,
        "pwd": pwd
    };
    $.post("/login/", data, function (data) {
        $.saveToken(data.token);//保存 token
        var url = "/general/index.html";
        if (data.isAdmin) {
            url = "/admin/index.html";
        }
        location.href = url;
    });
}
function register() {
    var account = $('#reg_account').val();
    var pwd = $('#reg_password').val();
    var pwd2 = $('#reg_password_agin').val();
    var cha = $("#reg_capcha").val();
    if (!emailCheck(account)) {
        $.msg_error("邮箱格式不正确");
        return;
    }
    if (pwd != pwd2) {
        $.msg_error("两次输入密码不一致");
        return;
    }
    if (!cha) {
        $.msg_error("请输入验证码");
        return;
    }
    var data = {
        "email": account,
        "pwd": pwd,
        "captcha": cha
    };
    $.post("/register", data, function (data) {
        $.msg_success("注册成功,请登录");
        $('.a_login').click();
    });
}
function findPwd() {
    var account = $('#findpwd_account').val();
    var pwd = $('#findpwd_password').val();
    var pwd2 = $('#findpwd_password_agin').val();
    var cha = $("#findpwd_capcha").val();
    if (!emailCheck(account)) {
        $.msg_error("邮箱格式不正确");
        return;
    }
    if (pwd != pwd2) {
        $.msg_error("两次输入密码不一致");
        return;
    }
    if (!cha) {
        $.msg_error("请输入验证码");
        return;
    }
    var data = {
        "email": account,
        "pwd": pwd,
        "captcha": cha
    };
    $.post("/find-pwd", data, function (data) {
        $.msg_success("密码重置成功,请重新登录");
        $('.a_login').click();
    });
}
function captchaCountDown(el, defaultStr) {
    curCount = count;
    InterValObj = window.setInterval(function () {
        setCountTime(el, defaultStr);
    }, 1000);
}
function setCountTime(el, defaultStr) {
    if (curCount == 0) {
        window.clearInterval(InterValObj);//停止计时器
        el.removeAttr("disabled");//启用按钮
        el.text(defaultStr);
    }
    else {
        curCount--;
        el.text(curCount + "s");
    }
}
function findPwdGetChpcha() {
    getChpcha($('#findpwd_account').val(), $("#findpwd_get_capcha"));
}
function regGetChpcha() {
    getChpcha($('#reg_account').val(), $("#reg_get_capcha"));
}
function getChpcha(email, el) {
    if (!emailCheck(email)) {
        $.msg_error("邮箱格式不正确");
        return;
    }
    el.attr("disabled", "true");
    $.get('/captcha?account=' + email, function () {
        captchaCountDown(el, "获得验证码");
    },function () {
        el.removeAttr("disabled");//启用按钮
    });
}
function emailCheck(email) {
    var emailPat = /^(.+)@(.+)$/;
    var matchArray = email.match(emailPat);
    return matchArray != null
}