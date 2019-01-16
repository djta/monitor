SELECT
	t.tuid
	,t.item_id
	,t.rule_code
	,t.remark
FROM
	t_term_skip_rule t
WHERE
	t.item_id = ${item_id}