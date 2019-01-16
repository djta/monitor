SELECT 
	q.menu_id
	,m.menu_name
	,m.uri
FROM hr_staff_quickmenu q 
INNER JOIN hr_menu m ON q.menu_id = m.tuid 
WHERE userlogin = '${def:user}' 
AND q.quick_type = 1 AND m.is_deleted = 0 
