select
	nt.authority_id as group_id
from
	os_wfm_node n
	inner join os_wfm_node_to t
	on n.tuid = t.node_id
	inner join os_wfm_node_to nt
	on t.next_node = nt.node_id
where
	n.tuid = ${fld:next_node}