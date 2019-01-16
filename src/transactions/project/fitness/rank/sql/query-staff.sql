SELECT 
	user_id
	,name
	,userlogin 
	,org_id 
FROM hr_staff 
WHERE org_id = ${def:org} AND is_member = 0