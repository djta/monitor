select
	c.entry_id
	,c.step_id
from
	os_currentstep c
where
	c.id = ${fld:current_id}