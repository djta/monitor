update
	hr_staff 
set 
	name = concat(${fld:lname},${fld:fname}),
	email = ${fld:email},
	mobile = ${fld:mobile}
where
	userlogin = '${def:user}'