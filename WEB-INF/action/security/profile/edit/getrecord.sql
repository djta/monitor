select	u.user_id, 
		u.userlogin, 
		u.email, 
		u.lname, 
		u.fname, 
		u.enabled,
		u.force_newpass, 
		u.pwd_policy, 
		u.locale,
		s.mobile
from 
	${schema}s_user u
	left join hr_staff s on s.userlogin = u.userlogin
where 
	u.userlogin = '${def:user}'
