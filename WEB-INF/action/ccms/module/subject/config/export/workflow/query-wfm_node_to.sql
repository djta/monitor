select
	d.*
from 
	os_wfm_node t
	inner join os_wfm_node_to d
	on t.tuid = d.node_id
where 
	t.wfm_id = ${fld:wfm_id}
