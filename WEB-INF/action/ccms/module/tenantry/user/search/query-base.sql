select
	f.user_id,
	f.userlogin,
	f.name,
	f.email,
	f.mobile
from 
	${schema}s_user s
	inner join hr_staff f on s.user_id = f.user_id
where 
	s.enabled = 1
	${filter}
	${orderby}