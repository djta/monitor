UPDATE cc_card 
SET 
	isgoon = -1 
WHERE code = ${fld:code} AND org_id = ${fld:org_id} AND isgoon = 0 
