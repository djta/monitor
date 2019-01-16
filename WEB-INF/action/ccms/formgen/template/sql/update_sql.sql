UPDATE
	${table}
SET
	${field}

	,snapshot = snapshot+1
WHERE
	${bpk_field} = ${fld:__pk_value__}