SELECT
    p.tenantry_id
    ,p.tenantry_name
    ,p.enabled
    ,p.description
    ,p.app_id
FROM
	t_tenantry p
WHERE
	p.tenantry_id=${fld:id}
