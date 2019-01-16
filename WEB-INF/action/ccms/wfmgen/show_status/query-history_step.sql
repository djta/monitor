select
	distinct 
	step_id
from 
	os_historystep
where
	entry_id = ${fld:entry_id}