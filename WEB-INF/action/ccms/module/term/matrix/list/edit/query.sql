SELECT
    f.tuid
    ,f.item_id
    ,f.list_name
    ,f.list_code
    ,f.list_score
    ,f.show_order
    ,f.show_type
    ,f.is_unspeak
    ,f.remark
    ,s.item_name
FROM
	t_term_list f
	left join t_term_item s
	on f.item_id = s.tuid
WHERE
	f.tuid=${fld:id}
