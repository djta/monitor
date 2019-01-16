select 
	userlogin,lname,fname,user_id 
from 
	${schema}s_user
where 
	userlogin = ${fld:userlogin}
