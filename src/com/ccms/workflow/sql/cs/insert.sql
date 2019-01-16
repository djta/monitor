INSERT	INTO
cs_task_pool
(
	tuid
	, cust_code
	, pk_value
	, bz_pk_value
	, subject_id
	, campaign_id
	, job_id
	, curr_node
	, ob_type
	, priority
	, created
	, updated
	, grab_flag_pass
	, is_done_forever
	, cs_bz_type
)
VALUES
(
	  ${seq:nextval@seq_cs_task_pool_id}
	, ?	/*cust_code*/
	, ?	/*pk_value*/
	, ?	/*bz_pk_value*/
	, ${fld:subject_id}
	, ${fld:campaign_id}
	, ${fld:job_id}
	, ${fld:first_node}
	, ${fld:first_node_ob_type}
	, ${fld:job_priority}
	, {ts '${def:timestamp}'}
	, {ts '${def:timestamp}'}
	, ''
	, '0'
	, ${fld:cs_bz_type}
)
