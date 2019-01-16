
document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.subject_id.value = "${fld:subject_id}";
document.formEditor.document_type.value = "${fld:document_type}";
document.formEditor.document_name.value = "${fld:document_name@js}";
document.formEditor.doc_width.value = "${fld:doc_width@js}";
document.formEditor.nav_url.value = "${fld:nav_url@js}";
document.formEditor.nav_url_right.value = "${fld:nav_url_right@js}";
document.formEditor.nav_url_top.value = "${fld:nav_url_top@js}";
document.formEditor.nav_url_bottom.value = "${fld:nav_url_bottom@js}";
document.formEditor.url.value = "${fld:url@js}";
document.formEditor.doc_hight.value = "${fld:doc_hight@js}";
document.formEditor.nav_url_width.value = "${fld:nav_url_width@js}";
document.formEditor.nav_right_width.value = "${fld:nav_right_width@js}";
document.formEditor.nav_url_hight.value = "${fld:nav_url_hight@js}";
document.formEditor.nav_bottom_hight.value = "${fld:nav_bottom_hight@js}";
document.formEditor.remark.value = "${fld:remark@js}";

document.formEditor.table_id.value = "${fld:table_id}";
document.formEditor.table_name.value = "${fld:table_name@js}";
document.formEditor.report_id.value = "${fld:report_id}";
document.formEditor.report_name.value = "${fld:report_name@js}";
document.formEditor.form_id.value = "${fld:form_id}";
document.formEditor.form_name.value = "${fld:form_name@js}";
document.formEditor.template_uri.value = "${fld:template_uri@js}";

setCheckboxValue("action_type","${fld:action_type@js}","formEditor");

if("${fld:action_type@js}"=="0"){
	document.getElementById("formName").style.display='';
	document.getElementById("reportName").style.display='none';
}else{
	document.getElementById("formName").style.display='none';
	document.getElementById("reportName").style.display='';
}


