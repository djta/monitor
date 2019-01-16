SELECT
    t.tuid
    , t.template_name    
    , m.model_name
    , case when t.if_manual_push_flag='0' then '否'
		when t.if_manual_push_flag='1' then '是'
	end as if_manual_push_flag
    , case when t.round_type='1' then '售前第一轮' 
		when t.round_type='2' then '售前第二轮' 
		when t.round_type='3' then '售前第三轮' 
		when t.round_type='4' then '售前第四轮' 
		when t.round_type='11' then '车主第一轮'
		when t.round_type='12' then '车主第二轮'  
		when t.round_type='13' then '车主第三轮'  
		when t.round_type='14' then '车主第四轮'  
		when t.round_type='20' then '投诉回访'  
	end as round_type
    , case when t.data_switch_flag='0' then 'OnGoing'
		when t.data_switch_flag='1' then '跟进提醒'
		else '不切换'
	end as data_switch_flag
    , case when t.data_push_flag ='0' then 'OnGoing'
		 when t.data_push_flag ='1' then '跟进提醒'
		 else '不推送'
	end as data_push_flag
    , t.reserve_accuracy
    , t.job_priority
    , t.job_quota
    , t.campaign_id
    , t.subject_id
    , case when t.task_duplicate_flag='0' then '客户'
		when t.task_duplicate_flag='1' then 'CASE(Leads)'
		when t.task_duplicate_flag='2' then 'CASE识别(Leads+更新时间)'  
	end as task_duplicate_flag 
    , case when t.task_duplicate_scope='0' then '活动'
		when t.task_duplicate_scope='1' then '主题' 
	end as task_duplicate_scope 
    , case when t.is_enabled = '0' then '启用'
    		when t.is_enabled = '1' then '停止'
      end as is_enabled                                           
    , case when t.is_enabled = '0' then CONCAT('<a href="javascript:void(0);" onclick="javascript:enabled(' , t.tuid , ',1);" title="停止">停止</a>')
    		when t.is_enabled = '1' then CONCAT('<a href="javascript:void(0);" onclick="javascript:enabled(' , t.tuid , ',0);" title="启用">启用</a>')
      end as url_enabled
  
FROM
	cs_job_template t
	left join cs_job_model m on t.model_id=m.tuid
WHERE
   1=1
    
${filter}
${orderby}
