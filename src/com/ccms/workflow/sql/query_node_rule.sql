SELECT
	f.tuid
	,f.node_to_id
	,t.field_code as rule_field
	,rule_operator
	,rule_value
	,rule_logic
    ,t.field_type
	,left_prefix
	,right_suffix
FROM
	os_wfm_node_rule f
	left join t_field t
	on f.rule_field = t.tuid
WHERE
    f.node_to_id = ${action_id}
order by
	f.tuid