UPDATE
	hr_authority_group
SET
	group_name     =${fld:group_name}
	,remark	 =${fld:remark}
	,updated   ={ts '${def:timestamp}'}
	,updatedby ='${def:user}'
	,tenantry_id =${def:tenantry}
WHERE
	tuid	=${fld:tuid}
