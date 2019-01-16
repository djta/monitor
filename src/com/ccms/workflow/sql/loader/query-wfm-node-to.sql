select 
	t.tuid as action_id
	,t.action_name
	,t.next_node as next_step
	,case nw.step_type when '1' then 'split' when '2' then 'join' else 'step' end as step_type
	,t.pre_class
	,t.post_class
	,t.condition_class
	,t.is_auto
	,t.authority_id
	,t.quartz_class
	,t.quartz_delay
	,t.quartz_cron
from	     
	os_wfm_node_to t
	inner join os_wfm_node n
	on t.node_id = n.tuid
	inner join os_wfm_node nw
	on t.next_node = nw.tuid
where 
	t.node_id = ${step_id}
