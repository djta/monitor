SELECT
    tuid
    ,group_name
    ,remark
FROM
	hr_authority_group
WHERE
  tenantry_id =${def:tenantry}
    
${filter}
${orderby}