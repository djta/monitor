select 
	tuid as step_id
	,node_name as step_name
	,node_type as is_start_node
	,old_status
	,status
	,case step_type when '1' then 'split' when '2' then 'join' else 'step' end as step_type
from	     
	os_wfm_node
where 
	wfm_id = ${wfm_id}