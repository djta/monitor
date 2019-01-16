UPDATE cc_guest_prepare 
SET 
	status = 3 
WHERE code = ${fld:code} AND org_id = ${fld:org_id}
