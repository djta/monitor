SELECT
	case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end ='0' then lower(fi.field_code) else lower(fi.field_code_alias) end	as field_code
FROM
	t_form f
	inner join t_form_show_field ff
	on ff.form_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
	f.tuid = ${form_id}
AND
case when ff.show_type is null then fi.show_type else ff.show_type end='1'
and
case when ff.is_cascade_combo is null then '0' else ff.is_cascade_combo end = '1'