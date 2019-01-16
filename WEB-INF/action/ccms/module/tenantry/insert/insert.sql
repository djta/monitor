INSERT	INTO
t_tenantry
(
	tenantry_id
	, tenantry_name
	, description
	, enabled
	, is_closed
	, app_id
)
VALUES
(
	${seq:nextval@seq_tenantry}
	,${fld:tenantry_name}
	,${fld:description}
	,${fld:enabled}
	,'0'
	,${fld:app_id}
)