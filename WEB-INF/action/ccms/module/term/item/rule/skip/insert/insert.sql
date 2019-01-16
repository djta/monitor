INSERT	INTO
t_term_skip_rule
(
	tuid
	, item_id
	, rule_code
	, remark
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	,${fld:item_id}
	,${fld:rule_code}
	,${fld:remark}
)