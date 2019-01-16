UPDATE
	t_term_list
SET
	item_id     =${fld:item_id}
	,list_name     =${fld:list_name}
	,list_code     =${fld:list_code}
	,list_score     =${fld:list_score}
	,show_type = ${fld:show_type}
	,show_order     =${fld:show_order}
	,is_unspeak     =${fld:is_unspeak}
	,remark	 =${fld:remark}
WHERE
	tuid	=${fld:tuid}
