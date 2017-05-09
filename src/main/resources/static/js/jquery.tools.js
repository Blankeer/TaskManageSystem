//这个文件主要是一些 tools 工具包,主要包含, AJAX封装,token 处理, 全局 Message 处理
//AJAX 定义
$.ajaxx = function (method, url, data, succ, fail) {
    var params = {
        type: method,//POST or GET ...
        url: url,
        contentType: "application/json;charset=UTF-8",//通用头部,证明这是一个 json 内容的包
        dataType: "json",
        data: JSON.stringify(data),//post or put 请求
        success: succ,
        error: function (data) {// 错误的回调
            if (typeof (fail) != "undefined") {
                fail(data);
                return;
            }
            data_json = data.responseJSON;
            if (data.status == 401) {//401错误都是token 鉴权失败,跳转到 login
                location.href = '/login.html';
            } else if (data.status == 500) {
                $.msg_error('服务器内部错误');
            } else if (typeof (data_json.message) != "undefined") {
                $.msg_error(data_json.message);
            } else {
                if (data.status == 400) {
                    $.msg_error('输入错误')
                } else {
                    $.msg_error(data.responseText);
                }
            }
        },
        beforeSend: function (request) {//ajax 发送之前的回调
            var token = $.getToken();
            if (token) {//把 token 添加到 head
                request.setRequestHeader("token", token);
            }
        },
        complete: function (req) {//ajax 完成之后的回调,没做处理

        }
    };
    if (data == null) {//如果 data为 null, 删掉这个字段
        delete params.data;
    }
    $.ajax(params);//调用 Jquery API 进行 AJAX 请求
};
//下面是get post delete 等方法的定义
$.get = function (url, success, fail) {
    $.ajaxx('get', url, null, success, fail);
};
$.delete = function (url, success, fail) {
    $.ajaxx('delete', url, null, success, fail);
};
$.post = function (url, data, success, fail) {
    $.ajaxx('post', url, data, success, fail);
};
$.put = function (url, data, success, fail) {
    $.ajaxx('put', url, data, success, fail);
};
//保存 token 到 localStorage
$.saveToken = function (token) {
    localStorage.setItem("token", token)
};

//获得 token
$.getToken = function () {
    return localStorage.getItem('token');
};
//清除 token 主要是退出时候调用
$.clearToken = function () {
    localStorage.removeItem("token");
};
// 下面是 Message 的配置
toastr.options = {
    "closeButton": true,
    "debug": false,
    "newestOnTop": false,
    "progressBar": false,
    "positionClass": "toast-top-center",
    "preventDuplicates": false,
    "onclick": null,
    "showDuration": "300",
    "hideDuration": "300",
    "timeOut": "5000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};
$.msg_waring = function (msg) {
    toastr.warning(msg)
};
$.msg_success = function (msg) {
    toastr.success(msg)
};
$.msg_error = function (msg) {
    toastr.error(msg)
};
//格式化 date 的方法,  2017-05-21 12:05
$.formatDate = function (time) {
    if (time == null) {
        return "不知道什么时间";
    }
    var date = new Date(time);
    var Y = date.getFullYear() + '-';
    var M = format0(date.getMonth() + 1) + '-';
    var D = format0(date.getDate()) + ' ';
    var h = format0(date.getHours()) + ':';
    var m = format0(date.getMinutes());
    return Y + M + D + h + m;
};
function format0(num) {
    return num < 10 ? '0' + num : num + '';
}
