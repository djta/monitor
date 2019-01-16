clearForm("formEditor");

document.formEditor.tuid.value = "${fld:menu_item_id}";
document.formEditor.position.value = "${fld:position}";
document.formEditor.description_cn.value = "${fld:description_cn@js}";
document.formEditor.description_en.value = "${fld:description_en@js}";
document.formEditor.logo_path.value = "${fld:logo_path@js}";
document.getElementById("bb").style.display = "";
document.getElementById("cc").style.display = "";
document.formEditor.service_id.value = "${fld:service_id}";
document.formEditor.service_id_name.value = "${fld:description_cn}";