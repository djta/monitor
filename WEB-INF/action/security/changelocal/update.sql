UPDATE
	${schema}s_user
SET
	locale = ${fld:locale}
WHERE
	userlogin = '${def:user}'
