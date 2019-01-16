UPDATE
	cs_job_model
SET
	subject_id = ${fld:subject_id}
	,search_table_id = ${fld:search_table_id}
	,search_form_id = ${fld:search_form_id}
	,model_name = ${fld:model_name}
	,import_data_batch = ${fld:import_data_batch_id}
	,import_data_batch_lable = ${fld:import_data_batch_name}
	,bz_type = ${fld:bz_type}
	,search_sql = ${fld:search_sql}
	, cust_name =${fld:cust_name_id}
	, cust_code =${fld:cust_code_id}
	, cust_sex =${fld:cust_sex_id}
	, data_owner =${fld:data_owner_id}
	, cust_name_lable =${fld:cust_name_name}
	, cust_code_lable =${fld:cust_code_name}
	, cust_sex_lable =${fld:cust_sex_name}
	, data_owner_lable =${fld:data_owner_name}
	, phone_number1 =${fld:phone_number1_id}
	, phone_number2 =${fld:phone_number2_id}
	, phone_number3 =${fld:phone_number3_id}
	, phone_number4 =${fld:phone_number4_id}
	, phone_number5 =${fld:phone_number5_id}
	, phone_number6 =${fld:phone_number6_id}
	, number_lable1 =${fld:phone_number1_name}
	, number_lable2 =${fld:phone_number2_name}
	, number_lable3 =${fld:phone_number3_name}
	, number_lable4 =${fld:phone_number4_name}
	, number_lable5 =${fld:phone_number5_name}
	, number_lable6 =${fld:phone_number6_name}
	, sms_number =${fld:sms_number_id}
	, sms_number_lable =${fld:sms_number_name}
	, email =${fld:email_id}
	, email_lable =${fld:email_name}
	, pk_value =${fld:pk_value_id}
	, pk_value_lable =${fld:pk_value_name}
	, bz_pk_value =${fld:bz_pk_value_id}
	, bz_pk_value_lable =${fld:bz_pk_value_name}
	, interested_series =${fld:interested_series_id}
	, interested_series_lable =${fld:interested_series_name}
	, updated	={ts '${def:timestamp}'}
	, updatedby	='${def:user}'
WHERE
	tuid = ${fld:tuid}
