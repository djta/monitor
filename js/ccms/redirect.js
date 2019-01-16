//定义首界面hash
var $HomeHash = "";
$(document).ready(function() {
	//判断是否是微信登陆 
});

$(window).on("hashchange",function(){
	var hash = window.location.hash;
	//ccms.dialog.notice("hashchange: " + hash);
	var actionroot=$("#home_actionroot").val();
	var url=$("#url").val();
	if(hash == ""){
		loadDivPage("body_content",url);
	}else if (hash.match("#")) {
		var h = hash.split("#")[1];
		if($HomeHash == ""){
			$HomeHash = h;
		}
		if(h.indexOf("a_") < 0){
			loadDivPage("body_content",h,true);
		}
	}
});
$(function(){ $(window).trigger("hashchange");});