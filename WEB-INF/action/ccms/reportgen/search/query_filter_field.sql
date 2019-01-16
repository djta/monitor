SELECT
    lower(fi.field_code)	as	field_code
    ,concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'_filter}')	as  colname_mark
    ,fi.field_code_alias
    ,fi.field_name_${def:locale}  as  field_name
    ,lower(fi.field_type)	as	field_type
    ,ff.is_mandatory		as	is_required
    ,field_length
    ,fi.format_mark	
  ,case when ff.show_type is null then fi.show_type else ff.show_type end as show_type
    ,fi.is_virtual_type
FROM
	t_report f
	inner join t_report_filter_field ff
	on ff.report_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
    f.tuid = ${fld:report_id}

order by ff.show_order