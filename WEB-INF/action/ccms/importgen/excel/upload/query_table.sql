SELECT
	${bpk_field} as tuid
FROM
	${table_schema}.${table}
where
	${fk_fld_name} = '${value}'