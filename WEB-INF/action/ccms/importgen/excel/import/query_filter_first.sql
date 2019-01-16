SELECT
	t.tuid
	,t.is_node
	,t.clause_code
	,t.clause_filter
	,t.clause_value
	,t.logic_type
	,t.field_type
	,f.col_name
	,f.is_save_code
FROM
	t_import_rule_filter t
	left join t_import_field f
	on t.field_id = f.tuid
	inner join t_import_rule r
	on r.tuid = t.rule_id
WHERE
	t.rule_id = ${rule_id}
and
	t.parent_id is null