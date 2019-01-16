select
	${field} as reciver
from
	hr_staff f
where
	f.userlogin = '${assign_user}'
and
	${field} is not null