SELECT
    p.tenantry_id
    , p.tenantry_name
    , case 
        when p.enabled = '0' then '停用'
        when p.enabled ='1' then '启用'
	else '启用'
        end as tenantry_state
    , p.description
    ,a.description as app_name
FROM
	t_tenantry p
	inner join ${schema}s_application a on a.app_id = p.app_id
WHERE
	p.is_closed ='0'

	${filter}

	${orderby}