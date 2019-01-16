SELECT
	t.team_id as tuid,
	t.team_name,
	s.name as leader_name,
	t.leader_id,
	t.remark
FROM
	hr_team t
	left join hr_staff s
	on s.userlogin = t.leader_id
WHERE
	t.team_id = ${fld:id}