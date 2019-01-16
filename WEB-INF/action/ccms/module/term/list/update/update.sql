update
	t_term_list
SET
	list_name = ${fld:list_name}
	,list_code = ${fld:list_code}
	,list_score = ${fld:list_score}
	,show_type = ${fld:show_type}
	,is_unspeak = ${fld:is_unspeak}
	,list_score_code = ${fld:list_score_code}
	,show_order = ${fld:show_order}
	,namespace = ${fld:namespace}
	,remark = ${fld:remark}
	,namespace_op = ${fld:namespace_op}
where
	tuid = ${fld:tuid}
