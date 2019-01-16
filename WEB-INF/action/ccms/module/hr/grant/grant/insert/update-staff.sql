UPDATE
	hr_staff
SET
	alias = ${fld:authuser}
WHERE
	userlogin = '${def:user}'