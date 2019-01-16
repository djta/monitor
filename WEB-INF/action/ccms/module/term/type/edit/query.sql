select
 	t.tuid
	,t.type_name
	,t.remark
	,t.show_order
	,item_num
	,tags
from
	t_term_type t
WHERE
	t.tuid = ${fld:tuid}