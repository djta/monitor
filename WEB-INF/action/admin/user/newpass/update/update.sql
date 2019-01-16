update 
	${schema}s_user 
set 
	passwd = ${fld:passwd_new},
	force_newpass = 1
where
	user_id = ${fld:user_id}