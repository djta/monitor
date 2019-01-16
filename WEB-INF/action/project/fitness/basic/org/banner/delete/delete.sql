UPDATE hr_org_banner  
SET 
	status = 0
	,updatedby = '${def:user}'
	,updated = {ts '${def:timestamp}'}
WHERE tuid = ${fld:id}
