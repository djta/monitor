SELECT
    tuid
    , template_name
    , if_manual_push_flag
    , round_type
    , data_switch_flag
    , data_push_flag
    , reserve_accuracy
    , job_priority
    , job_quota
    , campaign_id
    , subject_id
    , task_duplicate_flag 
    , task_duplicate_scope
    , to_date
    , from_date
    , model_id
    
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
       ,bz_type
       ob_category
FROM
	cs_job_template t
WHERE
	t.tuid=${fld:job_id}
