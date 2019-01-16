INSERT	INTO
t_term_list
(
	tuid
	, item_id
	, list_name
	, list_code
	, list_score
	, show_type
	, is_unspeak
	, show_order
	, remark
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	,${fld:item_id}
	,${fld:list_name}
	,${fld:list_code}
	,${fld:list_score}
	,${fld:show_type}
	,${fld:is_unspeak}
	,${fld:show_order}
	,${fld:remark}
)