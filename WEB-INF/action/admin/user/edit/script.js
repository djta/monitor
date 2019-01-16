clearForm("formEditor");
document.formEditor.userlogin.value ="${fld:userlogin@js}";
document.formEditor.lname.value = "${fld:lname@js}";
document.formEditor.fname.value = "${fld:fname@js}";
document.formEditor.email.value = "${fld:email@js}";
document.formEditor.pwd_policy.value = "${fld:pwd_policy@js}";
document.formEditor.locale.value = "${fld:locale@js}";


setCheckboxValue("force_newpass","${fld:force_newpass}","formEditor");
<role-list>
	setCheckboxValue("role_id","${fld:role_id}","formEditor");
</role-list>

document.getElementById("passwdtr").style.display = 'none';
document.getElementById("confirmtr").style.display='none';
document.getElementById("tenantryID").style.display='none';

document.formEditor.tuid.value = "${fld:user_id}";
document.formEditor.user_id.value = "${fld:user_id}";
document.formEditor.userlogin.disabled = true;