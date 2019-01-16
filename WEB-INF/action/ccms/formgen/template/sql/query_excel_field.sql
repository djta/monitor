SELECT
	lower(fi.field_code_alias) as colname
	,fi.field_name_cn  as  field_name_cn
	,fi.field_name_en  as  field_name_en
FROM
	t_form f
	inner join t_form_excel_field ff
	on ff.form_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
	inner join t_table t
	on fi.table_id = t.tuid
WHERE
	f.tuid = ${form_id}

order by 
	ff.show_order