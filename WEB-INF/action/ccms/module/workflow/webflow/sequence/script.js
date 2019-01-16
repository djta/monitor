var objId = document.getElementById("${fld:id}");
if(typeof(objId) != "undefined"&& objId!=null){
	objId.value = "${fld:seq}";
}else{
	ccms.dialog.alert("请确认编号元素(${fld:id})是否存在！");
}