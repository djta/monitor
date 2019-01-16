update 
	${schema}s_loginlog
set 
	exit_date='${def:timestamp}' 
where 
	jsessionid='${def:session}'
