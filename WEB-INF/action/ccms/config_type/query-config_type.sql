SELECT
	t.domain_text_${def:locale} as domain_text
	,t.domain_value
	,'0' as pid
	,t.show_order
	,concat('p',t.domain_value) as id
	,t.is_default
FROM
	t_domain t
WHERE
	t.subject_id = ${fld:search_subject_id}
and
	t.namespace = 'ConfigMainType'
and
	t.is_enabled = '1'
union

SELECT
	t.domain_text_${def:locale} as domain_text
	,t.domain_value
	,concat('p',t.parent_domain_value) as pid
	,t.show_order
	,concat('a',t.domain_value) as id
	,t.is_default
FROM
	t_domain t
WHERE
	t.subject_id = ${fld:search_subject_id}
and
	t.namespace = 'ConfigType'
and
	t.is_enabled = '1'

order by
	show_order