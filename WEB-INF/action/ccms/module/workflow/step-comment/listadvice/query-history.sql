select 
	hr.name as createdby,
	c.created,
	c.comments
from 
	os_entry_comment c
	left join hr_staff hr 
	on c.createdby = hr.userlogin
where 
	c.entry_id=${fld:entry_id}
order by
	c.created desc