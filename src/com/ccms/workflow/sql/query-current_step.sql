select
	e.wfm_id
	,e.name as wfm_name
	,s.entry_id
	,e.pk_value
	,s.step_id as current_node_id
	,case when h.node_help is not null then '' else 'none' end as is_node_help
from	
	os_currentstep s
	inner join os_wfm_node n
	on s.step_id = n.tuid
	inner join os_wfentry e
	on s.entry_id = e.id
	left join os_wfm_help h
	on h.node_id = n.tuid
where
	e.id = ${fld:__wfentry_id__}
and
	s.step_id = ${fld:__current_step_id__}