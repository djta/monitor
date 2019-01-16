SELECT
	d.domain_value
	,d.domain_text_${def:locale} as domain_text
FROM
	t_tenantry_domain d
WHERE
	d.namespace = '${domain_namespace}'
and
	d.tenantry_id = ${def:tenantry}
order by 
	d.show_order,d.domain_text_${def:locale}