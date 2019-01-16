SELECT 
	tuid,
	concat(menu_name,(CASE WHEN show_order < 0 THEN '（不显示）' ELSE '' END)) AS menu_name,
	pid 
FROM hr_menu 
WHERE is_deleted = 0
ORDER BY pid, show_order
