UPDATE
	hr_authority
SET
	authority_name     =${fld:authority_name}
	,remark	 =${fld:remark}
	,updated   ={ts '${def:timestamp}'}
	,updatedby ='${def:user}'
	,tenantry_id =${def:tenantry}
WHERE
	tuid	=${fld:tuid}
