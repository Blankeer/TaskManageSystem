$.ajaxx = function (method, url, data, succ, fail) {
    var params = {
        type: method,
        url: url,
        contentType: "application/json;charset=UTF-8",
        dataType: "json",
        data: JSON.stringify(data),
        success: succ,
        error: function (data) {
            data_json = data.responseJSON;
            if (data.status == 401) {
                location.href = '/login.html';
            } else if (data.status == 500) {
                $.msg_error('服务器内部错误');
            } else if (data_json.message) {
                $.msg_error(data_json.message);
            } else {
                if (data.status == 400) {
                    $.msg_error('输入错误')
                } else {
                    $.msg_error(data.responseText);
                }
            }
            if (fail) {
                fail(data);
            }
        },
        beforeSend: function (request) {
            var token = $.getToken();
            if (token) {
                request.setRequestHeader("token", token);
            }
        },
        complete: function (req) {

        }
    };
    if (data == null) {
        delete params.data;
    }
    $.ajax(params);
};
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
$.saveToken = function (token) {
    localStorage.setItem("token", token)
};

$.getToken = function () {
    return localStorage.getItem('token');
};
$.clearToken = function () {
    localStorage.removeItem("token");
};

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

