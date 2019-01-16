SELECT
	d.domain_text_${def:locale} as domain_text
	,d.domain_value
FROM
	cs_job_filter t
	left join t_domain d on t.namespace = d.namespace
WHERE
	t.tuid=${fld:id}
and
	d.is_enabled = '1'
and
	d.subject_id = ${def:subject}