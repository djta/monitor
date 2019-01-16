SELECT 
	m1.tuid
	,m1.menu_name 
	,m1.pid 
	,m1.uri 
FROM hr_menu m1 
WHERE m1.menu_grade = 1 AND m1.is_deleted = 0 
ORDER BY m1.show_order
