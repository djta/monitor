select 
	t.tuid
	,tab_name as tab_name
from 
	t_import_table t
where 
	imp_id = ${fld:imp_id}
	${filter}