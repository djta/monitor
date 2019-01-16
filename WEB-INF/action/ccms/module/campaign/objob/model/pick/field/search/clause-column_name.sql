and
	(
		f.field_name_${def:locale} like ${fld:column_name}
		or
		lower(f.field_code) like lower(${fld:column_name})
	)
