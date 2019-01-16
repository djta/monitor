SELECT
    f.tuid
    ,f.type_name
    ,f.remark
    ,f.show_order
    ,s.term_name
    ,f.item_num
    ,tags
FROM
	t_term_type f
	left join t_term s on  f.term_id = s.tuid
WHERE
    f.term_id = ${fld:term_id}
    
	${filter}
	${orderby}