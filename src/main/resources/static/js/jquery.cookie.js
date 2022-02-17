function setCookie(key, value, exdays){
	var d = new Date();
	d.setTime(d.getTime()+(exdays*24*60*60*1000));
	var expires = "expires="+d.toGMTString();
	document.cookie = key+"="+value+"; "+expires;
}
function getCookie(key){
	var key = key + "=";
	var ca = document.cookie.split(';');
	for(var i=0; i<ca.length; i++) {
		var c = ca[i].trim();
		if (c.indexOf(key)==0) { return c.substring(key.length,c.length); }
	}
	return "";
}