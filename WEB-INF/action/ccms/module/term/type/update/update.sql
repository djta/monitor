update
	t_term_type
SET
	type_name = ${fld:type_name}
	,show_order = ${fld:show_order}
	,remark = ${fld:remark}
	,item_num = ${fld:item_num}
	,tags = ${fld:tags}
where
	tuid = ${fld:tuid}
