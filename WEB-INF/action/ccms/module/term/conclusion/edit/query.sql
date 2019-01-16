select
 	t.tuid
	,t.from_score
	,t.to_score
	,t.show_order
	,t.remark
from
	t_term_conclusion t
WHERE
	t.tuid = ${fld:tuid}