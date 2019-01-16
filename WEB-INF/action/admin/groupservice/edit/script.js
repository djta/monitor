clearForm("formEditor");
document.formEditor.group_name.value = "${fld:group_name@js}";
document.formEditor.tuid.value = "${fld:group_id}";
setComboValue('app_id','${fld:app_id}','formEditor');
