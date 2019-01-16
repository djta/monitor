and 
(
	t .team_name like '%'||${fld:daochu_s_name}||'%'
	or
	exists (
		(select 1 from hr_staff
			LEFT JOIN hr_team_staff  ON hr_team_staff.userlogin = hr_staff.userlogin
			where  hr_staff.name  like '%'||${fld:daochu_s_name}||'%') 
		)
)