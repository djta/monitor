SELECT
    t.tuid
    ,t.model_name
    ,t.is_enabled
    ,t.subject_id
    ,t.search_table_id
    ,t.search_form_id
    ,s.subject_name
    ,fb.form_name as search_form_name
    ,tb.table_name as search_table_name
    ,t.search_sql
    ,t.bz_type
    ,t.cust_name
    ,t.cust_code              
    ,t.cust_sex               
    ,t.cust_name_lable        
    ,t.cust_code_lable        
    ,t.cust_sex_lable         
    ,t.phone_number1          
    ,t.phone_number2          
    ,t.phone_number3          
    ,t.phone_number4          
    ,t.phone_number5          
    ,t.phone_number6          
    ,t.number_lable1          
    ,t.number_lable2          
    ,t.number_lable3          
    ,t.number_lable4          
    ,t.number_lable5          
    ,t.number_lable6          
    ,t.data_owner             
    ,t.data_owner_lable       
    ,t.sms_number             
    ,t.sms_number_lable       
    ,t.email                  
    ,t.email_lable            
    ,t.pk_value               
    ,t.pk_value_lable         
    ,t.bz_pk_value            
    ,t.bz_pk_value_lable      
    ,t.interested_series      
    ,t.interested_series_lable
    ,t.import_data_batch
    ,t.import_data_batch_lable
FROM
	cs_job_model t
	left join t_subject s
	on t.subject_id = s.tuid
	left join t_table ta
	on t.table_id = ta.tuid
	left join t_form f
	on t.form_id = f.tuid
	left join t_table tb
	on t.search_table_id = tb.tuid
	left join t_form fb
	on t.search_form_id = fb.tuid
WHERE
	t.tuid=${fld:id}
