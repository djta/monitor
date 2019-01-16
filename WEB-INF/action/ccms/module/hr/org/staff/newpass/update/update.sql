update ${schema}s_user 
set 
	passwd = ${fld:passwd1},
	force_newpass = null 
where
	user_id = ${fld:user_id}