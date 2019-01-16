var objId = document.getElementById("${fld:id}");
if(typeof(objId) != "undefined"){
	objId.options.length = 0;
	option = new Option("请选择");
	option.value = "";
	objId.options.add(option);
	<rows>
		option = new Option("${fld:field_name@js}");
		option.value = "${fld:tuid}";
		objId.options.add(option);
	</rows>
}else{
	ccms.dialog.alert("请确认编号元素(${fld:id})是否存在！");
}