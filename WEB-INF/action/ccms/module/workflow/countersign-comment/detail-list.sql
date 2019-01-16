select 
	hr.name as createdby,
	c.created,
	c.comments
from 
	os_wfentry w
	inner join os_entry_comment c
	on w.id = c.entry_id
	left join hr_staff hr 
	on c.createdby = hr.userlogin
where 
	w.parent_id = ${fld:entry_id}
and
	w.parent_node_id = ${fld:node_id}
order by
	c.created desc