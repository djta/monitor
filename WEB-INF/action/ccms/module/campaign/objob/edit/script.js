clearForm("formEditor");

document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.job_name.value = "${fld:job_name@js}";
document.formEditor.job_quota.value = "${fld:job_quota}";
document.formEditor.job_priority.value = "${fld:job_priority}";
document.formEditor.campaign_id.value = "${fld:campaign_id@js}";
document.formEditor.subject_id.value = "${fld:subject_id}";
document.formEditor.template_id.value = "${fld:template_id}";

document.formEditor.from_date.value = "${fld:from_date@yyyy-MM-dd}";
document.formEditor.to_date.value = "${fld:to_date@yyyy-MM-dd}";
document.formEditor.reserve_accuracy.value = "${fld:reserve_accuracy}";
document.formEditor.remark.value = "${fld:remark@js}";

document.formEditor.reference_node_id.value = "${fld:reference_node_id}";
document.formEditor.reference_node_name.value = "${fld:reference_node_name@js}";

document.formEditor.parent_id.value = "${fld:parent_id}";
document.formEditor.parent_name.value = "${fld:parent_name@js}";
setComboValue("model_id","${fld:model_id}","formEditor");
setComboValue("result_type","${fld:result_type@js}","formEditor");
setComboValue("call_type","${fld:call_type@js}","formEditor");
setComboValue("quota_status","${fld:quota_status@js}","formEditor");

setComboValue("load_template","${fld:template_id}","formEditor");
setCheckboxValue("task_duplicate_scope","${fld:task_duplicate_scope@js}","formEditor");
setCheckboxValue("task_duplicate_flag","${fld:task_duplicate_flag@js}","formEditor");
setComboValue("data_push_flag","${fld:data_push_flag@js}","formEditor");
setComboValue("data_switch_flag","${fld:data_switch_flag@js}","formEditor");
setComboValue("round_type","${fld:round_type@js}","formEditor");

setCheckboxValue("if_manual_push_flag","${fld:if_manual_push_flag@js}","formEditor");
