clearForm("formEditor3");

document.formEditor3.tuid.value = "${fld:tuid}";
document.formEditor3.user_id.value = "${fld:user_id}";
document.formEditor3.userlogin.value = "${fld:userlogin@js}";
document.formEditor3.name.value = "${fld:name@js}";
setCheckboxValue("sex","${fld:sex}","formEditor3");
document.formEditor3.locale.value="${fld:locale@js}";

document.formEditor3.entry_date.value = "${fld:entry_date@yyyy-MM-dd}";
document.formEditor3.contract_from.value = "${fld:contract_from@yyyy-MM-dd}";
document.formEditor3.contract_end.value = "${fld:contract_end@yyyy-MM-dd}";
document.formEditor3.birthday.value = "${fld:birthday}";

document.formEditor3.address.value = "${fld:address@js}";
document.formEditor3.card_no.value = "${fld:card_no@js}";
document.formEditor3.salary.value = "${fld:salary}";
document.formEditor3.remark.value = "${fld:remark@js}";
document.formEditor3.email.value = "${fld:email@js}";
document.formEditor3.mobile.value = "${fld:mobile@js}";
document.formEditor3.ext_num.value = "${fld:ext_num@js}";

document.formEditor3.user_pinyin.value = "${fld:user_pinyin@js}";
ccms.util.setMulitCheckboxValue("_remind_type","${fld:remind_type}","formEditor3");

<skill-list>
	setCheckboxValue("skill_id","${fld:skill_id}","formEditor3");
</skill-list>

<org-post>
setCheckboxValue("org_post_id","${fld:org_post_id}","formEditor3");
</org-post>

$("#passwd_tr").hide();
$("#confirm_tr").hide();
$("#pwd_policy_tr").hide();
document.formEditor3.userlogin.disabled=true;
