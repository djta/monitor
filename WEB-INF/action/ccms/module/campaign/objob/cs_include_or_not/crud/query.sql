SELECT
	t.search_form_id
FROM
	cs_job t
WHERE
	t.tuid=${fld:job_id}
