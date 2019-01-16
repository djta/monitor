select 
	${field_code} as id 
	,case when ${field_reference} is not null and '${def:locale}'='en' then  ${field_reference} else ${field_alias}	end as description
	,'' as reference
from 
	${schema}.${table}
where
	${result_field} 
	= 
	(case when ${result}=1 then (select def_subject_id from hr_staff where userlogin='${def:user}') else 0 end)

	${filter}
order by
	description desc