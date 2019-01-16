SELECT
	skill_id ,
	skill_name 
FROM
	hr_skill
WHERE
	tenantry_id =${def:tenantry}