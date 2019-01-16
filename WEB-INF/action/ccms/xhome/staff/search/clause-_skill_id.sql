 and exists (
	select 1 from hr_staff_skill fk WHERE fk.user_id = f.user_id 
	and fk.skill_id = ${fld:_skill_id}
)
