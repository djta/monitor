clearForm("formEditor1");
$("#uName").html("${fld:lname@js}"+"${fld:fname@js}");
$("#uEmail").html("${fld:email@js}");
document.formEditor1.tuid.value = "${fld:user_id}";
document.formEditor1.user_id.value = "${fld:user_id}";
document.formEditor1.userlogin.value = "${fld:userlogin@js}";