INSERT INTO
	hr_org
(
    org_id
    ,org_name
    ,pid
    ,org_path
    ,tenantry_id
)
VALUES
(
      ${fld:org_id}
    , ${fld:org_name}
    , ${fld:pid}
  	, ${org_path}
  	,${def:tenantry}
)	