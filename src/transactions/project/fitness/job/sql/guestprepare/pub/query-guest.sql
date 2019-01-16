WITH cnfg AS (
	SELECT  
		COALESCE((
			SELECT c1.param_value FROM cc_config c1 
			WHERE category = 'GuestMaxFPCount' AND c1.org_id = (
				case when NOT EXISTS(SELECT 1 FROM cc_config c2 WHERE c2.org_id = ${fld:org_id} AND c2.category = c1.category) 
				THEN (SELECT org_id FROM hr_org WHERE (pid IS NULL OR pid = 0)) 
				ELSE ${fld:org_id} END
			) LIMIT 1
		),'30')::INTEGER as maxfpcount
	FROM dual 
) 
SELECT 
	t.code AS guestcode, NULL::VARCHAR AS customercode, t.org_id, t.mc 
FROM cc_guest t 
WHERE t.org_id = ${fld:org_id} 
AND (SELECT cg.maxfpcount FROM cnfg cg) < (
	SELECT COUNT(1) FROM cc_mcchange e WHERE e.guestcode = t.code AND e.org_id = t.org_id AND e.status = 1
) 
AND t.public = 0 AND t.status = 80 
