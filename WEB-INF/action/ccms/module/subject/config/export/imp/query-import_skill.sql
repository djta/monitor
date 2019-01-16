select
	t.*
from 
	t_import_skill t
where 
	t.imp_id = ${fld:imp_id}
