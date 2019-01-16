and
(
	exists (
		(select 1 from hr_staff
			where t.leader_id::VARCHAR = hr_staff.user_id::VARCHAR 
			and   hr_staff.name  like '%'||${fld:s_name}||'%') 
		)
)

