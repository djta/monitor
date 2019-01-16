SELECT 
	k.skill_id,
	k.menu_id,
	b.tuid AS menu_btn_id,
	b.btn_name,
	b.btn_id 
FROM hr_skill_menu k 
INNER JOIN hr_menu_btn b ON k.menu_id = b.menu_id 
WHERE k.skill_id = ${fld:skill_id} AND b.is_deleted = 0
