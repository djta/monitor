SELECT
	t.team_id,
	t.team_name,
	skill_scope,
	t.leader_id, 
	t.data_limit,
	
(select  string_agg(hr_staff.name,',') from hr_staff
LEFT JOIN hr_team_staff  ON hr_team_staff.userlogin = hr_staff.userlogin
where t.team_id= hr_team_staff.team_id) as member,

(select  string_agg(hr_team_staff.user_id::VARCHAR,',') from hr_staff
LEFT JOIN hr_team_staff  ON hr_team_staff.userlogin = hr_staff.userlogin
where t.team_id= hr_team_staff.team_id) as member_tuid
	
FROM hr_team t 
LEFT JOIN hr_team_staff ts ON t.team_id = ts.team_id 
LEFT JOIN hr_staff s ON ts.userlogin = s.userlogin 
and t.org_id='${def:org}'
WHERE t.team_id = ${fld:team_id}
