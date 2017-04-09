$.get = function (url, success, fail) {
    $.ajax({
        type: "get",
        url: url,
        contentType: "application/json;charset=UTF-8",
        dataType: "json",
        success: success,
        error: fail
    });
};
$.delete = function (url, success, fail) {
    $.ajax({
        type: "delete",
        url: url,
        contentType: "application/json;charset=UTF-8",
        dataType: "json",
        success: success,
        error: fail
    });
};
$.post = function (url, data, success, fail) {
    $.ajax({
        type: "post",
        url: url,
        contentType: "application/json;charset=UTF-8",
        dataType: "json",
        data: JSON.stringify(data),
        success: success,
        error: fail
    });
};
$.put=function (url, data, success, fail) {
    $.ajax({
        type: "put",
        url: url,
        contentType: "application/json;charset=UTF-8",
        dataType: "json",
        data: JSON.stringify(data),
        success: success,
        error: fail
    });
};
$.message=function (msg) {
    $('#alert_message').text(msg);
};
