select
	w.cust_code
	,w.pk_value
	,w.wfm_id
	,w.parent_id
	,w.parent_node_id
	,n.step_type
from	
	os_wfentry w
	left join os_wfm_node n
	on w.parent_node_id = n.tuid
where
	w.id = ${id}
	