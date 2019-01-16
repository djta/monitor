SELECT 
	(SELECT name FROM hr_staff WHERE userlogin = '${def:user}') AS staffname
	,(
		SELECT k.skill_name FROM hr_skill k 
		INNER JOIN hr_staff_skill sk ON k.skill_id = sk.skill_id 
		WHERE sk.userlogin = '${def:user}' 
		LIMIT 1
	) AS rolename
	,(SELECT org_name FROM hr_org WHERE org_id = ${def:org}) AS orgname
