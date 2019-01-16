select 
	org_id,
	org_name 
from 
	hr_org
where 
	tenantry_id = ${def:tenantry}
	
	${filter}
	${orderby}