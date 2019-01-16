clearForm("formEditor");
document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.old_data.value = "${fld:old_data@js}";
document.formEditor.mapping_text.value = "${fld:mapping_text@js}";
document.formEditor.mapping_value.value = "${fld:mapping_value@js}";
document.formEditor.namespace_mapping.value = "${fld:namespace_mapping@js}";

document.getElementById("divEditor").style.display='';
document.forms["formEditor"].elements["deleteCommand"].style.display='';
