SELECT
	s.skill_id,
	s.skill_name,
	s.remark,
	s.skill_id as tuid
FROM hr_skill s
WHERE s.skill_id = ${fld:id}
