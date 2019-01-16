clearForm("formEditor");

//document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.user_id.value = "${fld:user_id}";
document.formEditor.userlogin.value = "${fld:userlogin@js}";
document.formEditor.name.value = "${fld:name@js}";
setCheckboxValue("sex","${fld:sex}","formEditor");

document.formEditor.mobile.value = "${fld:mobile@js}";
document.formEditor.vc_contact.value = "${fld:vc_contact@js}";
document.formEditor.skill_id.value = "${fld:skill_id}";

$("#passwd_tr").hide();
$("#confirm_tr").hide();
document.formEditor.userlogin.disabled = true;
