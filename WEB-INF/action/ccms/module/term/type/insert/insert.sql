INSERT	INTO
	t_term_type
(
	tuid
	,term_id
	,type_name
	,remark
	,show_order
	,item_num
	,tags
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	,${fld:term_id}
	,${fld:type_name}
	,${fld:remark} 
	,${fld:show_order}
	,${fld:item_num}
	,${fld:tags}
)