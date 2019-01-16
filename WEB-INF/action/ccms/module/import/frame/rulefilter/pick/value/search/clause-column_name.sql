and
	(
		b.col_name like ${fld:column_name}
		or
		lower(b.field_code) like lower(${fld:column_name})
	)
