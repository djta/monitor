SELECT 
	m3.tuid
	,m3.uri
	,m3.menu_name
	,m3.pid
	,m3.show_order
	,m2.tuid AS parent_tuid
	,m2.menu_name AS parent_menu_name
	,m2.pid AS parent_pid
	,m2.uri AS parent_uri
FROM hr_menu m2 
LEFT JOIN hr_menu m3 ON m2.tuid = m3.pid AND m3.is_deleted = 0 
WHERE m2.pid = ${fld:rootid} AND m2.menu_grade = 2 AND m2.is_deleted = 0 
ORDER BY m2.show_order, m3.show_order 
