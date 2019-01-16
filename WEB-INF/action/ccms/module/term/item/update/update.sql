UPDATE
	t_term_item
SET
	type_id     =${fld:type_id}
	,item_name     =${fld:item_name}
	,item_code     =${fld:item_code}
	,is_matrix     =${fld:is_matrix}
	,is_matrix_transpose     =${fld:is_matrix_transpose}
	,is_page_break     =${fld:is_page_break}
	,list_show_type     =${fld:list_show_type}
	,is_list_mline     =${fld:is_list_mline}
	,show_order     =${fld:show_order}
	,remark	 =${fld:remark}
	,tags	 =${fld:tags}
WHERE
	tuid	=${fld:tuid}
