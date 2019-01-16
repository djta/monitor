SELECT 
	DISTINCT m2.menu_id
	,u2.pid
	,u2.menu_path
	,u2.menu_grade
	,u2.icon_path
	,u2.icon_path2
	,u2.menu_name
	,u2.uri
	,u2.show_order
FROM hr_skill_menu m2 
INNER JOIN hr_staff_skill fk2 ON m2.skill_id = fk2.skill_id 
INNER JOIN hr_staff f2 ON fk2.user_id = f2.user_id 
INNER JOIN hr_menu u2 ON m2.menu_id = u2.tuid 
WHERE f2.userlogin = '${def:user}' AND u2.menu_grade = 3 AND u2.is_deleted = 0 
AND u2.pid = ${fld:menuid} 
AND u2.menu_type = 0 AND u2.show_order >= 0 
ORDER BY u2.pid, u2.show_order
