//clearForm("formEditor");
<rows>
setComboValue("model_id","${fld:model_id}","formEditor"); //模型
document.formEditor.from_date.value = "${fld:from_date@yyyy-MM-dd}";//开始
document.formEditor.to_date.value = "${fld:to_date@yyyy-MM-dd}";//结束

setCheckboxValue("task_duplicate_scope","${fld:task_duplicate_scope@js}","formEditor");//除重范围
setCheckboxValue("task_duplicate_flag","${fld:task_duplicate_flag@js}","formEditor");//除重对象
setComboValue("data_push_flag","${fld:data_push_flag@js}","formEditor");//数据推送模式
setComboValue("data_switch_flag","${fld:data_switch_flag@js}","formEditor");//数据切换模式
setComboValue("round_type","${fld:round_type@js}","formEditor");//轮次类型
setCheckboxValue("if_manual_push_flag","${fld:if_manual_push_flag@js}","formEditor");
document.formEditor.reserve_accuracy.value = "${fld:reserve_accuracy}";
document.formEditor.job_quota.value = "${fld:job_quota}";
document.formEditor.job_priority.value = "${fld:job_priority}";
document.formEditor.campaign_id.value = "${fld:campaign_id@js}";
document.formEditor.template_id.value = "${fld:tuid}";
document.formEditor.subject_id.value = "${fld:subject_id}";

document.formEditor.table_id.value = "${fld:table_id}";
document.formEditor.form_id.value = "${fld:form_id}";
document.formEditor.cust_name.value = "${fld:cust_name}";
document.formEditor.cust_code.value = "${fld:cust_code@js}";
document.formEditor.cust_sex.value = "${fld:cust_sex}";
document.formEditor.cust_name_lable.value = "${fld:cust_name_lable}";
document.formEditor.cust_code_lable.value = "${fld:cust_code_lable@js}";
document.formEditor.cust_sex_lable.value = "${fld:cust_sex_lable}";

document.formEditor.phone_number1.value = "${fld:phone_number1}";
document.formEditor.phone_number2.value = "${fld:phone_number2}";
document.formEditor.phone_number3.value = "${fld:phone_number3}";
document.formEditor.phone_number4.value = "${fld:phone_number4}";
document.formEditor.phone_number5.value = "${fld:phone_number5}";
document.formEditor.number_lable1.value = "${fld:number_lable1}";
document.formEditor.number_lable2.value = "${fld:number_lable2}";
document.formEditor.number_lable3.value = "${fld:number_lable3}";
document.formEditor.number_lable4.value = "${fld:number_lable4}";
document.formEditor.number_lable5.value = "${fld:number_lable5}";

document.formEditor.sms_number.value = "${fld:sms_number}";
document.formEditor.sms_number_lable.value = "${fld:sms_number_lable}";
document.formEditor.email.value = "${fld:email}";
document.formEditor.email_lable.value = "${fld:email_lable}";
document.formEditor.pk_value.value = "${fld:pk_value}";
document.formEditor.pk_value_lable.value = "${fld:pk_value_lable}";
document.formEditor.bz_pk_value.value = "${fld:bz_pk_value}";
document.formEditor.bz_pk_value_lable.value = "${fld:bz_pk_value_lable}";

document.formEditor.search_table_id.value = "${fld:search_table_id}";
document.formEditor.search_form_id.value = "${fld:search_form_id}";
document.formEditor.bz_type1.value = "${fld:bz_type}";
document.formEditor.ob_category.value = "${fld:ob_category}";





</rows>