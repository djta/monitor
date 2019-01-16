clearForm("formEditor");
document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.tab_id.value = "${fld:tab_id}";
document.formEditor.field_id.value = "${fld:field_id}";
document.formEditor.field_code.value = "${fld:field_code}";
document.formEditor.col_name.value="${fld:col_name@js}";
document.formEditor.domain_namespace.value="${fld:domain_namespace@js}";
document.formEditor.field_type.value="${fld:field_type@js}";
document.formEditor.field_length.value="${fld:field_length}";
document.formEditor.show_order.value="${fld:show_order}";
document.formEditor.default_value.value="${fld:default_value@js}";
document.formEditor.fk_schema.value="${fld:fk_schema@js}";
document.formEditor.fk_tab.value="${fld:fk_tab@js}";
document.formEditor.fk_fld_code.value="${fld:fk_fld_code@js}";
document.formEditor.fk_fld_name.value="${fld:fk_fld_name@js}";
document.formEditor.remark.value="${fld:remark@js}";
document.formEditor.regex_rule.value="${fld:regex_rule@js}";
document.formEditor.regex_tip.value="${fld:regex_tip@js}";
document.formEditor.type_alias.value="${fld:field_name@js}";

setCheckboxValue("update_mode","${fld:update_mode}","formEditor");
setCheckboxValue("is_mandatory","${fld:is_mandatory}","formEditor");
setCheckboxValue("is_virtual_type","${fld:is_virtual_type}","formEditor");
setCheckboxValue("is_formula","${fld:is_formula}","formEditor");
setCheckboxValue("is_save_code","${fld:is_save_code}","formEditor");
setComboValue("show_align","${fld:show_align}","formEditor");

