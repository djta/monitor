UPDATE cc_customer 
SET 
	user_id = currval('security.seq_user') 
WHERE code = ${fld:code} 
AND org_id = ${fld:org_id} 
