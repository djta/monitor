clearForm("formEditor");

document.formEditor.tuid.value = "${fld:org_id}";
document.formEditor.org_name.value = "${fld:org_name@js}";
document.formEditor.remark.value = "${fld:remark@js}";
document.formEditor.show_order.value = "${fld:show_order}";

/*document.formEditor.org_type.value = "${fld:org_type@js}";
document.formEditor.org_type_name.value = "${fld:org_type_name@js}";*/

document.formEditor.org_code.value = "${fld:org_code@js}";
document.formEditor.contact_phone.value = "${fld:contact_phone@js}";
setSelectValue($("#province"), "${fld:province}");
getSelectDomain("city", "City", "Province", "${fld:province@js}", function(){
	setSelectValue($("#city"), "${fld:city}");
});

document.formEditor.business_hours_begin.value = "${fld:business_hours_begin}";
document.formEditor.business_hours_end.value = "${fld:business_hours_end}";
$("#upload_id").val("${fld:upload_id}");
if( "" != "${fld:upload_id}" ){
	$("#upload_image").attr("src","${def:context}/action/ccms/attachment/download?id=${fld:upload_id}&&type=1");
}
