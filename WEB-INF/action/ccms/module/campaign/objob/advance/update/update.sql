UPDATE
	cs_job_template
SET
	template_name = ${fld:template_name}
	, if_manual_push_flag = ${fld:if_manual_push_flag}
	, round_type = ${fld:round_type}
	, data_switch_flag = ${fld:data_switch_flag}
	, data_push_flag = ${fld:data_push_flag}
	, reserve_accuracy = ${fld:reserve_accuracy}
	, job_priority = ${fld:job_priority}
	, job_quota = ${fld:job_quota}
	, campaign_id = ${fld:campaign_id}
	, task_duplicate_flag = ${fld:task_duplicate_flag}
	, task_duplicate_scope = ${fld:task_duplicate_scope}
	, to_date =${fld:to_date}
	, from_date =${fld:from_date}
    , model_id = ${fld:model_id}
	, updated ={ts '${def:timestamp}'}
	, updatedby	='${def:user}'
WHERE
	tuid=${fld:tuid}
