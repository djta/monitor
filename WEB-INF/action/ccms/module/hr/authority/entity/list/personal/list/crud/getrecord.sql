SELECT
	userlogin as user_id,
	name as userlogin
FROM
	HR_STAFF
WHERE
	org_id = '${fld:org_id}'
AND NOT EXISTS (
	SELECT
		1
	FROM
		hr_authority_list
	WHERE
		hr_authority_list.entity_value = hr_staff.userlogin
	AND
		hr_authority_list.entity_id = ${fld:entity_id}
)