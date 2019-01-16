SELECT
	distinct 
	t.userlogin as user_id,
	t.name
FROM hr_staff_skill s
inner join hr_staff t on s.userlogin = t.userlogin
WHERE s.skill_id in (/**${fld:id}*/)
ORDER BY
	t.name