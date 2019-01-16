SELECT
	description
FROM
	(
		SELECT DISTINCT
			namespace AS description
		FROM
			t_domain
		WHERE
			1 = 1 
		${filter}
	) t 
		${orderby}