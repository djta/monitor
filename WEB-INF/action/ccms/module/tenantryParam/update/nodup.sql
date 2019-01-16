SELECT
	1
FROM
	t_tenantry_domain
WHERE
	domain_value = ${fld:domain_value}
and 
	namespace = ${fld:namespace}
and 
	tenantry_id =  ${def:tenantry}
and 
	tuid <> ${fld:tuid}