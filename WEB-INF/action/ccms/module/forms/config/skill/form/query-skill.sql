select 
	role_id as skill_id
	,rolename as skill_name
from 
	${schema}s_role
where
	app_id = (select app_id from t_tenantry where tenantry_id = ${def:tenantry})
and
	is_system = '0'
order by 
	skill_name