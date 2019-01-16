select 
	concat(s.fname , ' ' , s.lname) as usuario,operation,
	count(operation) as total
from
	${schema}s_auditlog a,
	${schema}s_user s
where
	s.userlogin = a.userlogin
	
	${filter}
group by
	s.fname,
	s.lname,a.operation
