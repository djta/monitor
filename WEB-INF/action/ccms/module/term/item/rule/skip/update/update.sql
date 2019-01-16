UPDATE
	t_term_skip_rule
SET
	item_id     =${fld:item_id}
	,rule_code     =${fld:rule_code}
	,remark	 =${fld:remark}
WHERE
	tuid	=${fld:tuid}
