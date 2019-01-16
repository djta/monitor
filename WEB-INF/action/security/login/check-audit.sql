select enabled as is_audit from 
	${schema}s_user u
where  u.userlogin = ${fld:userlogin}
