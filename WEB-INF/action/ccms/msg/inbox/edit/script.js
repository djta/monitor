clearForm("formEditor");

document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.msg_title.value = "${fld:msg_title@js}";
document.formEditor.msg_content.value = "${fld:msg_content@js}";
setEditorValue("${fld:msg_content@js}");
