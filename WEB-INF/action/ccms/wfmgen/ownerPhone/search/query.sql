select
	c.id as current_id
	,c.entry_id
	,c.step_id
	,s.node_name as step_name
	,w.wfm_name
	,s.step_type
	,case when s.step_type='3' or s.step_type='4' then e.pk_value else null end as p_pk_value
	,case when s.step_type='3' or s.step_type='4' then null else e.pk_value end as pk_value
	,case when s.step_type='3' or s.step_type='4' then s.child_wfm_id else e.wfm_id end as wfm_id
	,s.document_id
	,c.status
	,e.created
	,wf.name as createdby
	,e.parent_id as parent_entry_id
	,e.parent_node_id
from	
	os_currentstep c
	inner join os_wfm_node s on c.step_id = s.tuid
	inner join os_wfentry e on c.entry_id = e.id
	inner join os_wfm w on e.wfm_id = w.tuid
	left join hr_staff wf on e.createdby = wf.userlogin
where
	e.state = 1
and
	(
		c.owner = '${def:user}'
		or
		c.owner = (select userlogin from hr_staff where alias='${def:user}')
		or
		c.owner = concat('${def:org}','_org')
		or
		exists(
			select 1 from hr_post_staff p inner join hr_org_post gp on p.org_post_id=gp.tuid 
			where p.userlogin = '${def:user}' and c.owner = concat(gp.tuid,'_post')
		)
		or
		c.owner like '%#${def:user}#%'
	)
and
	w.tenantry_id = ${def:tenantry}
and
	not exists(
		select 1 from dual where 
		(s.step_type='3' or s.step_type='4')
		and
		(select count(1) from os_wfentry where state=1 and parent_id=c.entry_id and parent_node_id=c.step_id) > 0
	)
	
	${filter}

order by c.start_date desc