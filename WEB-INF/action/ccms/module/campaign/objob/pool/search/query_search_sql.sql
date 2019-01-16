SELECT
	t.search_sql
	,t.job_quota
FROM
	cs_job t 
WHERE
    t.tuid = ${fld:job_id}
