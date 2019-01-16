document.formEditor.tuid.value = "${fld:tenantry_id}";
document.formEditor.tenantry_name.value = "${fld:tenantry_name@js}";
document.formEditor.description.value = "${fld:description@js}";

setCheckboxValue("enabled","${fld:enabled@js}","formEditor");
setCheckboxValue("app_id","${fld:app_id}","formEditor");
<rows>
	if("${fld:is_default@js}"=="1"){
		setCheckboxValue("is_default","${fld:sub_id}","formEditor");
	}
	setCheckboxValue("subject_id","${fld:sub_id}","formEditor");
</rows>