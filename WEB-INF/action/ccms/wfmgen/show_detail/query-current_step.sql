select
	a.action_name
	,f.name as owner
	,c.start_date
	,c.status
from	
	os_currentstep c
	left join hr_staff f
	on c.owner = f.userlogin
	left join os_currentstep_prev p
	on c.id = p.id
	left join os_historystep h
	on p.previous_id = h.id
	left join os_wfm_node_to a
	on h.action_id = a.tuid
where
	c.entry_id = ${fld:entry_id}
and
	c.step_id = ${fld:node_id}

union

select
	a.action_name
	,f.name as owner
	,c.start_date
	,c.status
from	
	os_currentstep c
	left join hr_staff f
	on c.owner = f.userlogin
	left join os_currentstep_prev p
	on c.id = p.id
	left join os_historystep h
	on p.previous_id = h.id
	left join os_wfm_node_to a
	on h.action_id = a.tuid
	inner join os_wfentry e
	on c.entry_id = e.id
where
	e.parent_id = ${fld:entry_id}
and
	e.parent_node_id = ${fld:node_id}

order by start_date desc