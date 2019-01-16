select 
	imp_name
from 
	t_import
where 
	imp_name = ${fld:imp_name}
and
	tuid <> ${fld:tuid}