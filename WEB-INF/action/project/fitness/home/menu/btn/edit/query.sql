SELECT 
	tuid,
	btn_name,
	btn_id,
	uri,
	created,
	createdby 
FROM hr_menu_btn 
WHERE tuid = ${fld:id}
