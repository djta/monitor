select 
	${field}
from	     

	${table}
where 
case when ${delete_field} is null then '0' else ${delete_field} end = '0'

	${filter}

	${group_by}
    
    ${order_by}
