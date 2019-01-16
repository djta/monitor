select
	rule_field
	, rule_operator
	, rule_value
	, rule_logic
	, left_prefix
	, right_suffix
from
	cs_job_node_rule
where
	node_to_id = ${fld:id}
order by
	tuid desc