SELECT
	team_id,
	team_name
FROM
	hr_team
WHERE
	tenantry_id =${def:tenantry}