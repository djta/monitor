SELECT 
	m.menu_id
	,u.menu_name
	,u.uri
	,u.pid, 
	(
		CASE WHEN u.icon_path IS NOT NULL AND u.icon_path != '' 
		THEN u.icon_path ELSE '/js/project/fitness/image/SVG/index/icon_shezhi.svg' END
	) AS icon_path 
FROM hr_skill_menu m 
INNER JOIN hr_staff_skill fk ON m.skill_id = fk.skill_id 
INNER JOIN hr_staff f ON fk.user_id = f.user_id 
INNER JOIN hr_menu u ON m.menu_id = u.tuid 
WHERE f.userlogin = '${def:user}' AND u.menu_grade = 2 AND u.is_deleted = 0 
AND u.menu_type = 0 AND u.show_order >= 0 
ORDER BY u.pid, u.show_order