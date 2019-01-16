ccms.util.clearForm("formEditor");

$("#sms_number").val("${fld:sms_number}");
$("#sms_number_lable").val("${fld:sms_number_lable}");
$("#subject_id").val("${fld:subject_id}");
$("#subject_name").val("${fld:subject_name@js}");

document.formEditor.schema_code.value = "${fld:schema_name}";
document.formEditor.table_code.value = "${fld:table_code}";
document.formEditor.table_id.value = "${fld:table_id}";
$("#table_name").val("${fld:table_name@js}");

document.formEditor.form_id.value = "${fld:root_form_id}";
document.formEditor.form_access_type.value = "${fld:access_type}";
document.formEditor.form_name.value = "${fld:root_form_name}";

document.formEditor.job_id.value = "${fld:job_id}";
$("#job_alias").val("${fld:job_name@js}");

document.formEditor.first_node.value = "${fld:first_node}";
$("#first_node_alias").val("${fld:first_node_name@js}");

document.formEditor.first_node_ob_type.value = "${fld:first_node_ob_type}";
$("#first_node_ob_type_alias").val("${fld:first_node_ob_type_name@js}");

document.formEditor.priority.value = "${fld:priority}";

$("#bz_pk_value").val("${fld:bz_pk_value}");
$("#bz_pk_value_lable").val("${fld:bz_pk_value_lable}");

$("#pk_value").val("${fld:pk_value}");
$("#pk_value_lable").val("${fld:pk_value_lable}");

$("#cust_code").val("${fld:cust_code}");
$("#cust_code_lable").val("${fld:cust_code_lable}");

$("#cust_name").val("${fld:cust_name@js}");
$("#cust_name_lable").val("${fld:cust_name_lable@js}");

$("#cust_sex").val("${fld:cust_sex}");
$("#cust_sex_lable").val("${fld:cust_sex_lable}");

$("#phone_number1").val("${fld:phone_number1}");
$("#number_lable1").val("${fld:number_lable1}");

$("#phone_number2").val("${fld:phone_number2}");
$("#number_lable2").val("${fld:number_lable2}");

$("#phone_number3").val("${fld:phone_number3}");
$("#number_lable3").val("${fld:number_lable3}");

$("#phone_number4").val("${fld:phone_number4}");
$("#number_lable4").val("${fld:number_lable4}");

$("#phone_number5").val("${fld:phone_number5}");
$("#number_lable5").val("${fld:number_lable5}");

$("#phone_number6").val("${fld:phone_number6}");
$("#number_lable6").val("${fld:number_lable6}");

$("#sms_number").val("${fld:sms_number}");
$("#sms_number_lable").val("${fld:sms_number_lable}");

$("#email").val("${fld:email}");
$("#email_lable").val("${fld:email_lable}");

$("#interested_series").val("${fld:interested_series}");
$("#interested_series_lable").val("${fld:interested_series_lable}");

$("#import_data_batch").val("${fld:import_data_batch}");
$("#import_data_batch_lable").val("${fld:import_data_batch_lable}");

$("#data_owner").val("${fld:data_owner}");
$("#data_owner_lable").val("${fld:data_owner_lable}");

$("#free_time1_from").val("${fld:free_time1_from}");
$("#free_time1_to").val("${fld:free_time1_to}");

$("#free_time2_from").val("${fld:free_time2_from}");
$("#free_time2_to").val("${fld:free_time2_to}");

$("#free_time3_from").val("${fld:free_time3_from}");
$("#free_time3_to").val("${fld:free_time3_to}");

$("#modalAddnew").modal("show");