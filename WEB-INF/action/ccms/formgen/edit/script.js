document.formEditor_${fld:form_id}.__pk_value__.value = "${fld:__pk_value__}";
document.formEditor_${fld:form_id}.__p_pk_value__.value = "${fld:__p_pk_value__}";
$("#attachment_btn_${fld:form_id}").attr("code","${fld:__pk_value__}");

${controls}

//判断如果是版本还原的话
if("${fld:snapshot}" != ""){
	$("#formEditor_${fld:form_id} input[name='save_btn']").hide();
	$("#formEditor_${fld:form_id} input[name='wfmCommand']").hide();
	$("#formEditor_${fld:form_id} input[name='delete_btn']").hide();
}