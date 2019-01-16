INSERT	INTO
t_term_item_matrix
(
	tuid
	, item_id
	, item_name
	, item_code
	, show_order
	, remark
	, show_type
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	,${fld:item_id}
	,${fld:item_name}
	,${fld:item_code}
	,${fld:show_order}
	,${fld:remark}
	,${fld:show_type}
)
