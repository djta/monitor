UPDATE hr_org_info 
SET 
	business_hours_begin = ${fld:business_hours_begin}
	,business_hours_end = ${fld:business_hours_end}
	,business_hours_type = '1'
	,contact_phone = ${fld:contact_phone}
	,province = ${fld:province}
	,city = ${fld:city}
	,district = ${fld:district}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}' 
WHERE org_id = ${fld:tuid}
