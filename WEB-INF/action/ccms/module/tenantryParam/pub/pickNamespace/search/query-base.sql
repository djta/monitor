SELECT
	description,
	domain_text_cn,
	domain_value
FROM
	(
		SELECT DISTINCT
			namespace AS description,
			domain_text_cn,
			domain_value
		FROM
			t_tenantry_domain
		WHERE
			tenantry_id = ${def:tenantry}
		${filter}
	) t 
		${orderby}