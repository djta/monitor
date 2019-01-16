SELECT
	lower(fi.field_code_alias) as field_code_alias
,case when ff.show_type is null then fi.show_type else ff.show_type end as show_type
FROM
	t_form f
	inner join t_form_show_field ff
	on ff.form_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
	f.tuid = ${form_id}
order by 
	ff.show_order