select
	a.tuid as action_id
from	
	os_currentstep c
	inner join os_wfm_node_to a
	on a.node_id = c.step_id
where
	c.entry_id = ${entry_id}
and
	a.quartz_timeout is not null
and
	a.node_id != a.next_node