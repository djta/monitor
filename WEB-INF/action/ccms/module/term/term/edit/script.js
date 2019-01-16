$("#introduction").val("");
document.formEditor.tuid.value = "${fld:tuid}";
document.formEditor.term_name.value = "${fld:term_name}";
document.formEditor.remark.value = "${fld:remark}";
document.formEditor.pre_class.value = "${fld:pre_class}";
document.formEditor.post_class.value = "${fld:post_class}";
document.formEditor.logo_path.value = "${fld:logo_path}";

document.formEditor.introduction.value = "${fld:introduction@js}";
document.formEditor.ending.value = "${fld:ending}";
document.formEditor.question_bank_name.value = "${fld:question_bank_name}";

setCheckboxValue("term_type","${fld:term_type@js}","formEditor");
setCheckboxValue("status","${fld:status@js}","formEditor");
