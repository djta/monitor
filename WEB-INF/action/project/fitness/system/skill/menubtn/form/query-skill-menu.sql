SELECT 
	k.skill_id,
	k.menu_id,
	m.menu_name 
FROM hr_skill_menu k 
INNER JOIN hr_menu m ON k.menu_id = m.tuid 
WHERE k.skill_id = ${fld:skill_id} AND m.is_deleted = 0 
AND m.uri IS NOT NULL AND m.uri != '' 
