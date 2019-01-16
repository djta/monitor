SELECT
	t.search_sql
	,t.job_quota
FROM
	cs_job t
	left join cs_job_model m on t.model_id = m.tuid
WHERE
    t.tuid = ${fld:job_id}
