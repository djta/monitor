select 
	tenantry_id,
	tenantry_name 
from 
	t_tenantry 
where 
	is_closed ='0' 
and 
	enabled = '1'
