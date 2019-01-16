select 	
	user_id,
	userlogin,
	lname,
	fname,
	email
from
	${schema}s_user
where
	enabled = 0
	
	${filter}
	${orderby}



