select 
	${field}
from	     

	${table}
where 
	(case when '${delete_field}'='' then '0' else case when ${delete_field} is null then '0' else ${delete_field} end end ) = '0'

	${filter}

	${access_type}

${orderby}

