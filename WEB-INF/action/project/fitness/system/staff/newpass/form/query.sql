select	
	user_id
	,userlogin
	,name
	,email
from hr_staff
where user_id = ${fld:user_id}
