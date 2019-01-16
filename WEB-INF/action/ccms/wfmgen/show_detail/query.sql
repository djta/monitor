select
	a.action_name
	,f.name as owner
	,c.start_date
	,c.finish_date
	,ff.name as caller
	,ec.comments
from	
	os_historystep c
	left join os_wfm_node_to a
	on c.action_id = a.tuid
	left join hr_staff f
	on c.owner = f.userlogin
	left join hr_staff ff
	on c.caller = ff.userlogin
	left join os_entry_comment ec 
	on ec.entry_id=c.entry_id  
	and ec.node_id=c.step_id
where
	c.entry_id = ${fld:entry_id}
and
	c.step_id = ${fld:node_id}

union

select
	a.action_name
	,f.name as owner
	,c.start_date
	,c.finish_date
	,ff.name as caller
	,ec.comments
from	
	os_historystep c
	inner join os_wfm_node cn
	on c.step_id = cn.tuid and cn.node_type <> '2'
	left join os_wfm_node_to a
	on c.action_id = a.tuid
	left join hr_staff f
	on c.owner = f.userlogin
	left join hr_staff ff
	on c.caller = ff.userlogin
	inner join os_wfentry e
	on c.entry_id = e.id
	left join os_entry_comment ec 
	on ec.entry_id=c.entry_id
	and ec.node_id=c.step_id
where
	e.parent_id = ${fld:entry_id}
and
	e.parent_node_id = ${fld:node_id}

order by finish_date desc