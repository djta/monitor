SELECT
    f.tuid
    ,f.item_id
    ,f.item_name
    ,f.item_code
    ,f.show_order
    ,f.remark
    ,f.show_type
    ,s.item_name as item_name1
FROM
	t_term_item_matrix f
	left join t_term_item s
	on  f.item_id = s.tuid
WHERE
	f.tuid=${fld:id}
