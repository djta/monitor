UPDATE cc_ptrest 
SET 
	ptleftcount = ptleftcount - 1 
WHERE code = ${fld:ptrestcode} AND org_id = ${fld:org_id}
