SELECT
	t.grade_code as org_grade
	,t.grade_name as org_grade_alias
FROM
      hr_org_grade t 
where 
	t.tenantry_id = ${def:tenantry}