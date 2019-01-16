clearForm("formEditor");

document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.msg_title.value = "${fld:msg_title@js}";
document.formEditor.limit_date.value = "${fld:limit_date@yyyy-MM-dd}";
document.formEditor.remark.value = "${fld:remark@js}";
document.formEditor.send_flag.value = "${fld:send_flag}";

document.formEditor.msg_content.value = "${fld:msg_content@js}";
setEditorValue("${fld:msg_content@js}");

setCheckboxValue("topic_name","${fld:topic_name}","formEditor");

if("${fld:topic_name}" != ""){	//广播
	selectSendType("1");
	setCheckboxValue("send_type","1","formEditor");
}else{
	selectSendType("0");
	setCheckboxValue("send_type","0","formEditor");
}