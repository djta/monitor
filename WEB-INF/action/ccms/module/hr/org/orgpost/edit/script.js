clearForm("formEditor1");
document.formEditor1.tuid.value = "${fld:tuid}";
document.formEditor1.show_order.value = "${fld:show_order}";
document.formEditor1.org_post_name.value = "${fld:org_post_name@js}";
setComboValue("post_id","${fld:post_id}","formEditor1");
setComboValue("pid","${fld:pid}","formEditor1");