UPDATE
	cs_job
SET
	campaign_id = ${fld:campaign_id}
	,subject_id = ${fld:subject_id}
	,model_id = ${fld:model_id}
	,job_name = ${fld:job_name}
	,job_quota = ${fld:job_quota}
	,data_push_flag = ${fld:data_push_flag}
	,data_switch_flag = ${fld:data_switch_flag}
	,round_type = ${fld:round_type}
	,job_priority = ${fld:job_priority}
	,from_date = ${fld:from_date}
	,to_date = ${fld:to_date}
	,reserve_accuracy = ${fld:reserve_accuracy}
	,remark	 = ${fld:remark}
	,parent_id	 = ${fld:parent_id}
	,result_type	 = ${fld:result_type}
	,call_type	 = ${fld:call_type}
	,quota_status	 = ${fld:quota_status}
	,reference_node_id	 = ${fld:reference_node_id}
	,task_duplicate_scope = ${fld:task_duplicate_scope}
	,task_duplicate_flag = ${fld:task_duplicate_flag}
	,template_name = ${fld:template_name}
	,template_id = ${fld:template_id}
	, updated	={ts '${def:timestamp}'}
	, updatedby	='${def:user}'
	, if_manual_push_flag = ${fld:if_manual_push_flag}
WHERE
	tuid	=${fld:tuid}
