select 
	p.tuid
	,a.name as userlogin
	,h.name as authuser
	,p.created
	,p.op_title
	,p.op_action
from 
	hr_grant_log p
	inner join hr_staff h
	on p.authuser = h.userlogin
	inner join hr_staff a
	on p.userlogin = a.userlogin
WHERE
	1 = 1
	${filter}
${orderby}
