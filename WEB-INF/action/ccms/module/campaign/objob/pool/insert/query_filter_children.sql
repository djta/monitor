SELECT
	t.tuid
	,t.is_node
	,t.clause_code
	,t.clause_filter
	,t.clause_value
	,t.logic_type
	,t.field_type
FROM
	cs_job_filter t
WHERE
	t.job_id = ${fld:job_id}
and
	t.parent_id = ${p_id}
and
	t.filter_type = '${filter_type}'