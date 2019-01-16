SELECT code, org_id 
FROM cc_contract 
WHERE status = 1 AND org_id = ${fld:org_id} AND ('${def:date}'::date - createdate) > COALESCE((
	SELECT c1.param_value FROM cc_config c1 
	WHERE category = 'NOCollectionContract' AND c1.org_id = (
		case when NOT EXISTS(SELECT 1 FROM cc_config c2 WHERE c2.org_id = ${fld:org_id} AND c2.category = c1.category) 
		THEN (SELECT org_id FROM hr_org WHERE (pid IS NULL OR pid = 0)) 
		ELSE ${fld:org_id} END
	) LIMIT 1
), '7')::integer
