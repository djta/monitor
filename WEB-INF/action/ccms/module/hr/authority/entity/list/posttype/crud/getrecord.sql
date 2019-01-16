SELECT
	post_id ,
	post_name 
FROM
	hr_post t
WHERE
	t.tenantry_id =${def:tenantry}