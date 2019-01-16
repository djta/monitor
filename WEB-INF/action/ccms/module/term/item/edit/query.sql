SELECT
    f.tuid
    ,f.type_id
    ,f.item_name
    ,f.item_code
    ,f.is_matrix
    ,f.is_matrix_transpose
    ,f.is_page_break
    ,f.list_show_type
    ,f.is_list_mline
    ,f.show_order
    ,f.remark
    ,f.tags
    ,s.type_name
FROM
	t_term_item f
	left join t_term_type s on f.type_id = s.tuid
WHERE
	f.tuid=${fld:id}
