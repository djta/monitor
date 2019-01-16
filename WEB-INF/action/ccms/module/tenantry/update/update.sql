UPDATE
	t_tenantry
SET
	tenantry_name     =${fld:tenantry_name}
	,description	 =${fld:description}
	,enabled        =${fld:enabled}
	,app_id			=${fld:app_id}
WHERE
	tenantry_id	=${fld:tuid}
