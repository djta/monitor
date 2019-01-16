select 
	${field_code}	as 	id 
	,${field_alias}	as description
	,${field_reference}	as reference
from 
	${schema}${table}
where
	
	(case when '${delete_field}'='' then '0' else case when ${delete_field} is null then '0' else ${delete_field} end end ) = '0'
and
	
(
	${field_alias} like ${fld:name}
or
	${field_reference} like ${fld:name}
)

${orderby}
