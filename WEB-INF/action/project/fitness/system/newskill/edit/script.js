clearForm("formEditor");
document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.skill_name.value = "${fld:skill_name@js}";
document.formEditor.remark.value = "${fld:remark@js}";
setSelectValue($("#skill_scope"), "${fld:skill_scope}");

setCheckboxRadioValue("data_limit", "${fld:data_limit}");