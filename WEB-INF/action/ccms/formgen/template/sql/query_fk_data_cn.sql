select 
	${field_code} as domain_value 
	,${field_alias} as domain_text
	,${field_reference} as reference
from 
	${schema}${table}
order by
	domain_text desc