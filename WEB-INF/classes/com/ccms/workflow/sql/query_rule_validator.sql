SELECT
	count(*) as is_pass
FROM
	${table} 
WHERE
    ${bpk_field} = '${pk_value}'

    ${filter}
