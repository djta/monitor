SELECT 
	g.org_id
	,g.org_name 
FROM hr_org g 
WHERE g.org_id = ${def:org} 

UNION 

SELECT 
	g.org_id
	,g.org_name 
FROM hr_org g 
WHERE EXISTS(
	SELECT 1 FROM hr_staff_org so 
	INNER JOIN hr_staff f ON so.user_id = f.user_id 
	WHERE f.userlogin = '${def:user}' AND so.org_id = g.org_id
)
