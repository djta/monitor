clearForm("formEditor");
document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.domain_value.value = "${fld:domain_value@js}";
document.formEditor.domain_text_cn.value = "${fld:domain_text_cn@js}";
document.formEditor.domain_text_en.value = "${fld:domain_text_en@js}";
document.formEditor.namespace.value = "${fld:namespace@js}";
document.formEditor.parent_namespace.value = "${fld:parent_namespace@js}";
document.formEditor.parent_domain_value.value = "${fld:parent_domain_value@js}";
document.formEditor.remark.value = "${fld:remark@js}";
document.formEditor.show_order.value = "${fld:show_order}";

document.formEditor.parent_namespace.value = "${fld:parent_namespace}";
document.formEditor.subject_id.value = "${fld:subject_id}";
document.formEditor.subject_name.value = "${fld:subject_name}";

setCheckboxValue("is_default","${fld:is_default@js}","formEditor");
