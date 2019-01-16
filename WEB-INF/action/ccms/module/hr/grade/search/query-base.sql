select 
	tuid,
	grade_code,
	grade_name,
	created,
	createdby
from 
	hr_org_grade
where 
	tenantry_id = ${def:tenantry}
	
	${filter}
	${orderby}