SELECT 
	t.tuid
	,t.entity_id 
	,t.entity_value as grade_code
	,hog.grade_name 
from 
	hr_authority_list t
	left join hr_org_grade hog on hog.grade_code=t.entity_value
where
	entity_id=${fld:entity_id}
