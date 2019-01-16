SELECT
	${value_field_code} as domain_value,
	${show_field_code} as domain_text
FROM
	${table_name}
WHERE
	${parent_field_code} = ${fld:parent_value}
order by 
	${show_field_code}
