INSERT	INTO
cs_job
(
	tuid
	, subject_id
	, campaign_id
	, model_id
	, job_name
	
	, job_quota
	, data_push_flag
	, data_switch_flag
	, round_type
	, job_priority
	
	, from_date
	, to_date
	, is_enabled
	, reserve_accuracy
	, remark
	
	, parent_id
	, result_type
	, call_type
	, quota_status
	, reference_node_id
	, task_duplicate_scope
	, task_duplicate_flag
	, template_id
	
	, created
	, createdby
	, updated
	, updatedby
	, if_manual_push_flag
	
	
	
	,table_id
	,form_id
	,cust_name
	,cust_code
	,cust_sex
	,cust_name_lable
	,cust_code_lable
	,cust_sex_lable
	
	,phone_number1
	,phone_number2
	,phone_number3
	,phone_number4
	,phone_number5
	,number_lable1
	,number_lable2
	,number_lable3
	,number_lable4
	,number_lable5

	,sms_number
	,sms_number_lable
	,email
	,email_lable
	,pk_value
	,pk_value_lable
	,bz_pk_value
	,bz_pk_value_lable
	,search_table_id
	,search_form_id
	-- ,bz_type
	,ob_category
)
VALUES
(
	${seq:nextval@seq_cs_job}
	,${fld:subject_id}
	,${fld:campaign_id}
	,${fld:model_id}
	,${fld:job_name}
	
	,${fld:job_quota}
	,${fld:data_push_flag}
	,${fld:data_switch_flag}
	,${fld:round_type}
	,${fld:job_priority}
	
	,${fld:from_date}
	,${fld:to_date}
	,'0'
	,${fld:reserve_accuracy}
	,${fld:remark}
	
	,${fld:parent_id}
	,${fld:result_type}
	,${fld:call_type}
	,${fld:quota_status}
	,${fld:reference_node_id}
	
	,${fld:task_duplicate_scope}
	,${fld:task_duplicate_flag}
	,${fld:template_id}

	
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, ${fld:if_manual_push_flag}
	
	,${fld:table_id}
	,${fld:form_id}
	,${fld:cust_name}
	,${fld:cust_code}
	,${fld:cust_sex}
	,${fld:cust_name_lable}
	,${fld:cust_code_lable}
	,${fld:cust_sex_lable}

	,${fld:phone_number1}
	,${fld:phone_number2}
	,${fld:phone_number3}
	,${fld:phone_number4}
	,${fld:phone_number5}
	,${fld:number_lable1}
	,${fld:number_lable2}
	,${fld:number_lable3}
	,${fld:number_lable4}
	,${fld:number_lable5}
	
	,${fld:sms_number}
	,${fld:sms_number_lable}
	,${fld:email}
	,${fld:email_lable}
	,${fld:pk_value}
	,${fld:pk_value_lable}
	,${fld:bz_pk_value}
	,${fld:bz_pk_value_lable}
	
	,${fld:search_table_id}
	,${fld:search_form_id}
	-- ,${fld:bz_type1}
	,${fld:ob_category}
	
)