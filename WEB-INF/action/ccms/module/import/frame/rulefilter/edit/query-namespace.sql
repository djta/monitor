SELECT
	d.domain_text_${def:locale} as domain_text
	,d.domain_value
FROM
	t_import_rule_filter t
	left join t_domain d
	on t.namespace = d.namespace
WHERE
	t.tuid=${fld:id}
and
	d.is_enabled = '1'
