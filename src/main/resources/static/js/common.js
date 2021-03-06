function toPage(path) {
    window.location.href = path;
}

$(document).ready(function () {
    $(".user ").mouseover(function () {
        $(".user-popup ").show();
    });
    $(".user ").mouseout(function () {
        $(".user-popup ").hide();
    });
});

//为url添加时间戳
function timestamp(url) {
    var getTimestamp = new Date().getTime();
    if (url.indexOf("?") > -1) {
        url = url + "&timestamp=" + getTimestamp
    } else {
        url = url + "?timestamp=" + getTimestamp
    }
    return url;
}

function showLoading(){
    $("body").addClass("no-overflow");
    $("body").append("<div id='loadingDiv' style=\"position: fixed;top: calc(40%);left: calc(47%);z-index: 2000;background-color: #e1e1e1d4;width: 135px;height: 90px;border-radius: 15px;text-align: center;padding: 18px;\"><div class=\"spinner-border\" role=\"status\">\n" +
        "    <span class=\"visually-hidden\">加载中...</span>\n" +
        "</div><br><label>考试云加载中...</label></div>\n" +
        "<div class=\"modal-backdrop fade\" id='fullEnable' style='z-index: 1900'></div>");
}
function hideLoading(){
    $("#loadingDiv").remove();
    $("#fullEnable").remove();
    $("body").removeClass("no-overflow");
}