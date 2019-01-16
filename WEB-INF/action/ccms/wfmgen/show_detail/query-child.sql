select
	c.id as child_entry_id
from	
	os_wfentry c
	inner join os_wfm_node n
	on c.parent_node_id = n.tuid
where
	c.parent_id = ${fld:entry_id}
and
	c.parent_node_id = ${fld:node_id}
and
	n.step_type = '4'