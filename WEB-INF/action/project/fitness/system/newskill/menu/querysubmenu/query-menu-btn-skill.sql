SELECT 
	b.tuid
	,b.menu_btn_id 
FROM hr_skill_menu_btn b 
WHERE b.skill_id = ${fld:skill_id}
