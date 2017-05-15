var InterValObj; //timer变量，控制时间
var count = 60; //间隔函数，1秒执行
var curCount;//当前剩余秒数
$(document).ready(function () {
    //检查 localstorage中token 是否存在, token 是否与某个用户对应,如果 token 校验成功,直接跳转到主页
    var token = $.getToken();
    if (token != null) {//token 存在的时候
        $.get('/token/check', function (data) {
            var url = "/general/index.html";//跳转到管理员或普通用户页面
            if (data.isAdmin) {
                url = "/admin/index.html";
            }
            location.href = url;
        });
    }
    //下面的点击事件都是切换界面的,登录/注册/找回密码,主要是用的 Jq 的 show/hide 方法
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
    //具体点击登录,点击注册,找回密码按钮的逻辑
    $('#bu_login').click(login);
    $('#findpwd_submit').click(findPwd);
    $('#bu_register').click(register);
    $('#findpwd_get_capcha').click(findPwdGetChpcha);
    $('#reg_get_capcha').click(regGetChpcha);
});
//点击登录按钮之后
function login() {
    var account = $('#login_account').val();
    var pwd = $('#login_password').val();
    if (!emailCheck(account)) {
        $.msg_error("邮箱格式不正确");
        return;
    }
    var data = {//把 email 和 pwd, 组成 js 的对象,最终会转换成 json
        "email": account,
        "pwd": pwd
    };
    $.post("/login/", data, function (data) {
        $.saveToken(data.token);// 登录成功,保存 token
        var url = "/general/index.html";//跳转到管理员或普通用户页面
        if (data.isAdmin) {
            url = "/admin/index.html";
        }
        location.href = url;
    });
}
//点击注册按钮的逻辑
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
        $('.a_login').click();//注册成功,切换到登录页面
    });
}
//点击找回密码确认按钮
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
        $('.a_login').click();//找回密码成功,切换到登录页面
    });
}
//倒计时, el 为倒计时按钮, defaultStr 是默认文本
function captchaCountDown(el, defaultStr) {
    curCount = count;
    InterValObj = window.setInterval(function () {// 调用 js 的方法
        setCountTime(el, defaultStr);//每个1秒调用一次这个方法
    }, 1000);
}
//每秒调用一次该方法
function setCountTime(el, defaultStr) {
    if (curCount == 0) {//倒计时到0之后,鼠标变为可点击
        window.clearInterval(InterValObj);//停止计时器
        el.removeAttr("disabled");//启用按钮
        el.text(defaultStr);
    } else {
        curCount--;
        el.text(curCount + "s");//显示 xx 秒
    }
}
//点击找回密码页面的 获取验证码 按钮
function findPwdGetChpcha() {
    getChpcha($('#findpwd_account').val(), $("#findpwd_get_capcha"));
}
//点击注册页面的 获取验证码 按钮
function regGetChpcha() {
    getChpcha($('#reg_account').val(), $("#reg_get_capcha"));
}
//获取验证码的 API,AJAX 调用
function getChpcha(email, el) {
    if (!emailCheck(email)) {
        $.msg_error("邮箱格式不正确");
        return;
    }
    el.attr("disabled", "true");//把按钮变为不可点击
    $.get('/captcha?account=' + email, function () {
        captchaCountDown(el, "获得验证码");//开始倒计时
    }, function () {
        el.removeAttr("disabled");//获取失败启用按钮
    });
}
function emailCheck(email) {
    var emailPat = /^(.+)@(.+)$/;//正则表达式,粗略判断邮箱中有没有@
    var matchArray = email.match(emailPat);
    return matchArray != null
}