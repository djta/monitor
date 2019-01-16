INSERT	INTO
cs_job_template
(
	tuid
    , template_name
    , model_id
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
    , is_enabled
    , to_date
    , from_date
	, created
	, createdby
	, updated
	, updatedby
)
VALUES
(
	${seq:nextval@seq_cs_job_template}
	,${fld:template_name}
	,${fld:model_id}
	,${fld:if_manual_push_flag}
	,${fld:round_type}
	,${fld:data_switch_flag}
	,${fld:data_push_flag}
	,${fld:reserve_accuracy}
	,${fld:job_priority}
	,${fld:job_quota}
	,${fld:campaign_id}
	,${fld:subject_id}
	,${fld:task_duplicate_flag}
	,${fld:task_duplicate_scope}	
	,'0'	
	,${fld:to_date}
	,${fld:from_date}
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, {ts '${def:timestamp}'}
	, '${def:user}'
)