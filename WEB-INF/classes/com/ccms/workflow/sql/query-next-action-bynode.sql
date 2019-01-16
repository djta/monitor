select
	a.tuid as action_id
	,a.quartz_timeout
from	
	os_wfm_node_to a
where
	a.node_id = ${node_id}