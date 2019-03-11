document.formEditor.issystem.value = "${fld:issystem@js}";
document.formEditor.senduser.value = "${fld:senduser@js}";
document.formEditor.content.value = "${fld:content@js}";
document.formEditor.sendtime.value = "${fld:sendtime@yyyy-MM-dd HH:mm:ss}";
document.formEditor.viewtime.value = "${fld:viewtime@yyyy-MM-dd HH:mm:ss}";
if("${fld:viewtime}"==""){
	$("#formTitle").html("未读消息");
}else{
	$("#formTitle").html("已读消息");
}

//查看时间
var url = "${def:context}${def:actionroot}/updateviewtime?tuid=${fld:tuid}"
ajaxCall(url,{
	method: "get",
   	progress: true,
   	dataType: "script",
	success: function() {
	}
});
