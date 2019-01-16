SELECT 
	domain_value,
	domain_text_cn 
FROM t_domain 
WHERE namespace = ${fld:namespace} AND is_enabled = '1' 
AND (
	CASE WHEN ${fld:parent_namespace} IS NOT NULL AND ${fld:parent_namespace} != '' 
	THEN parent_namespace = ${fld:parent_namespace} ELSE TRUE END
) 
AND (
	CASE WHEN ${fld:parent_domain_value} IS NOT NULL AND ${fld:parent_domain_value} != '' 
	THEN parent_domain_value = ${fld:parent_domain_value} ELSE TRUE END
) 
ORDER BY domain_text_cn