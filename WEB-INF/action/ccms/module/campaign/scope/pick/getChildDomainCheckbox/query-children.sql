SELECT
	t.domain_text_${def:locale} as domain_text,
	t.domain_value
FROM
	t_domain t
WHERE
	t.parent_namespace = ${fld:parent_namespace}
and
	t.namespace = ${fld:child_namespace}
and
	t.domain_value = ${fld:parent_value}
and
	t.is_enabled = '1'
order by 
	t.show_order
    ,t.domain_text_${def:locale}
