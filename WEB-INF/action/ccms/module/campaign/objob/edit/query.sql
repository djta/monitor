SELECT
    t.tuid
    ,t.job_name
    ,t.job_quota
    ,t.data_push_flag
    ,t.data_switch_flag
    ,t.round_type
    ,t.job_priority
    ,t.from_date
    ,t.to_date
    ,t.is_enabled
    ,t.reserve_accuracy
    ,t.campaign_id
    ,c.campaign_name
    ,c.subject_id
    ,t.remark
    ,t.parent_id
    ,t.result_type
    ,t.call_type
    ,t.quota_status
    ,t.reference_node_id
    ,s.subject_name
    ,tp.job_name as	parent_name
    ,jn.node_name as reference_node_name
    ,t.task_duplicate_scope
    ,t.task_duplicate_flag
    ,t.template_name
    ,t.template_id                  
    ,t.if_manual_push_flag
    ,t.model_id
FROM
	cs_job t
	left join cs_job tp
	on t.parent_id = tp.tuid
	left join cs_job_node jn
	on t.reference_node_id = jn.tuid
	inner join cs_campaign c
	on t.campaign_id = c.tuid
	left join t_subject s
	on c.subject_id = s.tuid
WHERE
	t.tuid=${fld:id}
