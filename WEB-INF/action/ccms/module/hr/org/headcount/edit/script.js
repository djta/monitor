clearForm("formEditor2");
document.formEditor2.tuid.value = "${fld:hc_id}";
document.formEditor2.hc_name.value = "${fld:hc_name@js}";
document.formEditor2.show_order.value = "${fld:show_order}";
setComboValue("org_post_id","${fld:org_post_id}","formEditor2");
