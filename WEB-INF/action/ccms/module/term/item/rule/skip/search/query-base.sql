SELECT
    f.tuid
    ,f.item_id
    ,f.rule_code
    ,f.remark
    ,s.item_name
FROM
	t_term_skip_rule f
	left join t_term_item s
	on  f.item_id = s.tuid
WHERE
  s.tuid = ${fld:item_id1}
${filter}
${orderby}
    