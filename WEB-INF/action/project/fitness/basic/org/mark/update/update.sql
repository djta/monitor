update hr_org_info 
set
	coordinate = ${fld:coordinate}
	,address = ${fld:address}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}' 
where org_id = ${fld:org_id}