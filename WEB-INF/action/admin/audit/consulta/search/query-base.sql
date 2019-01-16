select 
	a.userlogin,
	context_alias,
	operation,area,
	op_date,
	op_time,
	extra_info,
	a.target_table,
	a.pkey,
	a.op_date,
	a.op_time
from
	${schema}s_auditlog a,
	${schema}s_user s
where
	s.userlogin = a.userlogin
	
${filter}
${orderby}

