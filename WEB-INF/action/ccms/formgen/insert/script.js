if("${fld:__wfm_id__}"!="" && "${fld:wfentry_id}" != ""){
	$("#formEditor_${fld:form_id} input[name='__wfentry_id__']").val("${fld:wfentry_id}");
	$("#formEditor_${fld:form_id} input[name='__current_step_id__']").val("${fld:current_step_id}");
}

//先给业务主键赋值，新增或编辑时会清空主键值
$("#formEditor_${fld:form_id} input[name=__pk_value__]").val("${fld:bpk_field_value}");