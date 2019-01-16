SELECT
	d.domain_value
	,d.domain_text_en as domain_text
	,d.is_default
FROM
	t_domain d
WHERE
    lower(d.namespace) = lower('${fld:domain_namespace}')
and
	d.is_enabled = '1'
and
	(d.subject_id is null or d.subject_id=${subject_id})
order by 
    d.show_order,d.domain_text_en
    