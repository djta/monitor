select
	distinct
	f.mobile as reciver,
	f.userlogin
from
	hr_staff f
	inner join (${table}) t
	on f.userlogin = t.id
where
	f.mobile is not null
and
	len(f.mobile) = 11