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
})

$(document).ready(function () {
    $(".user ").mouseover(function () {
        $(".user-popup ").show();
    });
    $(".user ").mouseout(function () {
        $(".user-popup ").hide();
    });
})

function showLoading(){
    $("body").append("<div class=\"spinner-border\" role=\"status\" style=\"position: absolute;top: calc(40%);left: calc(50%);\">\n" +
        "    <span class=\"visually-hidden\">Loading...</span>\n" +
        "</div>\n" +
        "<div class=\"modal-backdrop fade\" id='fullEnable'></div>");
}
function hideLoading(){
    $(".spinner-border:eq(0)").remove();
    $("#fullEnable").remove();
}