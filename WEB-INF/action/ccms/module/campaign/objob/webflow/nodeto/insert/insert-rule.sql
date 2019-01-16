INSERT	INTO
    cs_job_node_rule
(
	tuid
	, node_to_id
	, rule_field
	, rule_operator
	, rule_value
	, rule_logic
	, left_prefix
	, right_suffix
	, created
	, createdby
)
select
	${seq:nextval@seq_cs_job_node_rule}
	, ${seq:currval@seq_cs_job_node_to}
	, ${fld:rule_field}
	, ${fld:rule_operator}
	, ${fld:rule_value}
	, ${fld:rule_logic}
	, ${fld:left_prefix}
	, ${fld:right_suffix}
	, {ts '${def:timestamp}'}
	, '${def:user}'
from
	dual
where
	${fld:rule_field} is not null