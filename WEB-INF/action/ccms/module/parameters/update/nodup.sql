SELECT
	1
FROM
	t_domain
WHERE
	domain_value = ${fld:domain_value}
and 
	namespace = ${fld:namespace}
and 
	subject_id =  ${fld:subject_id}
and 
	tuid <> ${fld:tuid}