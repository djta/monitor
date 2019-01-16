$(document).ready(function() {
	$("#quickmenu_btn").unbind().on("click",function(){
		ccms.dialog.open({url: contextPath+"/action/project/fitness/home/quickmenu/form", width: 600, height: 400});
	});
	ajaxCall("/action/project/fitness/home/quickmenu/load",{
		method: "get",
		progress: false,
		dataType: "script",
		success: function() {
		}
	});
});
