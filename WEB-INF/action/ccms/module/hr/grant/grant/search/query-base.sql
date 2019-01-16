select 
	p.tuid
	,p.grant_name
	,h.name as authuser
	,p.start_time
	,p.end_time
	,p.terminate_time
	,case p.status when '0' then '授权中' when '1' then '正常结束' when '2' then '提前终止' end as status
	,case p.status when '0' then '' else 'none' end as is_can_update
from 
	hr_grant p
	inner join hr_staff h
	on p.authuser = h.userlogin
WHERE
	p.userlogin = '${def:user}'
and
	p.is_deleted = '0'

	${filter}
${orderby}
