select 
	(select name from hr_staff where userlogin = oec.createdby) as createdby,
	oec.created,
	oec.comments
from 
	os_entry_comment oec
where 
	oec.entry_id=${fld:entry_id}
order by
	oec.created desc