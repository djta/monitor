DELETE FROM cc_customer c 
WHERE code IN (
	SELECT customercode FROM cc_contract WHERE code = ${fld:code} AND org_id = ${fld:org_id} 
	AND contracttype = 0 AND type IN (0,5) 
) 
AND org_id = ${fld:org_id} 
AND NOT EXISTS(
	SELECT 1 FROM cc_card d WHERE d.customercode = c.code AND d.org_id = c.org_id
)
