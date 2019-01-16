select
	c.id as current_id
	,c.entry_id
	,c.step_id
	,s.node_name as step_name
	,w.wfm_name
	,s.step_type
	,s.document_id
	,c.status
	,e.created
	,wf.name as createdby
from	
	os_currentstep c
	inner join os_wfm_node s on c.step_id = s.tuid
	inner join os_wfentry e on c.entry_id = e.id
	inner join os_wfm w on e.wfm_id = w.tuid
	left join hr_staff wf on e.createdby = wf.userlogin
where
	w.tenantry_id = ${def:tenantry}
and
	e.state = 2
and
	c.owner = '${def:user}'

	${filter}

order by c.start_date desc