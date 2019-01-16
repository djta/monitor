SELECT
    tuid
    ,authority_name
    ,remark
FROM
	hr_authority
WHERE
    tenantry_id=${def:tenantry}
    
${filter}
${orderby}