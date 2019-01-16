SELECT 
	m2.tuid 
	,m2.menu_name 
	,m2.pid 
	,m2.uri 
	,(
		SELECT COUNT(1) FROM hr_menu m3 
		WHERE m2.tuid = m3.pid AND m3.menu_grade = 3 AND m3.is_deleted = 0 
	) AS subcount 
FROM hr_menu m2 
WHERE m2.pid = ${fld:rootid} AND m2.menu_grade = 2 AND m2.is_deleted = 0 
ORDER BY m2.show_order
