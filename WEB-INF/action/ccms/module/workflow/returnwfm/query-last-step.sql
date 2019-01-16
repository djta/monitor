select
	t.tuid as action_id
	,h.owner
from
	os_currentstep s
	inner join os_currentstep_prev p
	on s.id = p.id
	inner join os_historystep h
	on p.previous_id = h.id
	inner join os_wfm_node_to t
	on s.step_id = t.node_id
	and h.step_id = t.next_node
where
	s.entry_id = ${fld:__wfentry_id__}
and
	s.step_id = ${fld:__current_step_id__}