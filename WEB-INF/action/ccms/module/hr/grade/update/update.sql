UPDATE
	hr_org_grade
SET
	grade_code = ${fld:grade_code}
	,grade_name = ${fld:grade_name}
WHERE
	tuid = ${fld:tuid}
