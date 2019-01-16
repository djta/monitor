update
	${schema}s_user 
set
	locale = ${fld:locale}
	,fname = ${fld:name}
where
	userlogin=${fld:userlogin}