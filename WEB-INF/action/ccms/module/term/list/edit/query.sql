select
 	tuid
	,list_name
	,list_code
	,list_score
	,show_type
	,is_unspeak
	,list_score_code
	,namespace
	,show_order
	,remark
	,namespace_op
from
	t_term_list
WHERE
	tuid = ${fld:tuid}