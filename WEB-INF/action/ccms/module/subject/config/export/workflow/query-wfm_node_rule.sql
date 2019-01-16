select
	f.*
from 
	os_wfm_node t
	inner join os_wfm_node_to d
	on t.tuid = d.node_id
	inner join os_wfm_node_rule f
	on d.tuid = f.node_to_id
where 
	t.wfm_id = ${fld:wfm_id}