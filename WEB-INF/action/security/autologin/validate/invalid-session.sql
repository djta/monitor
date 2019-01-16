select 
	a.userlogin 
from 
	${schema}s_session a,
	hr_staff b
where 
	a.userlogin = ${fld:userlogin}
	and
	a.context_alias = '${def:alias}'
	and
	a.userlogin=b.userlogin
	and
	b.cti_is_needed='1'
