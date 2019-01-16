update
	os_currentstep
set
	owner = '${def:user}'
where
	id = ${fld:current_id}
and
	not exists(
		select 1 from hr_staff where userlogin=os_currentstep.owner
	)