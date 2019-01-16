SELECT 
	DISTINCT m.menu_id
	,u.pid AS root_id
	,u.pid
	,u.menu_path
	,u.menu_grade
	,u.icon_path
	,u.menu_name
	,u.uri
	,u.show_order
FROM hr_skill_menu m 
INNER JOIN hr_staff_skill fk ON m.skill_id = fk.skill_id 
INNER JOIN hr_staff f ON fk.user_id = f.user_id 
INNER JOIN hr_menu u ON m.menu_id = u.tuid 
WHERE u.pid = ${fld:menu_id} 
AND f.userlogin = '${def:user}' AND u.menu_grade = 3 AND u.is_deleted = 0 
AND u.menu_type = 0 AND u.show_order >= 0 
ORDER BY u.pid, u.show_order
