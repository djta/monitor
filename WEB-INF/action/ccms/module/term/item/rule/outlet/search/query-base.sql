SELECT
    f.tuid
    ,f.rule_code
    ,f.item_to
    ,f.remark
    ,s.item_name
FROM
	t_term_outlet_rule f
	left join t_term_item s
	on  f.item_id = s.tuid
WHERE
 item_id = ${fld:item_id1}
${filter}
${orderby}
