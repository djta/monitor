and 
	(
		f.userlogin = ${fld:daochu_userall}
		or
		f.name like concat('%', ${fld:daochu_userall}, '%')
		or
		f.mobile = ${fld:daochu_userall}
	)