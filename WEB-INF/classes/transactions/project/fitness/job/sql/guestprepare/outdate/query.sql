WITH cnfg AS (
	SELECT  
		COALESCE((
			SELECT c1.param_value FROM cc_config c1 
			WHERE category = 'GuestOutdate' AND c1.org_id = (
				case when NOT EXISTS(SELECT 1 FROM cc_config c2 WHERE c2.org_id = ${fld:org_id} AND c2.category = c1.category) 
				THEN (SELECT org_id FROM hr_org WHERE (pid IS NULL OR pid = 0)) 
				ELSE ${fld:org_id} END
			) LIMIT 1
		),'30')::INTEGER as outday
	FROM dual
) 
SELECT 
	t.code
	,t.org_id
	,t.mc 
FROM cc_guest t 
WHERE t.org_id = ${fld:org_id} AND t.status = 1 
AND EXISTS(SELECT 1 FROM cc_mcchange m WHERE m.guestcode = t.code AND m.org_id = t.org_id AND m.status = 1) 
AND (
	(SELECT cg.outday FROM cnfg cg) < '${def:date}'::date - (
		SELECT max(m.created) FROM cc_mcchange m WHERE m.guestcode = t.code AND m.org_id = t.org_id AND m.status = 1
	)::date 
)
