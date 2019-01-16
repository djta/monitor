UPDATE hr_org_info 
SET 
	business_hours_begin = ${fld:business_hours_begin}
	,business_hours_end = ${fld:business_hours_end}
	,business_hours_type = ${fld:business_hours_type}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}' 
WHERE org_id = ${fld:tuid}
