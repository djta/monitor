clearForm("formEditor");

document.formEditor.icon_path.value='${fld:icon_path@js}';
document.formEditor.orden.value='${fld:orden}';
document.formEditor.tuid.value='${fld:panel_id}';
 
setComboValue("service_id","${fld:service_id}","formEditor");