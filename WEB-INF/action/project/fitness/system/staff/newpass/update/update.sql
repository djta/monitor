update ${schema}s_user 
set 
	passwd = ${fld:passwd} 
where user_id = ${fld:user_id}
