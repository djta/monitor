SELECT 
	d.code, d.cardcode, d.customercode, d.org_id 
FROM cc_savestopcard d 
WHERE d.status =  1 
AND d.startdate IS NOT NULL AND d.prestopdays IS NOT NULL  
AND d.startdate + d.prestopdays <= '${def:date}'::date 
/**AND d.enddate <= '${def:date}'::date*/
