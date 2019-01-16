update 
	${schema}s_user 
set 
	fname = ${fld:name},
	email = ${fld:email}
where
	user_id = ${fld:user_id}
