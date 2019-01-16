select
	id
from	
	os_wfentry
where
	parent_id = ${parent_id}
and
	parent_node_id = ${parent_node_id}
and
	state = 1