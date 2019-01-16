SELECT
	config.param_value as sitedate_value
FROM
	cc_config config
WHERE
	config.category = 'Sitedate'
	and config.org_id = (case when 
	not exists(select 1 from cc_config c where c.org_id = ${fld:org_id} and c.category=config.category) 
	then (select org_id from hr_org where pid is null or pid = 0) else ${fld:org_id} end)