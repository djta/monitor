select
	n.tuid as parent_node_id
	,cw.wfm_name
	,cw.tuid as wfm_id
	,cw.table_id
from	
	os_wfm_node_to t
	inner join os_wfm_node n
	on t.next_node = n.tuid
	inner join os_wfm cw
	on n.child_wfm_id = cw.tuid
where
	t.tuid = ${action_id}
	