select
	f.user_id,
	f.userlogin,
	f.name,
	f.mobile,
	f.vc_contact,
	(	
		select fk.skill_id
	 	from hr_staff_skill fk where fk.user_id = f.user_id limit 1
	 ) as skill_id,
	f.sex 
from hr_staff f 
where f.user_id = ${fld:id}
