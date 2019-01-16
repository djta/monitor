select
	distinct
	f.email as reciver,
	f.userlogin
from
	hr_staff f
	inner join (${table}) t
	on f.userlogin = t.id
where
	f.email is not null