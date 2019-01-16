SELECT
    fi.field_code as field_code
    ,fi.field_code_alias
    ,fi.field_name_${def:locale} as field_name
    ,lower(fi.field_type) as field_type
    ,fi.is_mandatory as	is_required	/*是否必填项*/
    ,field_length
    ,fi.format_mark			/*显示格式*/
    ,nvl(ff.show_type,fi.show_type) as show_type
FROM
	t_report f
	inner join t_report_filter_field ff
	on ff.report_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
    f.tuid = ${fld:report_id}

order by ff.show_order