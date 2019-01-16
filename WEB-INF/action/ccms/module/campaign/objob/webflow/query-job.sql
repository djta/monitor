SELECT
	t.tuid as job_id
	,t.job_name
	,t.subject_id
FROM
	cs_job t
WHERE
	t.tuid = ${fld:job_id}
