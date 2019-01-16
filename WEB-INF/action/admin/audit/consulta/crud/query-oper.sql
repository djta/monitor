select
	distinct(operation) as oper
from
	${schema}s_auditlog

order by operation