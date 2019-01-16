select
	t.*
from 
	t_import t
where 
	t.tuid = ${fld:imp_id}
