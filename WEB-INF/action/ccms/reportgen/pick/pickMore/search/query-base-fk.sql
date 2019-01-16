select 
	${field_code} as id 
	,case when ${field_reference} is not null and '${def:locale}'='en' then  ${field_reference} else ${field_alias}	end as description
	,'' as reference
from 
	${schema}.${table}
where
	1 = 1

	${filter}
order by
	description desc