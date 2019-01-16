select
	1
from 
	dual
where 
	(select count(1) from ${schema}s_user
where userlogin = '${def:user}'
and (
	passwd = ${fld:oldPasswd} or passwd = ${fld:oldPasswd_mw}
)
) = 0