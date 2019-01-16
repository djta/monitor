update 
	hr_org_info
set
	coordinate = ${fld:coordinate}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}'
where
	org_id = ${fld:org_id}