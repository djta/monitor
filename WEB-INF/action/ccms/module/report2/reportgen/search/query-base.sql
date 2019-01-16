select
	${field}
from	     

	${table}
where 
	nvl(${delete_field},'0') = '0'

	${filter}

	${group_by}
    
    ${order_by}

limit 10000
