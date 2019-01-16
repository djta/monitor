select
	c.id
	,c.entry_id
	,w.wfm_name
	,s.node_name as step_name
	,a.action_name
	,f.name as owner
	,c.start_date
	,c.finish_date
	,c.status
	,c.step_id
	,ff.name as caller
	,s.node_type
	--,case when e.state=4 and s.node_type<>'2' then concat(concat('<a href="javascript:void(0)" onclick="javascript:reOpen(',c.id),');" >Reopen</a>') else '' end as reopen	--结单状态,并且不是结束节点
	,null as reopen
from	
	os_historystep c
	left join os_wfm_node s
	on c.step_id = s.tuid
	left join os_wfm_node_to a
	on c.action_id = a.tuid
	left join hr_staff f
	on c.owner = f.userlogin
	left join hr_staff ff
	on c.caller = ff.userlogin
	left join os_wfentry e
	on c.entry_id = e.id
	left join os_wfm w
	on e.wfm_id = w.tuid
where
	c.entry_id = ${fld:entry_id}
	
	${filter}

order by c.start_date