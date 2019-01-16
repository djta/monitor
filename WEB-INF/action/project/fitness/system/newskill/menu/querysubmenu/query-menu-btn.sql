SELECT 
	b.tuid
	,b.menu_id 
	,b.btn_id
	,b.btn_name 
FROM hr_menu_btn b 
WHERE b.is_deleted = 0
