document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.list_name.value = "${fld:list_name@js}";
document.formEditor.list_code.value = "${fld:list_code@js}";
document.formEditor.list_score.value = "${fld:list_score}";
document.formEditor.list_score_code.value = "${fld:list_score_code@js}";
document.formEditor.namespace.value = "${fld:namespace@js}";
document.formEditor.remark.value = "${fld:remark@js}";
document.formEditor.show_order.value = "${fld:show_order}";

setCheckboxValue("is_unspeak","${fld:is_unspeak@js}","formEditor");
setCheckboxValue("show_type","${fld:show_type@js}","formEditor");
setCheckboxValue("namespace_op","${fld:namespace_op@js}","formEditor");
