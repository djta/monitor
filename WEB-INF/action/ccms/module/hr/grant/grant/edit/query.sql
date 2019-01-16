SELECT
	p.tuid
   	,p.grant_name
	,p.authuser
	,h.name as user_name
	,p.remark
	,p.start_time
	,p.end_time
FROM
	hr_grant p
	left join hr_staff h
	on p.authuser = h.userlogin
WHERE
	p.tuid = ${fld:id}