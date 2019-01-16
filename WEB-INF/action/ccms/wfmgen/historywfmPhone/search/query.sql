select
	c.entry_id
	,e.wfm_id
	,e.pk_value
	,c.step_id
	,w.wfm_name
	,s.node_name as step_name
	,a.action_name
	,s.document_id
	,c.status
	,e.created
	,wf.name as createdby
	,c.finish_date
	,cf.name as caller
from	
	os_historystep c
	inner join os_wfm_node_to a on c.action_id = a.tuid
	inner join os_wfm_node s on c.step_id = s.tuid
	inner join os_wfentry e on c.entry_id = e.id
	inner join os_wfm w on e.wfm_id = w.tuid
	left join hr_staff wf on e.createdby = wf.userlogin
	left join hr_staff cf on c.caller = cf.userlogin
where
	e.state = 4
and
	c.caller = '${def:user}'
and
	w.tenantry_id = ${def:tenantry}

	${filter}

order by c.finish_date desc