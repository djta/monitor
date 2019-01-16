SELECT
    t.tuid
    ,t.job_id
    ,t.form_id
    ,t.clause_code
    ,case when t.clause_code='cc_incident.campaign_id' then '活动编号'
		when t.clause_code='cc_incident.incident_type' then '沟通类型'
		when t.clause_code='cc_incident.talk_code' then '沟通结果'
		when t.clause_code='cc_incident.call_code' then '拨打状态'
		when t.clause_code='cc_incident.mb_subject' then '外呼主题'
		when t.clause_code='cc_incident.agent_update_time' then '拨打时间'
		when t.clause_code='cs_task_pool.is_done' then '是否完成活动'
	end as field_name
    ,t.clause_filter
    ,t.clause_value
    ,t.is_node
    ,t.parent_id
    ,t.logic_type
    ,t.namespace
    ,t.field_type
    ,t.phrase_name
    ,t.filter_type
FROM
	cs_job_filter t
WHERE
	t.tuid=${fld:id}
