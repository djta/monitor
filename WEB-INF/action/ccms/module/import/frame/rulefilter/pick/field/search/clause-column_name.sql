and
	(
		t.field_name_${def:locale} like ${fld:column_name}
		or
		lower(t.field_code) like lower(${fld:column_name})
	)
