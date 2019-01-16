INSERT	INTO
    os_wfm_node_rule
(
	tuid
	, node_to_id
	, rule_field
	, rule_operator
	, rule_value
	, rule_logic
	, left_prefix
	, right_suffix
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	, ${fld:node_to_id}
	, ${fld:rule_field}
	, ${fld:rule_operator}
	, ${fld:rule_value}
	, ${fld:rule_logic}
	, ${fld:left_prefix}
	, ${fld:right_suffix}
)
