SELECT 
	m.skill_id 
	,m.menu_id 
FROM hr_skill_menu m 
INNER JOIN hr_skill k ON k.skill_id = m.skill_id 
WHERE k.org_id = ${fld:pid} AND k.skill_id = ${fld:skill_id} 
