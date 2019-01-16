select 
	${field}
from	     

	${table}
where 
	${bpk_field} = ${fld:pk_value}
	
	${filter}

and 
	not exists(select 1 from cs_task_pool p where 
		p.${pool_duplicate_scope_field}=${duplicate_scope_field}
		and p.${pool_unique_field}=${table}.${unique_field}
		)
