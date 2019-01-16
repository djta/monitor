clearForm("formEditor1");

document.formEditor1.userlogin.value = "${fld:userlogin@js}";
document.formEditor1.name.value = "${fld:name@js}";
setCheckboxValue("sex","${fld:sex}","formEditor");
document.formEditor1.birthday.value = "${fld:birthday}";
document.formEditor1.address.value = "${fld:address@js}";
document.formEditor1.email.value = "${fld:email@js}";
document.formEditor1.mobile.value = "${fld:mobile@js}";
