clearForm("formEditor");
document.formEditor.tuid.value = "${fld:menu_id}";
document.formEditor.title_cn.value = "${fld:title_cn@js}";
document.formEditor.title_en.value = "${fld:title_en@js}";
document.formEditor.position.value = "${fld:position}";
document.formEditor.logo_path.value = "${fld:logo_path@js}";

//给角色授权
<role-list>
	setCheckboxValue("role_id","${fld:role_id}","formEditor");
</role-list>

//菜单项
document.formEditor.service_name.value = "${fld:service_name@js}";
document.formEditor.service_id.value = "${fld:service_id}";
//上级菜单项
document.formEditor.p_menu_name.value = "${fld:p_menu_name@js}";
document.formEditor.parentmenu_id.value = "${fld:parentmenu_id}";