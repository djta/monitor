update hr_staff set 
	name = ${fld:name},
	mobile = ${fld:mobile},
	vc_contact = ${fld:vc_contact},
	sex = ${fld:sex} 
where user_id = ${fld:user_id}
