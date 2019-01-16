SELECT
	t.domain_text_${def:locale} as domain_text,
	t.domain_value,
	t.parent_domain_value
FROM
	t_domain t
WHERE
	t.parent_namespace = ${fld:parent_namespace}
and
	t.is_enabled = '1'
and 
	t.parent_domain_value = (select parent_domain_value from t_domain where namespace = ${fld:namespace} and domain_value = ${fld:domain_value})
order by 
    t.show_order
    ,t.domain_text_${def:locale}
