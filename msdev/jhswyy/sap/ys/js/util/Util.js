function getMondayAndSunday(date, status) {
    var week = date.getDay(); //获取时间的星期数
    var minus = 0;
    if (status) {
        minus = week ? week - 1 : 6;
        minus = 0 - minus;
    } else {
        minus = week ? 7 - week : week;
    }
    date.setDate(date.getDate() + minus); //获取minus天前的日期
    return date.getFullYear() + "-" + ((date.getMonth() + 1) < 10 ? ("0" + (date.getMonth() + 1)) : (date.getMonth() + 1)) + "-" + (date.getDate() < 10 ? ("0" + date.getDate()) : date.getDate());
}

function createBrowser(itemName, name, _callback, browserUrl) {
    jQuery("#" + itemName).e8Browser({
        name: name,
        viewType: "0",
        browserValue: "0",
        isMustInput: "1",
        browserSpanValue: "",
        hasInput: true,
        isSingle: true,
        completeUrl: "/data.jsp",
        browserUrl: "/systeminfo/BrowserMain.jsp?url=/formmode/browser/CommonSingleBrowser.jsp?customid=36",
        width: "150px",
        hasAdd: false,
        isSingle: true,
        _callbackParams: "",
        _callback: _callback//调用回调方法
    });
}

function createUserBrowser(itemName, name, _callback) {
    createBrowser(itemName, name, _callback, config.user_browserUrl);
}

function createItemBrowser(itemName, name, _callback) {
    createBrowser(itemName, name, _callback, config.item_browserUrl);
}