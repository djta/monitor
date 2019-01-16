select
	distinct
	f.userlogin as reciver,
	f.userlogin
from
	hr_staff f
	inner join (${table}) t
	on f.userlogin = t.id