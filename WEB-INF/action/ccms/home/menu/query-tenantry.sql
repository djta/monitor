select
	(select tenantry_name from t_tenantry where tenantry_id = ${def:tenantry}) as tenantry_name
	,(select b.org_name from hr_staff a inner join hr_org b on a.org_id = b.org_id and a.userlogin= '${def:user}') as org_name
from
	dual