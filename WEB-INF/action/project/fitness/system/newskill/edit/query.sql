SELECT
	s.skill_id,
	s.skill_name,
	s.skill_scope,
	s.remark,
	s.skill_id as tuid,
	s.data_limit
FROM hr_skill s
WHERE s.skill_id = ${fld:id} and org_id = ${def:org}
