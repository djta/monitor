select
	nt.authority_id as group_id
from
	os_wfm_node_to t
	inner join os_wfm_node_to nt
	on t.next_node = nt.node_id
where
	t.tuid = ${fld:action_id}