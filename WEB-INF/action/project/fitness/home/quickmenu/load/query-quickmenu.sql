SELECT 
	q.menu_id
	,m.tuid
	,m.menu_name
	,m.uri
	--(case when (select count(1) from hr_menu where pid=m.tuid)>0 
		--then (select uri from hr_menu where pid=m.tuid order by show_order limit 1) 
		--else m.uri end) as uri
	,(
		CASE WHEN m.icon_path IS NOT NULL AND m.icon_path != '' 
		THEN m.icon_path ELSE '/js/project/fitness/image/SVG/index/icon_shezhi.svg' END
	) AS icon_path
FROM hr_staff_quickmenu q 
INNER JOIN hr_menu m ON q.menu_id = m.tuid 
WHERE userlogin = '${def:user}' 
AND q.quick_type = 0 AND m.is_deleted = 0 
order by m.tuid desc

