clearForm("formEditor");
setComboValue("app_id","${fld:app_id}","formEditor");
document.formEditor.tuid.value ="${fld:role_id}";
document.formEditor.rolename.value = "${fld:rolename@js}";
document.formEditor.description.value = "${fld:description@js}";
