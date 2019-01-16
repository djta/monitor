select 
	subject_id as sub_id,
	is_default
from 
	t_subject_tenantry
where 
	tenantry_id = ${fld:id}
