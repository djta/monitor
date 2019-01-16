clearForm("formEditor");

document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.skill_name.value = "${fld:skill_name@js}";
setCheckboxValue("skill_scope","${fld:skill_scope@js}","formEditor");
document.formEditor.remark.value = "${fld:remark@js}";

setCheckboxValue("is_default","${fld:is_default@js}","formEditor");

<role-list>
setCheckboxValue("role_id","${fld:role_id}","formEditor");
</role-list>