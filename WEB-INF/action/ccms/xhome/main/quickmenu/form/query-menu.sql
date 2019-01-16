SELECT 
	DISTINCT km.menu_id, m.menu_name, m.uri, m.pid 
FROM hr_staff s 
INNER JOIN hr_staff_skill sk ON sk.user_id = s.user_id 
INNER JOIN hr_skill_menu km ON sk.skill_id = km.skill_id
INNER JOIN hr_menu m ON km.menu_id = m.tuid 
WHERE s.userlogin = '${def:user}' 
AND m.is_deleted = 0
