SELECT
     j.search_table_id
    , j.search_form_id
    , j.table_id	as root_table_id
    , ta.table_name as root_table_name
    , j.form_id		as root_form_id
    , f.form_name as root_form_name
	,j.bz_type
	,j.cust_name 
	,j.cust_code 
	,j.cust_sex 
	,j.data_owner 
	,j.cust_name_lable 
	,j.cust_code_lable 
	,j.cust_sex_lable 
	,j.data_owner_lable 
	,j.phone_number1 
	,j.phone_number2 
	,j.phone_number3 
	,j.phone_number4 
	,j.phone_number5 
	,j.phone_number6 
	,j.number_lable1 
	,j.number_lable2 
	,j.number_lable3 
	,j.number_lable4 
	,j.number_lable5 
	,j.number_lable6 
	,j.sms_number 
	,j.sms_number_lable 
	,j.email 
	,j.email_lable 
	,j.job_priority as priority
	,j.free_time1_from
	,j.free_time1_to
	,j.free_time2_from
	,j.free_time2_to
	,j.free_time3_from
	,j.free_time3_to
	,j.pk_value 
	,j.pk_value_lable 
	,j.bz_pk_value 
	,j.bz_pk_value_lable 
	,j.interested_series
	,j.interested_series_lable
	,j.import_data_batch
	,j.import_data_batch_lable
FROM cs_job j 
	left join t_form f on j.form_id = f.tuid
	left join t_table ta on j.table_id = ta.tuid
WHERE
    j.tuid = ${fld:job_id}
