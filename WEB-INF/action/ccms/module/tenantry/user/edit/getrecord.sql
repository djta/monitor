select
	f.user_id,
	f.userlogin,
	f.name,
	f.email,
	f.mobile,
	sex
from hr_staff f
where user_id = ${fld:id}
