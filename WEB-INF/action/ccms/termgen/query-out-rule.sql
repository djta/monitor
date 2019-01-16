SELECT
	t.tuid
	,t.item_id
	,t.rule_code
	,t.remark
	,t.item_to as to_code
FROM
	t_term_outlet_rule t
WHERE
	t.item_id = ${item_id}