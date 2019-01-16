INSERT	INTO
t_term_outlet_rule
(
	tuid
	, item_id
	, rule_code
	, item_to
	, remark
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	,${fld:item_id}
	,${fld:rule_code}
	,${fld:item_to}
	,${fld:remark}
)