select 
	hts.tuid
	,hs.userlogin
	,hs.name 
	from hr_team_staff hts 
		inner join hr_staff hs on hts.staff_id=hs.userlogin 
	where team_id=${fld:team_id}	
	${filter}
	${orderby}
