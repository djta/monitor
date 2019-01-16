select 
	p.tuid    as  id
	, p.remark as description
	, p.group_name as name
from 
	hr_authority_group p
where
	p.tenantry_id = ${def:tenantry}
	
	${filter}
	${orderby}
