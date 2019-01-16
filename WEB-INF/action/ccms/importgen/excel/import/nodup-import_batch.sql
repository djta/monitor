select 
	tuid 
from 
	t_import_history
where 
	import_batch = ${fld:import_batch}
	and imp_id = ${fld:imp_id}