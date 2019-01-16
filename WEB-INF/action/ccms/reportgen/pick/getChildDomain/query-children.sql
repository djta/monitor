SELECT
	case ${fld:show_type} when '0' then t.domain_value else t.domain_text_${def:locale} end as domain_text,
	case ${fld:show_value_type} when '0' then t.domain_value else t.domain_text_${def:locale} end as domain_value
FROM
	t_domain t
WHERE
	t.parent_namespace = ${fld:parent_namespace}
and
	t.namespace = ${fld:namespace}
and
	t.parent_domain_value = ${fld:parent_domain_value}
and
	t.is_enabled = '1'
and
	(t.subject_id is null or t.subject_id=${def:subject})
order by 
    t.show_order
