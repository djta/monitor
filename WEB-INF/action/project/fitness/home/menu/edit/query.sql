SELECT 
	tuid,
	pid,
	menu_path,
	menu_grade,
	icon_path,
	icon_path2,
	uri,
	menu_type,
	menu_name,
	show_order,
	created,
	createdby 
FROM hr_menu 
WHERE tuid = ${fld:id}
