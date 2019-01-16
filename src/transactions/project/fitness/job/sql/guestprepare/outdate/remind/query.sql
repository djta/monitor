WITH cnfg AS (
	SELECT outday, ceil(outday*1.00/4) as maxextday FROM (
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
	) AS t
) 
SELECT 
	t.code
	,t.org_id
	,t.mc
	,t.name
	,((SELECT max(m.created) FROM cc_mcchange m WHERE m.guestcode = t.code AND m.org_id = t.org_id AND m.status = 1)
		+ (SELECT concat(cg.outday,' day') FROM cnfg cg)::interval)::date AS outdate 
FROM cc_guest t 
WHERE t.status = 1 AND t.mc IS NOT NULL AND t.mc != '' AND t.org_id = ${fld:org_id} 
AND EXISTS(SELECT 1 FROM cc_mcchange m WHERE m.guestcode = t.code AND m.org_id = t.org_id AND m.status = 1) 
AND (SELECT cg.outday - cg.maxextday FROM cnfg cg) <= ('${def:date}'::date - (
	SELECT max(m.created) FROM cc_mcchange m WHERE m.guestcode = t.code AND m.org_id = t.org_id AND m.status = 1
)::date) 
AND (SELECT cg.outday FROM cnfg cg) > ('${def:date}'::date - (
	SELECT max(m.created) FROM cc_mcchange m WHERE m.guestcode = t.code AND m.org_id = t.org_id AND m.status = 1
)::date) 

