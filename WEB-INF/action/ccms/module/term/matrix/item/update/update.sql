UPDATE
	t_term_item_matrix
SET
	item_id     =${fld:item_id}
	,item_name     =${fld:item_name}
	,item_code     =${fld:item_code}
	,show_order     =${fld:show_order}
	,remark	 =${fld:remark}
	,show_type = ${fld:show_type}
WHERE
	tuid	=${fld:tuid}
