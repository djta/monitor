select 	
	s.login_date,
	s.login_time,
	s.userlogin,
	s.context_alias,
	s.remote_addr, 
	s.userlogin as u_userlogin,
	concat(concat(u.lname , ' ') , u.fname) as usuario
from 
	${schema}s_user u, ${schema}s_session s
where
	u.userlogin = s.userlogin
	
	${filter}
	${orderby}
