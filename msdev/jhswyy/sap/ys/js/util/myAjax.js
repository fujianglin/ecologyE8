function myAjax(url, type, data) {
    var response={
        code:1,
    };
    $.ajax({
        url: url,
        type: type,
        async: false,
        data: data,
        dataType: "text",
        success: function (msg) {
            response=JSON.parse(msg);
        },
        fail: function (msg) {
            return msg;
        }
    });
    return response;
}