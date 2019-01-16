select
	a.tuid as action_id
	,a.quartz_timeout
from	
	os_wfm_node_to a
where
	a.node_id = ${step_id}
and
	a.quartz_timeout is not null
and
	a.node_id != a.next_node