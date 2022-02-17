window.onload = function (){
    var role = document.getElementById("role").innerHTML;
    if(role==="用户"||role==="管理员"||role===""||role==null){
        window.history.back();
    }
}