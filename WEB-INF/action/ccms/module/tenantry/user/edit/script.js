clearForm("formEditor");
document.formEditor.userlogin.value ="${fld:userlogin@js}";
document.formEditor.name.value = "${fld:name@js}";
document.formEditor.email.value = "${fld:email@js}";
document.formEditor.mobile.value = "${fld:mobile}";

<role-list>
	setCheckboxValue("role_id","${fld:role_id}","formEditor");
	setCheckboxValue("sex","${fld:sex}","formEditor");
</role-list>

document.getElementById("passwdtr").style.display = 'none';
document.getElementById("confirmtr").style.display = 'none';

document.formEditor.tuid.value = "${fld:user_id}";
document.formEditor.user_id.value = "${fld:user_id}";
document.formEditor.userlogin.disabled = true;