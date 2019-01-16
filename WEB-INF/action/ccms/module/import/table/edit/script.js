clearForm("formEditor");
document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.table_id.value = "${fld:table_id}";
document.formEditor.table_name.value = "${fld:table_name@js}";
document.formEditor.parent_id.value = "${fld:parent_id}";
document.formEditor.parent_name.value = "${fld:parent_name@js}";
document.formEditor.bpk_field_alias.value = "${fld:bpk_field_alias@js}";
document.formEditor.remark.value = "${fld:remark@js}";
document.formEditor.tab_name.value = "${fld:tab_name@js}";
setCheckboxValue("if_new_flag","${fld:if_new_flag@js}","formEditor");
setCheckboxValue("data_operator_type","${fld:data_operator_type@js}","formEditor");
document.getElementById("imp_id").value="${fld:imp_id}";

