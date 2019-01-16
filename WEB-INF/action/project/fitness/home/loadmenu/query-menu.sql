select array_to_json(array_agg(row_to_json(t1)))::varchar AS menustr FROM (
	SELECT 
		DISTINCT m.menu_id
		,u.pid
		,u.menu_path
		,u.menu_grade
		,u.icon_path
		,u.icon_path2
		,u.menu_name
		,u.uri
		,u.show_order
		,(
			SELECT array_to_json(array_agg(row_to_json(t2))) FROM (
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
				WHERE f2.userlogin = '${def:user}' AND u2.menu_grade = 2 AND u2.is_deleted = 0 
				AND u2.pid = m.menu_id 
				AND u2.menu_type = 0 AND u2.show_order >= 0 
				ORDER BY u2.pid, u2.show_order
			) AS t2
		)::varchar AS submenu 
	FROM hr_skill_menu m 
	INNER JOIN hr_staff_skill fk ON m.skill_id = fk.skill_id 
	INNER JOIN hr_staff f ON fk.user_id = f.user_id 
	INNER JOIN hr_menu u ON m.menu_id = u.tuid 
	WHERE f.userlogin = '${def:user}' AND u.menu_grade = 1 AND u.is_deleted = 0 
	AND u.menu_type = 0 AND u.show_order >= 0 
	ORDER BY u.pid, u.show_order
) AS t1
