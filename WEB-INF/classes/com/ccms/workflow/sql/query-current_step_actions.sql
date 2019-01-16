select
	nn.node_type
	,nn.step_type
	,nt.tuid as action_id
	,nt.action_name
	,nt.pre_class
	,nt.next_node
from	
	os_wfm_node_to nt
	inner join os_wfm_node nn
	on nt.next_node = nn.tuid
where
	nt.node_id = ${fld:__current_step_id__}