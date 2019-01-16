clearForm("formEditor");

document.formEditor.tuid.value = "${fld:org_id}";
document.formEditor.org_name.value = "${fld:org_name@js}";
document.formEditor.remark.value = "${fld:remark@js}";
document.formEditor.show_order.value = "${fld:show_order}";
document.formEditor.short_name.value = "${fld:short_name@js}";

setComboValue("org_grade","${fld:org_grade@js}","formEditor");
setCheckboxValue("org_type","${fld:org_type@js}","formEditor");

//document.formEditor.business_hours_begin.value = "${fld:business_hours_begin}";
//document.formEditor.business_hours_end.value = "${fld:business_hours_end}";
$("#upload_image").attr("src","${def:context}/action/ccms/attachment/download?id=${fld:upload_id}&&type=1");
$("#upload_id").val("${fld:upload_id}");