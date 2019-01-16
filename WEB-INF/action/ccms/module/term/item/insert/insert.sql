INSERT	INTO
t_term_item
(
	tuid
	, type_id
	, item_name
	, item_code
	, is_matrix
	, is_matrix_transpose
	, is_page_break
	, list_show_type
	, is_list_mline
	, show_order
	, remark
	,tags
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	,${fld:type_id}
	,${fld:item_name}
	,${fld:item_code}
	,${fld:is_matrix}
	,${fld:is_matrix_transpose}
	,${fld:is_page_break}
	,${fld:list_show_type}
	,${fld:is_list_mline}
	,${fld:show_order}
	,${fld:remark}
	,${fld:tags}
)