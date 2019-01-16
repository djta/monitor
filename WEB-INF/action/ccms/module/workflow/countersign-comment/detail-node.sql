select 
	n.tuid as node_id
	,n.node_name
	,w.id as entry_id
from 
	os_wfentry w
	inner join os_wfm_node n
	on w.wfm_id = n.wfm_id
where
	w.id = ${fld:entry_id}
and
	n.step_type = '3'
order by 
	n.node_name