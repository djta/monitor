SELECT
	t.domain_text_${def:locale} as domain_text,
	t.domain_value
FROM
	t_domain t
WHERE
	t.parent_namespace = ${fld:parent_namespace}
and 
	t.parent_domain_value =  ${fld:parent_domain_value}
and
	t.is_enabled = '1'
and
	t.subject_id=${def:subject}
order by 
	t.show_order
	,t.domain_text_${def:locale}
