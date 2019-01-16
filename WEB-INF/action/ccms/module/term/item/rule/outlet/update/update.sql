UPDATE
	t_term_outlet_rule
SET
	item_id     =${fld:item_id}
	,rule_code     =${fld:rule_code}
	,item_to = ${fld:item_to}
	,remark	 =${fld:remark}
WHERE
	tuid	=${fld:tuid}
