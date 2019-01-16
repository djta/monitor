clearForm("formEditor1");
$("#uName").val("${fld:name@js}");
$("#uUserlogin").val("${fld:userlogin}");
document.formEditor1.tuid.value = "${fld:user_id}";
document.formEditor1.user_id.value = "${fld:user_id}";
document.formEditor1.userlogin.value = "${fld:userlogin@js}";