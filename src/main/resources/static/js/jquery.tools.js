$.ajaxx = function (method, url, data, succ, fail) {
    var params = {
        type: method,
        url: url,
        contentType: "application/json;charset=UTF-8",
        dataType: "json",
        data: JSON.stringify(data),
        success: succ,
        error: fail,
        beforeSend: function (req) {

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
