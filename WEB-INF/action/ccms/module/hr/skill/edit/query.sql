SELECT
	s.skill_id,
	s.skill_name,
	s.skill_scope,
	s.remark,
	s.skill_id as tuid
	,s.is_default
FROM
	hr_skill s
WHERE
	s.skill_id = ${fld:id}
