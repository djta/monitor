select
	p.owner as alias
from
	os_currentstep p
	inner join hr_staff h
	on p.owner = h.userlogin
where
	p.entry_id = ${id}
and
	p.step_id = ${step_id}
and
	h.alias = '${caller}'