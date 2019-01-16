UPDATE hr_org_banner 
SET 
	bannername = ${fld:bannername}
	,attachid = ${fld:upload_id}
	,linkurl = ${fld:linkurl}
	,showorder = ${fld:showorder} 
	,updatedby = '${def:user}'
	,updated = {ts '${def:timestamp}'} 
WHERE tuid = ${fld:tuid}
