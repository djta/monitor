SELECT 
	c.param_value 	/** 0按岗位 1按分组 */
FROM cc_config c 
WHERE c.category = 'Taskgoal' AND c.org_id = (
	CASE WHEN NOT EXISTS(SELECT 1 FROM cc_config c2 where c2.org_id = ${fld:org_id} AND c2.category = c.category) 
	THEN (SELECT org_id FROM hr_org WHERE pid IS NULL OR pid = 0) ELSE ${fld:org_id} END
) 