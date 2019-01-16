update 
	hr_staff h
set
	alias = null
where	
	alias is not null
and
	not exists(
		select 1 from hr_grant where userlogin=h.userlogin and authuser=h.alias and status='0'
	)