document.formEditor.user_id.value = "${fld:user_id}";
document.formEditor.userlogin.value = "${fld:userlogin}";
document.formEditor.name.value = "${fld:name@js}";
document.formEditor.name_en.value = "${fld:name_en@js}";
document.formEditor.mobile.value="${fld:mobile}";
document.formEditor.vc_contact.value="${fld:contact}";
setSelectValue($("#_skill_ids"), "${fld:skill_id}");
ccms.util.setCheckboxValue('data_limit',"${fld:data_limit}","formEditor");
setSelectValue($("#staff_category"), $("#_skill_ids").find("option:selected").attr("code"));
document.formEditor.remark.value="${fld:remark@js}";

document.formEditor.status.value = "${fld:i_status}";
document.formEditor.email.value = "${fld:email}";
document.formEditor.address.value = "${fld:address}";
ccms.util.setCheckboxValue("sex", "${fld:sex}", "formEditor");
document.formEditor.user_pinyin.value = "${fld:user_pinyin}";
document.formEditor.wx.value = "${fld:wx}";
document.formEditor.leav_time.value = "${fld:updated}";
document.formEditor.card_no.value = "${fld:card_no}";
document.formEditor.school.value = "${fld:school}";
document.formEditor.bankcard.value = "${fld:bankcard}";
document.formEditor.education.value = "${fld:education}";
document.formEditor.otherperson.value = "${fld:otherperson}";
document.formEditor.bankname.value = "${fld:bankname}";
document.formEditor.major.value = "${fld:major}";
document.formEditor.origin.value = "${fld:origin}";
document.formEditor.entry_date.value = "${fld:entry_date}";

if( "" != "${fld:imgid}" ){
	$("#headpic").attr("src", contextPath+"/action/project/fitness/util/attachment/download?tuid=${fld:imgid}&type=1");
}else{
	$("#headpic").attr("src", contextPath+"/js/project/fitness/image/SVG/170X220.svg");
}
if($("#_skill_ids").find("option:selected").attr("code")=="1"){
	$("#isclasstype").show();
}else{
	$("#isclasstype").hide();
}
ccms.util.setCheckboxValue("isclass", "${fld:isclass}", "formEditor");

$("#passwdli").children().hide();
