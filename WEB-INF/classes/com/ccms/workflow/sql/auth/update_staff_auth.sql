update 
	hr_staff h
set
	alias = (select max(authuser) from hr_grant where userlogin=h.userlogin and status='0' and start_time <= {ts '${def:timestamp}'})
where
	alias is null
and
	exists(
		select 1 from hr_grant where userlogin=h.userlogin and status='0' and start_time <= {ts '${def:timestamp}'}
	)