update 
	hr_staff 
set 
	name = ${fld:name},
	email = ${fld:email},
	mobile = ${fld:mobile}
where
	user_id = ${fld:user_id}
