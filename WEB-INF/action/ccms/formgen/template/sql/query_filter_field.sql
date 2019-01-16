SELECT
	'__page_size__' as field_code_alias
	,'1' as show_type
	,'__page_size__' as field_code
	,'${fld:__page_size__}' as colname_mark
	,'' as field_name_cn
	,'' as field_name_en
	,'32' as field_length
	,'1' as is_required
	,'' as format_mark
	,'integer' as field_type
	,'' as table_code
	,99 as show_order
FROM
	dual
UNION

SELECT
	lower(fi.field_code_alias) as field_code_alias
	,case when ff.show_type is null then fi.show_type else ff.show_type end as show_type
	,lower(fi.field_code)	as	field_code
	,concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'_filter}')	as  colname_mark
	,fi.field_name_cn  as  field_name_cn
	,fi.field_name_en  as  field_name_en
	,fi.field_length
	,ff.is_mandatory as	is_required
	,fi.format_mark
	,lower(fi.field_type) as field_type
	,case when fi.is_virtual_type='0' then concat(t.table_code,'.') else '' end as table_code
	,ff.show_order
FROM
	t_form f
	inner join t_form_filter_field ff
	on ff.form_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
	inner join t_table t
	on fi.table_id = t.tuid
WHERE
	f.tuid = ${form_id}
order by 
	show_order