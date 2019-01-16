select 
	${field_code}	as 	domain_value 
	,case when ${field_reference} is not null then ${field_reference} else ${field_alias} end	as domain_text
	,${field_reference}	as reference
from 
	${schema}${table}
order by
	domain_text desc