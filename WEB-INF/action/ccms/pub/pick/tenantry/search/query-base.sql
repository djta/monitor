select 
	p.tenantry_id    as  id
	, p.tenantry_name as description
from 
	t_tenantry p
where
case when p.enabled is null then '1' else p.enabled end = '1'
	
	${filter}
	${orderby}