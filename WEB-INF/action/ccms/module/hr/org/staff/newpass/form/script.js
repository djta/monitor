clearForm("formEditorPwd");
$("#uName").html("${fld:name@js}");
$("#uEmail").html("${fld:email@js}");
document.formEditorPwd.tuid.value = "${fld:user_id}";
document.formEditorPwd.user_id.value = "${fld:user_id}";
document.formEditorPwd.userlogin.value = "${fld:userlogin@js}";