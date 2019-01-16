and 
	(
		f.userlogin = ${fld:userall}
		or
		f.name like concat('%', ${fld:userall}, '%')
		or
		f.mobile = ${fld:userall}
	)