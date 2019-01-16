SELECT
	grade_code,
	grade_name,
	0 as sort_order
FROM
	hr_org_grade
WHERE
	tenantry_id =${def:tenantry}
    