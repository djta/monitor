select 
	userlogin,
	login_date,
	login_time,
	remote_addr,
	context,
	exit_date
from 
	${schema}s_user u, ${schema}s_loginlog l
where
	u.user_id = l.user_id
	
	${filter}
	${orderby}


