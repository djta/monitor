SELECT
	t.domain_text_${def:locale} as domain_text,
	t.domain_value,
	t.parent_domain_value,
	t.parent_domain_value2
FROM
	t_domain t
WHERE
	t.namespace = 'City'
and
	(t.parent_domain_value = ${fld:province} or ${fld:province} is null)
and
	t.is_enabled = '1'
order by 
    t.show_order
    ,t.domain_text_${def:locale}
