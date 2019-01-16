and exists (
	select 1 from hr_staff_skill fk
	inner join hr_skill k on fk.skill_id = k.skill_id
	WHERE fk.user_id = f.user_id 
	and k.skill_scope = ${fld:_staff_category}
)
