INSERT	INTO
cs_job_model
(
	tuid
	, subject_id
	, search_table_id
	, search_form_id
	, model_name
	, is_enabled
	, import_data_batch
	, import_data_batch_lable
	, bz_type
	, search_sql
	, cust_name
	, cust_code
	, cust_sex
	, data_owner
	, cust_name_lable
	, cust_code_lable
	, cust_sex_lable
	, data_owner_lable
	, phone_number1
	, phone_number2
	, phone_number3
	, phone_number4
	, phone_number5
	, phone_number6
	, number_lable1
	, number_lable2
	, number_lable3
	, number_lable4
	, number_lable5
	, number_lable6
	, sms_number
	, sms_number_lable
	, email
	, email_lable
	, pk_value
	, pk_value_lable
	, bz_pk_value
	, bz_pk_value_lable
	, interested_series
	, interested_series_lable
	, created
	, createdby
	, updated
	, updatedby
)
VALUES
(
	${seq:nextval@seq_cs_job_model}
	,${fld:subject_id}
	,${fld:search_table_id}
	,${fld:search_form_id}
	,${fld:model_name}
	,'0'
	,${fld:import_data_batch_id}
	,${fld:import_data_batch_name}
	,${fld:bz_type}
	,${fld:search_sql}
	, ${fld:cust_name_id}
	, ${fld:cust_code_id}
	, ${fld:cust_sex_id}
	, ${fld:data_owner_id}
	, ${fld:cust_name_name}
	, ${fld:cust_code_name}
	, ${fld:cust_sex_name}
	, ${fld:data_owner_name}
	, ${fld:phone_number1_id}
	, ${fld:phone_number2_id}
	, ${fld:phone_number3_id}
	, ${fld:phone_number4_id}
	, ${fld:phone_number5_id}
	, ${fld:phone_number6_id}
	, ${fld:phone_number1_name}
	, ${fld:phone_number2_name}
	, ${fld:phone_number3_name}
	, ${fld:phone_number4_name}
	, ${fld:phone_number5_name}
	, ${fld:phone_number6_name}
	, ${fld:sms_number_id}
	, ${fld:sms_number_name}
	, ${fld:email_id}
	, ${fld:email_name}
	, ${fld:pk_value_id}
	, ${fld:pk_value_name}
	, ${fld:bz_pk_value_id}
	, ${fld:bz_pk_value_name}	
	, ${fld:interested_series_id}
	, ${fld:interested_series_name}
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, {ts '${def:timestamp}'}
	, '${def:user}'
)