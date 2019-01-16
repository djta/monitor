SELECT 
	tuid, coordinate, address 
FROM hr_org_info 
WHERE org_id = ${fld:org_id} 
