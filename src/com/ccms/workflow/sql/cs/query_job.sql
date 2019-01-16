select 
	t.tuid as job_id
	,t.subject_id
	,t.campaign_id
	,t.task_duplicate_scope
	,t.task_duplicate_flag
	, (select n.tuid from cs_job_node n where n.node_type='0' and n.job_id = t.tuid) as first_node	--start node
    , (select n.ob_type from cs_job_node n where n.node_type='0' and n.job_id = t.tuid) as first_node_ob_type
    , t.job_priority
    , m.cust_code
    , m.pk_value
    , m.bz_pk_value
    , m.bz_type  as  cs_bz_type
    , m.search_sql
    , b.table_code
    , b.bpk_field
from 
	cs_job t 
	inner join cs_campaign c
	on t.campaign_id = c.tuid
	inner join cs_job_model m
	on t.model_id = m.tuid
	inner join t_table b
	on m.search_table_id = b.tuid
where
	t.tuid = ${fld:job_id}
and
	t.is_enabled = '1'	/*任务启用*/
and
	t.parent_id is null /*只取主job*/
and
	c.is_deleted = '0'	/*未删除*/
and
	c.campaign_status = '1'	/*活动启用*/
and
	(t.from_date is null or t.from_date <= {d '${def:date}'})
and
	(t.to_date is null or t.to_date >= {d '${def:date}'})