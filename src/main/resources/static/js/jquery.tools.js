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
                location.href='/login.html';
            } else if (data.status == 500) {
                alert('服务器内部错误');
            } else if (data_json.message) {
                alert(data_json.message);
            } else {
                if (data.status == 400) {
                    alert('输入错误')
                } else {
                    alert(data.responseText);
                }
            }
            fail(data);
        },
        beforeSend: function (request) {
            var token = localStorage.getItem('token');
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
$.message = function (msg) {
    $('#alert_message').text(msg);
};
