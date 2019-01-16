select 
	t.org_id
	,t.org_name
	,t.pid
from 
	hr_org t
	left join hr_org_grade h
	on t.org_type = h.tuid
where 	
	t.tenantry_id = ${def:tenantry}
and
	(
		${fld:org_id} is null
		or
		(
			${fld:org_id} is not null
			and
			exists(
				select 1 from hr_org s where s.org_id = ${fld:org_id} and CHARINDEX(s.org_path ,t.org_path)>1
			)
		)
	)
and
	(
		${fld:from_level} is null
		or
		(
			${fld:from_level} is not null
			and
			h.grade_level >= ${fld:from_level}
		)
	)
and
	(
		${fld:to_level} is null
		or
		(
			${fld:to_level} is not null
			and
			h.grade_level <= ${fld:to_level}
		)
	)	