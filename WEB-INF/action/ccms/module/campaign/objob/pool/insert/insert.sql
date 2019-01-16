INSERT	INTO
cs_task_pool
(
	tuid
	, subject_id
	, campaign_id
	, job_id
	, curr_node
	, ob_type
	, cust_code
	, pk_value
	, bz_pk_value
	, priority
	, created
	, createdby
	, grab_flag_pass
	, is_done_forever
	, cs_bz_type
	, is_closed
)
VALUES
(
	  ${seq:nextval@seq_cs_task_pool_id}
	, ${fld:subject_id}
	, ${fld:campaign_id}
	, ${fld:job_id}
	, ${fld:first_node}
	, ${fld:first_node_ob_type}
	, ?	/*cust_code*/
	, ?	/*pk_value*/
	, ?	/*bz_pk_value*/
	, ${fld:job_priority}
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, ''
	, '0'
	, ${fld:cs_bz_type}
	, '0'
)
